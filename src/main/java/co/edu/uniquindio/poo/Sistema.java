package co.edu.uniquindio.poo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Clase principal del sistema QuindíoExpress. Gestiona el registro,
 * consulta, clasificación y despacho de paquetes durante una jornada.
 *
 * <p>Cada necesidad del sistema se resuelve con una estructura de datos
 * específica, elegida según su comportamiento y complejidad:</p>
 *
 * <ul>
 *   <li><b>Orden de registro:</b> {@code List<Paquete>} con {@code ArrayList}.</li>
 *   <li><b>Búsqueda por código:</b> {@code List<Paquete>} ordenada + búsqueda binaria recursiva.</li>
 *   <li><b>Municipios sin repetir:</b> {@code Set<Municipio>} con {@code HashSet}.</li>
 *   <li><b>Municipios ordenados:</b> {@code TreeSet<Municipio>}.</li>
 *   <li><b>Municipio → paquetes:</b> {@code Map<Municipio, List<Paquete>>}.</li>
 *   <li><b>FIFO estricto:</b> {@code Queue<Paquete>} con {@code ArrayDeque}.</li>
 *   <li><b>Despacho por prioridad:</b> {@code PriorityQueue<Paquete>}.</li>
 *   <li><b>Historial de entregas:</b> {@link ListaEnlazada} propia.</li>
 * </ul>
 *
 * @author Santiago
 * @version 1.0
 * @see Paquete
 * @see Repartidor
 * @see Comparators
 * @see ListaEnlazada
 */
public class Sistema {

    /** Prefijo de los códigos de paquete. */
    private static final String PREFIJO_PAQUETE = "PQ";

    /** Prefijo de las identificaciones de repartidor. */
    private static final String PREFIJO_REPARTIDOR = "RP";

    /** Cantidad máxima de códigos únicos que se pueden generar (000 a 999). */
    private static final int MAX_CODIGOS = 1000;

    // ==================== CÓDIGOS ÚNICOS ====================

    /** Códigos de paquetes ya usados. */
    private final Set<String> codigosPaqueteUsados;

    /** Identificaciones de repartidores ya usadas. */
    private final Set<String> codigosRepartidorUsados;

    /** Generador aleatorio para los códigos. */
    private final Random random;

    // ==================== PAQUETES ====================

    /** Paquetes en orden exacto de registro. */
    private final List<Paquete> paquetesRegistrados;

    /** Paquetes ordenados por código (para búsqueda binaria). */
    private final List<Paquete> paquetesOrdenadosPorCodigo;

    /** Paquetes pendientes de despacho en orden FIFO. */
    private final Queue<Paquete> colaFifo;

    /** Paquetes pendientes ordenados por prioridad de despacho. */
    private final PriorityQueue<Paquete> colaPrioridad;

    /** Historial de entregas en orden cronológico (lista propia). */
    private final ListaEnlazada<Paquete> historialEntregas;

    // ==================== MUNICIPIOS ====================

    /** Municipios con al menos un paquete registrado (sin duplicados). */
    private final Set<Municipio> municipiosConPaquetes;

    /** Municipios con al menos un paquete registrado, ordenados alfabéticamente. */
    private final TreeSet<Municipio> municipiosOrdenados;

    /** Relación municipio → lista de paquetes destinados a él. */
    private final Map<Municipio, List<Paquete>> paquetesPorMunicipio;

    // ==================== REPARTIDORES ====================

    /** Repartidores registrados en el sistema. */
    private final List<Repartidor> repartidores;

    // ==================== CONSTRUCTOR ====================

    /**
     * Crea un sistema vacío, listo para operar una jornada.
     */
    public Sistema() {
        this.codigosPaqueteUsados = new HashSet<>();
        this.codigosRepartidorUsados = new HashSet<>();
        this.random = new Random();

        this.paquetesRegistrados = new ArrayList<>();
        this.paquetesOrdenadosPorCodigo = new ArrayList<>();
        this.colaFifo = new ArrayDeque<>();
        this.colaPrioridad = new PriorityQueue<>(Comparators.PARA_DESPACHO);
        this.historialEntregas = new ListaEnlazada<>();

        this.municipiosConPaquetes = new HashSet<>();
        this.municipiosOrdenados = new TreeSet<>();
        this.paquetesPorMunicipio = new HashMap<>();

        this.repartidores = new ArrayList<>();
    }

