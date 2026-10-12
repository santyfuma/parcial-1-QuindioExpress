package co.edu.uniquindio.poo;

/**
 * Contrato genérico para las colecciones internas desarrolladas por
 * QuindíoExpress.
 *
 * <p>Establece QUÉ operaciones debe ofrecer una colección, sin importar
 * CÓMO se implementen internamente. Las implementaciones (por ejemplo
 * {@link ListaEnlazada}) definen el cómo.</p>
 *
 * <p>El tipo genérico {@code T} permite reutilizar la colección con
 * distintos tipos de dato: {@code Coleccion<Paquete>}, {@code Coleccion<Repartidor>},
 * etc.</p>
 *
 * @param <T> tipo de los elementos almacenados en la colección
 *
 * @author Santiago
 * @version 1.0
 * @see ListaEnlazada
 */
public interface Coleccion<T> {

    /**
     * Agrega un elemento al final de la colección.
     *
     * @param elemento el elemento a agregar, no nulo
     * @throws IllegalArgumentException si el elemento es nulo
     */
    void agregar(T elemento);

    /**
     * Elimina la primera ocurrencia del elemento indicado.
     *
     * @param elemento el elemento a eliminar
     * @return {@code true} si se eliminó, {@code false} si no se encontró
     */
    boolean eliminar(T elemento);

    /**
     * Devuelve el elemento ubicado en la posición indicada.
     *
     * @param indice posición del elemento (base 0)
     * @return el elemento en esa posición
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    T obtener(int indice);

    /**
     * Devuelve la cantidad de elementos almacenados.
     *
     * @return el tamaño de la colección
     */
    int tamanio();

    /**
     * Indica si la colección no contiene elementos.
     *
     * @return {@code true} si está vacía, {@code false} en caso contrario
     */
    boolean estaVacia();
}