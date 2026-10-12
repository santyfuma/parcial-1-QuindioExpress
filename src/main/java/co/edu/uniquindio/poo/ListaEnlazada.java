package co.edu.uniquindio.poo;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * <p>La lista es genérica y reutilizable con cualquier tipo {@code T}.</p>
 *
 * <p>El código externo NO necesita conocer los nodos internos: la lista
 * implementa {@link Iterable} para permitir el recorrido con {@code for-each}
 * y expone únicamente los métodos definidos en {@link Coleccion}.</p>
 *
 */
public class ListaEnlazada<T> implements Coleccion<T>, Iterable<T> {

    /**
     * Nodo interno de la lista. Es {@code private static} para que el
     * exterior no pueda crear ni manipular nodos directamente.
     */
    private static class Nodo<T> {
        T valor;
        Nodo<T> siguiente;

        Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    /** Primer nodo de la lista. {@code null} si la lista está vacía. */
    private Nodo<T> cabeza;

    /** Cantidad actual de elementos en la lista. */
    private int tamanio;

    /**
     * Crea una lista vacía.
     */
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    // ==================== AGREGAR ====================

    @Override
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento no puede ser nulo.");
        }

        Nodo<T> nuevo = new Nodo<>(elemento);

        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    // ==================== ELIMINAR ====================

    @Override
    public boolean eliminar(T elemento) {
        if (elemento == null || cabeza == null) {
            return false;
        }

        // Caso especial: eliminar la cabeza
        if (cabeza.valor.equals(elemento)) {
            cabeza = cabeza.siguiente;
            tamanio--;
            return true;
        }

        // Buscar en el resto de la lista
        Nodo<T> anterior = cabeza;
        Nodo<T> actual = cabeza.siguiente;

        while (actual != null) {
            if (actual.valor.equals(elemento)) {
                anterior.siguiente = actual.siguiente;
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    // ==================== OBTENER ====================

    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice " + indice + " fuera de rango [0, " + (tamanio - 1) + "]"
            );
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    // ==================== TAMAÑO / VACÍA ====================

    @Override
    public int tamanio() {
        return tamanio;
    }

    @Override
    public boolean estaVacia() {
        return cabeza == null;
    }

    // ==================== ITERADOR ====================

    /**
     * Devuelve un iterador que permite recorrer la lista de principio a fin
     * sin exponer los nodos internos.
     *
     * @return un iterador sobre los elementos de la lista
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException("No hay más elementos en la lista.");
                }
                T valor = actual.valor;
                actual = actual.siguiente;
                return valor;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ListaEnlazada[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.valor);
            if (actual.siguiente != null) sb.append(" → ");
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}