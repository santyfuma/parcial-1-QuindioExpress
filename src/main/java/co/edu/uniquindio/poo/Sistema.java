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
     * <p>Por ahora se implementa con búsqueda lineal (insert position).
     * En la Sesión 6 se reemplazará por una versión con búsqueda binaria
     * recursiva, para mantener la coherencia con el resto del sistema.</p>
     *
     * @param paquete paquete a insertar
     */
    private void insertarOrdenadoPorCodigo(Paquete paquete) {
        int posicion = 0;
        while (posicion < paquetesOrdenadosPorCodigo.size()
                && paquetesOrdenadosPorCodigo.get(posicion).compareTo(paquete) < 0) {
            posicion++;
        }
        paquetesOrdenadosPorCodigo.add(posicion, paquete);
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
    
}