    // ==================== GENERACIÓN DE CÓDIGOS ====================

    /**
     * Genera un código único para un paquete con formato {@code PQ###}.
     *
     * <p>Complejidad: O(1) promedio, porque el {@code Set} verifica
     * duplicados en tiempo constante.</p>
     *
     * @return un código único no usado previamente
     * @throws IllegalStateException si ya se generaron 1000 códigos
     */
    private String generarCodigoPaquete() {
        return generarCodigoUnico(PREFIJO_PAQUETE, codigosPaqueteUsados);
    }

    /**
     * Genera una identificación única para un repartidor con formato {@code RP###}.
     *
     * @return una identificación única no usada previamente
     * @throws IllegalStateException si ya se generaron 1000 identificaciones
     */
    private String generarCodigoRepartidor() {
        return generarCodigoUnico(PREFIJO_REPARTIDOR, codigosRepartidorUsados);
    }

    /**
     * Método genérico que genera un código único con el prefijo indicado,
     * verificando que no esté repetido en el conjunto dado.
     *
     * @param prefijo prefijo del código ({@code "PQ"} o {@code "RP"})
     * @param usados conjunto de códigos ya usados
     * @return un código único
     * @throws IllegalStateException si se agotaron los códigos disponibles
     */
    private String generarCodigoUnico(String prefijo, Set<String> usados) {
        if (usados.size() >= MAX_CODIGOS) {
            throw new IllegalStateException(
                    "Se agotaron los códigos disponibles con prefijo " + prefijo
            );
        }
        String codigo;
        do {
            int numero = random.nextInt(MAX_CODIGOS);
            codigo = String.format("%s%03d", prefijo, numero);
        } while (usados.contains(codigo));
        usados.add(codigo);
        return codigo;
    }

    // ==================== REGISTRO DE PAQUETES ====================

    /**
     * Registra un nuevo paquete en el sistema.
     *
     * <p>El sistema:</p>
     * <ol>
     *   <li>Genera un código único para el paquete.</li>
     *   <li>Lo agrega al final de la lista de registro (orden cronológico).</li>
     *   <li>Lo inserta en la posición correcta de la lista ordenada por código
     *       (para futuras búsquedas binarias).</li>
     *   <li>Lo agrega a la cola FIFO y a la cola de prioridad.</li>
     *   <li>Actualiza el registro de municipios (HashSet + TreeSet + Map).</li>
     * </ol>
     *
     * @param destino municipio de destino, no nulo
     * @param peso peso en kilogramos, mayor que cero
     * @param prioridad prioridad entre 1 y 5
     * @param tiempoEstimado tiempo estimado en minutos, positivo
     * @return el paquete creado y registrado
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Paquete registrarPaquete(Municipio destino,
                                    float peso,
                                    int prioridad,
                                    int tiempoEstimado) {

        String codigo = generarCodigoPaquete();
        Paquete paquete = new Paquete(codigo, destino, peso, prioridad, tiempoEstimado);

        // 1. Orden exacto de registro (#1)
        paquetesRegistrados.add(paquete);

        // 2. Orden natural por código, para búsqueda binaria (#2)
        insertarOrdenadoPorCodigo(paquete);

        // 3. Cola FIFO estricta (#6)
        colaFifo.offer(paquete);

        // 4. Cola de prioridad para despacho (#7)
        colaPrioridad.offer(paquete);

        // 5. Registro de municipios (#3, #4, #5)
        registrarMunicipioDePaquete(paquete);

        return paquete;
    }

    /**
     * Inserta el paquete en la posición correcta de la lista ordenada
     * por código, manteniendo el orden ascendente.
     *
     * <p>La posición de inserción se calcula con una <b>búsqueda binaria
     * recursiva</b>, aprovechando que la lista ya está ordenada. Esto
     * mantiene la coherencia: tanto la inserción como la búsqueda usan
     * el mismo enfoque O(log n).</p>
     *
     * <p><b>Precondición:</b> {@code paquetesOrdenadosPorCodigo} debe
     * estar ordenada por código ascendente.</p>
     *
     * @param paquete paquete a insertar
     */
    private void insertarOrdenadoPorCodigo(Paquete paquete) {
        int posicion = encontrarPosicionInsercion(
                paquete.getCodigo(),
                0,
                paquetesOrdenadosPorCodigo.size() - 1
        );
        paquetesOrdenadosPorCodigo.add(posicion, paquete);
    }

