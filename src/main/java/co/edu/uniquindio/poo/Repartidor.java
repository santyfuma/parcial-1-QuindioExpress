package co.edu.uniquindio.poo;

import java.util.Objects;

/**
 * Representa un repartidor de QuindíoExpress encargado de entregar
 * paquetes dentro de un municipio o zona específica.
 *
 * <p>El orden natural de los repartidores está determinado por su
 * identificación en orden ascendente.</p>
 *
 * <p>Dos repartidores son iguales si comparten la misma identificación,
 * ya que es su identificador único.</p>
 *
 * <p>La identificación no se genera dentro de esta clase: el {@link Sistema}
 * es responsable de generarla y pasarla al constructor.</p>
 *
 * @author Santiago
 * @version 1.0
 * @see Sistema
 * @see Municipio
 */
public class Repartidor implements Comparable<Repartidor> {

    /** Identificación única del repartidor. Formato esperado: {@code RP###}. */
    private final String identificacion;

    /** Nombre completo del repartidor. No nulo ni vacío. */
    private final String nombre;

    /** Municipio o zona principal donde realiza sus entregas. No nulo. */
    private final Municipio zona;

    /** Indica si el repartidor puede recibir nuevas asignaciones. */
    private boolean disponible;

    /**
     * Construye un nuevo repartidor con todos sus atributos.
     *
     * <p>El repartidor se crea por defecto como disponible.</p>
     *
     * @param identificacion identificador único del repartidor, no nulo ni vacío
     * @param nombre nombre completo del repartidor, no nulo ni vacío
     * @param zona municipio o zona principal del repartidor, no nulo
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Repartidor(String identificacion, String nombre, Municipio zona) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación es obligatoria.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("La zona es obligatoria.");
        }

        this.identificacion = identificacion;
        this.nombre = nombre;
        this.zona = zona;
        this.disponible = true;
    }

    /**
     * Devuelve la identificación única del repartidor.
     * @return la identificación (ej: {@code "RP001"})
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * Devuelve el nombre completo del repartidor.
     * @return el nombre, nunca nulo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la zona principal del repartidor.
     * @return el municipio de la zona, nunca nulo
     */
    public Municipio getZona() {
        return zona;
    }

    /**
     * Indica si el repartidor está disponible para nuevas asignaciones.
     * @return {@code true} si está disponible, {@code false} en caso contrario
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Cambia el estado de disponibilidad del repartidor.
     *
     * @param disponible nuevo estado de disponibilidad
     */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repartidor)) return false;
        Repartidor repartidor = (Repartidor) o;
        return Objects.equals(identificacion, repartidor.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    /**
     * Compara dos repartidores por su identificación en orden ascendente.
     *
     * @param otro repartidor contra el que se compara
     * @return negativo si este va antes, 0 si son iguales, positivo si va después
     */
    @Override
    public int compareTo(Repartidor otro) {
        return this.identificacion.compareTo(otro.identificacion);
    }

    @Override
    public String toString() {
        return String.format(
                "Repartidor[%s - %s, zona: %s, %s]",
                identificacion, nombre, zona, disponible ? "disponible" : "no disponible"
        );
    }
}