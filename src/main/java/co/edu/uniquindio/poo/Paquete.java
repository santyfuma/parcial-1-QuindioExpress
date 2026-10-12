package co.edu.uniquindio.poo;

import java.util.Objects;

/**
 * Representa un paquete gestionado por QuindíoExpress durante una jornada.
 *
 * <p>El orden natural de los paquetes está determinado por el código
 * en orden ascendente. Ejemplo: {@code PQ105 < PQ318 < PQ942}.</p>
 *
 * <p>Dos paquetes se consideran iguales si comparten el mismo código,
 * ya que este es su identificador único dentro de la jornada.</p>
 *
 * <p>El código no se genera dentro de esta clase: el {@link Sistema}
 * es responsable de generar códigos únicos y pasarlos al constructor.
 * Esto permite centralizar la validación de unicidad y facilita
 * las pruebas unitarias.</p>

 * @see Sistema
 * @see Municipio
 */
public class Paquete implements Comparable<Paquete> {

    /** Código único del paquete. Formato esperado: {@code PQ###}. */
    private final String codigo;

    /** Municipio hacia el cual debe ser enviado el paquete. */
    private final Municipio destino;

    /** Peso del paquete en kilogramos. Debe ser mayor que cero. */
    private final float peso;

    /** Prioridad del paquete. Rango: 1 (mínima) a 5 (máxima). */
    private final int prioridad;

    /** Tiempo estimado de entrega en minutos. Debe ser positivo. */
    private final int tiempoEstimado;

    /**
     * Construye un nuevo paquete con todos sus atributos.
     *
     * @param codigo identificador único del paquete, no nulo ni vacío
     * @param destino municipio de destino, no nulo
     * @param peso peso en kilogramos, debe ser mayor que cero
     * @param prioridad nivel de prioridad entre 1 y 5
     * @param tiempoEstimado tiempo estimado de entrega en minutos, positivo
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Paquete(String codigo,
                   Municipio destino,
                   float peso,
                   int prioridad,
                   int tiempoEstimado) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (destino == null) {
            throw new IllegalArgumentException("El destino no puede ser nulo.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser positivo.");
        }
        if (prioridad < 1 || prioridad > 5) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5.");
        }
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser positivo.");
        }

        this.codigo = codigo;
        this.destino = destino;
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
    }

    /**
     * Devuelve el código único del paquete.
     * @return el código del paquete (ej: {@code "PQ105"})
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Devuelve el municipio de destino del paquete.
     * @return el destino, nunca nulo
     */
    public Municipio getDestino() {
        return destino;
    }

    /**
     * Devuelve el peso del paquete en kilogramos.
     * @return el peso, siempre mayor que cero
     */
    public float getPeso() {
        return peso;
    }

    /**
     * Devuelve la prioridad del paquete (1 = mínima, 5 = máxima).
     * @return el valor de la prioridad
     */
    public int getPrioridad() {
        return prioridad;
    }

    /**
     * Devuelve el tiempo estimado de entrega en minutos.
     * @return el tiempo estimado, siempre positivo
     */
    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paquete)) return false;
        Paquete paquete = (Paquete) o;
        return Objects.equals(codigo, paquete.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    /**
     * Compara dos paquetes por su código en orden ascendente.
     *
     * <p>Este es el orden natural de los paquetes y se utiliza en
     * estructuras ordenadas como {@code TreeSet} y en la búsqueda
     * binaria recursiva implementada en {@link Sistema}.</p>
     *
     * @param otro paquete contra el que se compara
     * @return negativo si este paquete va antes, 0 si son iguales,
     *         positivo si va después
     */
    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        return String.format(
                "Paquete[%s → %s, %.1f kg, prioridad %d, %d min]",
                codigo, destino, peso, prioridad, tiempoEstimado
        );
    }
}