    /**
     * Búsqueda binaria recursiva que calcula la posición donde debe
     * insertarse un código para mantener la lista ordenada.
     *
     * <p>Si el código no existe (caso normal en este sistema, porque
     * el {@code Sistema} genera códigos únicos), retorna el índice
     * donde debería quedar para que la lista siga ordenada.</p>
     *
     * @param codigo código a insertar
     * @param inicio índice inferior del rango actual
     * @param fin índice superior del rango actual
     * @return índice donde debe insertarse el código
     */
    private int encontrarPosicionInsercion(String codigo, int inicio, int fin) {
        // CASO BASE 1: rango vacío → la posición es 'inicio'
        if (inicio > fin) {
            return inicio;
        }

        int medio = inicio + (fin - inicio) / 2;
        String codigoMedio = paquetesOrdenadosPorCodigo.get(medio).getCodigo();
        int comparacion = codigoMedio.compareTo(codigo);

        if (comparacion < 0) {
            // El código del medio es menor → buscar en la mitad derecha
            return encontrarPosicionInsercion(codigo, medio + 1, fin);
        } else if (comparacion > 0) {
            // El código del medio es mayor → buscar en la mitad izquierda
            return encontrarPosicionInsercion(codigo, inicio, medio - 1);
        } else {
            // CASO BASE 2: el código ya existe → retornar su posición
            return medio;
        }
    }

    /**
     * Registra el municipio destino del paquete en las estructuras
     * correspondientes.
     *
     * @param paquete paquete cuyo destino se va a registrar
     */
    private void registrarMunicipioDePaquete(Paquete paquete) {
        Municipio destino = paquete.getDestino();

        // #3: Set sin duplicados
        municipiosConPaquetes.add(destino);

        // #4: TreeSet ordenado alfabéticamente
        municipiosOrdenados.add(destino);

        // #5: Map municipio → lista de paquetes
        paquetesPorMunicipio
                .computeIfAbsent(destino, k -> new ArrayList<>())
                .add(paquete);
    }

    // ==================== REGISTRO DE REPARTIDORES ====================

    /**
     * Registra un nuevo repartidor en el sistema con una identificación única.
     *
     * @param nombre nombre completo del repartidor, no nulo ni vacío
     * @param zona municipio principal donde opera
     * @return el repartidor creado y registrado
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Repartidor registrarRepartidor(String nombre, Municipio zona) {
        String identificacion = generarCodigoRepartidor();
        Repartidor repartidor = new Repartidor(identificacion, nombre, zona);
        repartidores.add(repartidor);
        return repartidor;
    }

    // ==================== GETTERS BÁSICOS ====================

    /**
     * Devuelve la cantidad de paquetes registrados en la jornada.
     * @return cantidad de paquetes
     */
    public int getCantidadPaquetesRegistrados() {
        return paquetesRegistrados.size();
    }

    /**
     * Devuelve la cantidad de repartidores registrados.
     * @return cantidad de repartidores
     */
    public int getCantidadRepartidores() {
        return repartidores.size();
    }

    // ==================== ATENCIÓN DE PAQUETES ====================

    /**
     * Atiende el siguiente paquete respetando estrictamente el orden
     * de registro (FIFO).
     *
     * <p>El paquete se elimina de todas las estructuras activas para
     * evitar que siga apareciendo en colas o listas de pendientes.</p>
     *
     * @return el paquete atendido, o {@code null} si no hay pendientes
     */
    public Paquete atenderFifo() {
        Paquete paquete = colaFifo.poll();
        if (paquete != null) {
            eliminarDeEstructurasActivas(paquete);
        }
        return paquete;
    }

    /**
     * Atiende el siguiente paquete según las reglas de prioridad:
     * <ol>
     *   <li>Mayor prioridad primero.</li>
     *   <li>Menor tiempo estimado en caso de empate.</li>
     *   <li>Código alfabéticamente menor en caso de empate final.</li>
     * </ol>
     *
     * @return el paquete atendido, o {@code null} si no hay pendientes
     */
    public Paquete atenderPorPrioridad() {
        Paquete paquete = colaPrioridad.poll();
        if (paquete != null) {
            eliminarDeEstructurasActivas(paquete);
        }
        return paquete;
    }

    /**
     * Elimina un paquete de todas las estructuras activas:
     * cola FIFO, cola de prioridad, lista de registro y lista ordenada.
     *
     * <p>No se toca el historial de entregas ni el mapa de municipios,
     * porque esa información se conserva para auditoría.</p>
     *
     * @param paquete paquete a eliminar
     */
    private void eliminarDeEstructurasActivas(Paquete paquete) {
        colaFifo.remove(paquete);
        colaPrioridad.remove(paquete);
        paquetesRegistrados.remove(paquete);
        paquetesOrdenadosPorCodigo.remove(paquete);
    }

    // ==================== ENTREGA ====================

    /**
     * Registra un paquete como entregado, agregándolo al historial.
     *
     * <p>El historial conserva estrictamente el orden de entrega. Si el
     * paquete ya había sido entregado, no se duplica.</p>
     *
     * @param paquete paquete entregado, no nulo
     * @throws IllegalArgumentException si el paquete es nulo
     */
    public void entregarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo.");
        }
        // Evitar duplicados en el historial
        if (!historialEntregas.contiene(paquete)) {
            historialEntregas.agregar(paquete);
        }
    }

    // ==================== GETTERS ====================

    /**
     * Devuelve la cantidad de paquetes pendientes en la cola FIFO.
     * @return cantidad de pendientes
     */
    public int getCantidadPendientes() {
        return colaFifo.size();
    }

    /**
     * Devuelve la cantidad de paquetes en el historial de entregas.
     * @return cantidad de entregados
     */
    public int getCantidadEntregados() {
        return historialEntregas.tamanio();
    }

    /**
     * Devuelve la cantidad de municipios con al menos un paquete registrado.
     * @return cantidad de municipios
     */
    public int getCantidadMunicipiosConPaquetes() {
        return municipiosConPaquetes.size();
    }

    /**
     * Devuelve una copia del conjunto de municipios sin duplicados.
     * @return copia defensiva del conjunto
     */
    public Set<Municipio> getMunicipiosConPaquetes() {
        return new HashSet<>(municipiosConPaquetes);
    }

    /**
     * Devuelve una copia del conjunto de municipios ordenados alfabéticamente.
     * @return copia defensiva del TreeSet
     */
    public TreeSet<Municipio> getMunicipiosOrdenados() {
        return new TreeSet<>(municipiosOrdenados);
    }

    /**
     * Devuelve los paquetes destinados a un municipio dado.
     *
     * @param municipio municipio a consultar
     * @return copia de la lista de paquetes (vacía si no hay registros)
     */
    public List<Paquete> getPaquetesPorMunicipio(Municipio municipio) {
        List<Paquete> paquetes = paquetesPorMunicipio.get(municipio);
        return paquetes == null ? new ArrayList<>() : new ArrayList<>(paquetes);
    }

    /**
     * Devuelve una copia de la lista de paquetes en orden de registro.
     * @return copia defensiva de la lista
     */
    public List<Paquete> getPaquetesRegistrados() {
        return new ArrayList<>(paquetesRegistrados);
    }

    /**
     * Devuelve una copia de la lista de repartidores.
     * @return copia defensiva de la lista
     */
    public List<Repartidor> getRepartidores() {
        return new ArrayList<>(repartidores);
    }

    // ==================== MOSTRAR (para depuración y demos) ====================

    /**
     * Imprime en consola los paquetes pendientes en orden de registro.
     */
    public void mostrarPaquetesEnOrdenDeRegistro() {
        System.out.println("=== Paquetes pendientes (orden de registro) ===");
        if (colaFifo.isEmpty()) {
            System.out.println("(No hay paquetes pendientes)");
            return;
        }
        for (Paquete p : colaFifo) {
            System.out.println(p);
        }
    }

    /**
     * Imprime en consola los paquetes ordenados por prioridad de despacho.
     */
    public void mostrarPaquetesPorPrioridadDeDespacho() {
        System.out.println("=== Paquetes pendientes (orden de despacho) ===");
        if (colaPrioridad.isEmpty()) {
            System.out.println("(No hay paquetes pendientes)");
            return;
        }
        // Se usa una copia para no consumir la cola original
        List<Paquete> copia = new ArrayList<>(colaPrioridad);
        copia.sort(Comparators.PARA_DESPACHO);
        for (Paquete p : copia) {
            System.out.println(p);
        }
    }

    /**
     * Imprime en consola el historial de entregas en orden cronológico.
     */
    public void mostrarHistorialDeEntregas() {
        System.out.println("=== Historial de entregas (orden de entrega) ===");
        if (historialEntregas.estaVacia()) {
            System.out.println("(No hay entregas registradas)");
            return;
        }
        // Recorrido usando el iterador propio de la ListaEnlazada (for-each)
        for (Paquete p : historialEntregas) {
            System.out.println(p);
        }
    }

    /**
     * Imprime en consola los municipios ordenados alfabéticamente.
     */
    public void mostrarMunicipiosOrdenados() {
        System.out.println("=== Municipios con paquetes (orden alfabético) ===");
        if (municipiosOrdenados.isEmpty()) {
            System.out.println("(No hay municipios registrados)");
            return;
        }
        for (Municipio m : municipiosOrdenados) {
            System.out.println(m);
        }
    }

    // ==================== BÚSQUEDA BINARIA RECURSIVA ====================

    /**
     * Busca un paquete por su código usando <b>búsqueda binaria recursiva</b>.
     *
     * <p><b>Precondición:</b> la lista {@code paquetesOrdenadosPorCodigo}
     * debe estar ordenada por código ascendente. Esto se garantiza
     * manteniéndola ordenada en cada inserción.</p>
     *
     * <p><b>Complejidad:</b> O(log n), porque en cada llamada se descarta
     * la mitad del rango de búsqueda.</p>
     *
     * @param codigo código del paquete a buscar (ej: "PQ105")
     * @return el paquete encontrado
     * @throws IllegalArgumentException si el código es nulo o vacío
     * @throws java.util.NoSuchElementException si no existe un paquete con ese código
     */
    public Paquete buscarPaquetePorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de búsqueda no puede ser nulo ni vacío.");
        }

        Paquete encontrado = busquedaBinariaRecursiva(
                codigo,
                0,
                paquetesOrdenadosPorCodigo.size() - 1
        );

        if (encontrado == null) {
            throw new java.util.NoSuchElementException(
                    "No existe un paquete con el código " + codigo
            );
        }
        return encontrado;
    }

    /**
     * Búsqueda binaria recursiva que localiza un paquete por código
     * dentro del rango {@code [inicio, fin]} de la lista ordenada.
     *
     * <p><b>Análisis recursivo:</b></p>
     * <ul>
     *   <li><b>Caso base (no encontrado):</b> {@code inicio > fin} → retorna {@code null}.</li>
     *   <li><b>Caso base (encontrado):</b> el código del medio coincide → retorna el paquete.</li>
     *   <li><b>Caso recursivo:</b> el código del medio es menor o mayor →
     *       se reduce el rango a la mitad y se llama recursivamente.</li>
     *   <li><b>Cómo se avanza hacia el caso base:</b> cada llamada reduce
     *       el rango a la mitad ({@code medio + 1} o {@code medio - 1}).</li>
     *   <li><b>Parámetros que cambian:</b> {@code inicio} y {@code fin}.</li>
     * </ul>
     *
     * @param codigo código buscado (constante en todas las llamadas)
     * @param inicio índice inferior del rango actual
     * @param fin índice superior del rango actual
     * @return el paquete encontrado, o {@code null} si no existe
     */
    private Paquete busquedaBinariaRecursiva(String codigo, int inicio, int fin) {
        // CASO BASE 1: rango vacío → no se encontró
        if (inicio > fin) {
            return null;
        }

        int medio = inicio + (fin - inicio) / 2;
        Paquete paqueteMedio = paquetesOrdenadosPorCodigo.get(medio);
        int comparacion = paqueteMedio.getCodigo().compareTo(codigo);

        if (comparacion == 0) {
            // CASO BASE 2: encontrado
            return paqueteMedio;
        } else if (comparacion < 0) {
            // El código del medio es menor → buscar en la mitad derecha
            return busquedaBinariaRecursiva(codigo, medio + 1, fin);
        } else {
            // El código del medio es mayor → buscar en la mitad izquierda
            return busquedaBinariaRecursiva(codigo, inicio, medio - 1);
        }
    }

    // ==================== MÉTODOS RECURSIVOS ====================

    /**
     * Calcula el peso total de todos los paquetes registrados cuyo destino
     * coincide con el municipio indicado.
     *
     * <p><b>Implementación recursiva:</b> suma los pesos uno a uno, avanzando
     * por la lista de paquetes registrados. No usa bucles, según exige el
     * enunciado para las operaciones recursivas.</p>
     *
     * <p><b>Complejidad:</b> O(n), porque se recorre toda la lista una sola vez.</p>
     *
     * @param municipio municipio a consultar
     * @return el peso total en kilogramos (0.0 si no hay paquetes para ese municipio)
     * @throws IllegalArgumentException si el municipio es nulo
     */
    public float pesoTotalPorMunicipio(Municipio municipio) {
        if (municipio == null) {
            throw new IllegalArgumentException("El municipio no puede ser nulo.");
        }
        return pesoTotalPorMunicipioRec(municipio, 0);
    }

    /**
     * Método recursivo auxiliar para calcular el peso total.
     *
     * <p><b>Análisis recursivo:</b></p>
     * <ul>
     *   <li><b>Caso base:</b> {@code indice == paquetesRegistrados.size()} →
     *       no quedan paquetes por revisar → retorna {@code 0.0f}.</li>
     *   <li><b>Caso recursivo:</b> sumar (si aplica) el peso del paquete
     *       en la posición {@code indice} y llamar con {@code indice + 1}.</li>
     *   <li><b>Cómo se avanza:</b> el índice aumenta en 1 en cada llamada.</li>
     *   <li><b>Parámetros:</b> {@code municipio} (constante) e {@code indice} (variable).</li>
     * </ul>
     *
     * @param municipio municipio a filtrar
     * @param indice posición actual en la lista
     * @return el peso total desde {@code indice} hasta el final
     */
    private float pesoTotalPorMunicipioRec(Municipio municipio, int indice) {
        // CASO BASE: no quedan paquetes por revisar
        if (indice == paquetesRegistrados.size()) {
            return 0.0f;
        }

        // CASO RECURSIVO
        Paquete actual = paquetesRegistrados.get(indice);
        float pesoActual = 0.0f;

        if (actual.getDestino() == municipio) {
            pesoActual = actual.getPeso();
        }

        return pesoActual + pesoTotalPorMunicipioRec(municipio, indice + 1);
    }

    /**
     * Cuenta cuántos paquetes registrados tienen una prioridad mayor o
     * igual a la indicada.
     *
     * <p><b>Ejemplo:</b> si las prioridades son {@code 5, 2, 4, 1, 5, 3, 4}
     * y se consulta una prioridad mínima de 4, el resultado es {@code 4}
     * (los paquetes con prioridad 5, 4, 5 y 4).</p>
     *
     * <p><b>Implementación recursiva</b> (sin bucles), según exige el enunciado.</p>
     *
     * @param prioridadMinima valor mínimo de prioridad a considerar (1 a 5)
     * @return cantidad de paquetes con prioridad &gt;= {@code prioridadMinima}
     * @throws IllegalArgumentException si la prioridad no está entre 1 y 5
     */
    public int contarPaquetesConPrioridadMinima(int prioridadMinima) {
        if (prioridadMinima < 1 || prioridadMinima > 5) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5.");
        }
        return contarPaquetesConPrioridadMinimaRec(prioridadMinima, 0);
    }

    /**
     * Método recursivo auxiliar para contar paquetes por prioridad mínima.
     *
     * <p><b>Análisis recursivo:</b></p>
     * <ul>
     *   <li><b>Caso base:</b> {@code indice == paquetesRegistrados.size()} →
     *       no quedan paquetes → retorna {@code 0}.</li>
     *   <li><b>Caso recursivo:</b> aportar 1 si el paquete cumple la
     *       condición, 0 si no, y llamar con {@code indice + 1}.</li>
     *   <li><b>Cómo se avanza:</b> el índice aumenta en 1.</li>
     *   <li><b>Parámetros:</b> {@code prioridadMinima} (constante) e
     *       {@code indice} (variable).</li>
     * </ul>
     *
     * @param prioridadMinima prioridad mínima a contar
     * @param indice posición actual en la lista
     * @return cantidad de paquetes con prioridad suficiente, desde {@code indice} al final
     */
    private int contarPaquetesConPrioridadMinimaRec(int prioridadMinima, int indice) {
        // CASO BASE: no quedan paquetes por revisar
        if (indice == paquetesRegistrados.size()) {
            return 0;
        }

        // CASO RECURSIVO
        Paquete actual = paquetesRegistrados.get(indice);
        int aporte = (actual.getPrioridad() >= prioridadMinima) ? 1 : 0;

        return aporte + contarPaquetesConPrioridadMinimaRec(prioridadMinima, indice + 1);
    }

    // ==================== DIVIDE Y VENCERÁS ====================

    /**
     * Encuentra el paquete de mayor peso entre todos los paquetes registrados
     * en la jornada, aplicando la estrategia <b>divide y vencerás</b>.
     *
     * <p><b>Estrategia:</b></p>
     * <ol>
     *   <li><b>Dividir:</b> partir el rango en dos mitades.</li>
     *   <li><b>Conquistar:</b> encontrar el mayor en cada mitad recursivamente.</li>
     *   <li><b>Combinar:</b> devolver el mayor de los dos resultados.</li>
     * </ol>
     *
     * <p><b>Complejidad:</b> O(n). Aunque se divide en mitades, todos los
     * elementos deben ser revisados al menos una vez.</p>
     *
     * @return el paquete de mayor peso
     * @throws IllegalStateException si no hay paquetes registrados
     */
    public Paquete paqueteDeMayorPeso() {
        if (paquetesRegistrados.isEmpty()) {
            throw new IllegalStateException("No hay paquetes registrados para analizar.");
        }
        return paqueteDeMayorPesoRec(0, paquetesRegistrados.size() - 1);
    }

    /**
     * Método recursivo que encuentra el paquete de mayor peso dentro del
     * rango {@code [inicio, fin]} aplicando divide y vencerás.
     *
     * <p><b>Análisis recursivo:</b></p>
     * <ul>
     *   <li><b>Caso base:</b> {@code inicio == fin} → un solo elemento →
     *       ese elemento es el mayor del rango.</li>
     *   <li><b>Caso recursivo:</b> dividir el rango en dos mitades,
     *       resolver cada mitad y quedarse con el de mayor peso.</li>
     *   <li><b>Cómo se avanza:</b> cada llamada reduce el rango a la mitad.
     *       El rango se vuelve más pequeño hasta llegar a un solo elemento.</li>
     *   <li><b>Parámetros:</b> {@code inicio} y {@code fin}, que cambian
     *       en cada llamada (mitad izquierda y mitad derecha).</li>
     *   <li><b>Combinación:</b> se comparan los pesos de los ganadores de
     *       cada mitad y se devuelve el mayor.</li>
     * </ul>
     *
     * @param inicio índice inferior del rango
     * @param fin índice superior del rango
     * @return el paquete de mayor peso dentro del rango
     */
    private Paquete paqueteDeMayorPesoRec(int inicio, int fin) {
        // CASO BASE: un solo elemento → es el mayor de ese rango
        if (inicio == fin) {
            return paquetesRegistrados.get(inicio);
        }

        // DIVIDIR: partir el rango en dos mitades
        int medio = inicio + (fin - inicio) / 2;

        // CONQUISTAR: encontrar el mayor de cada mitad
        Paquete mayorIzquierda = paqueteDeMayorPesoRec(inicio, medio);
        Paquete mayorDerecha = paqueteDeMayorPesoRec(medio + 1, fin);

        // COMBINAR: devolver el mayor de los dos
        return (mayorIzquierda.getPeso() >= mayorDerecha.getPeso())
                ? mayorIzquierda
                : mayorDerecha;
    }


    






}