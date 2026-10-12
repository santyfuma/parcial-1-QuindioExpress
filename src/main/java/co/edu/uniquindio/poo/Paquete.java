package co.edu.uniquindio.poo;

import java.util.Objects;

/**
 * Representa un paquete gestionado por QuindíoExpress.
 *
 * Orden natural: por código ascendente (PQ105 < PQ318 < PQ942).
 * Dos paquetes son iguales si tienen el mismo código.
 *
 * El código NO se genera aquí: el Sistema es responsable de generar
 * códigos únicos y pasarlos al constructor.
 */
public class Paquete implements Comparable<Paquete> {

    private final String codigo;
    private final Municipio destino;
    private final float peso;
    private final int prioridad;      // 1 (mínima) a 5 (máxima)
    private final int tiempoEstimado; // minutos

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

    // ==================== GETTERS ====================

    public String getCodigo()         { return codigo; }
    public Municipio getDestino()     { return destino; }
    public float getPeso()            { return peso; }
    public int getPrioridad()         { return prioridad; }
    public int getTiempoEstimado()    { return tiempoEstimado; }

    // ==================== EQUALS / HASHCODE ====================
    // Dos paquetes son iguales si tienen el mismo código.

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

    // ==================== COMPARABLE ====================
    // Orden natural: código ascendente. Ej: PQ105 < PQ318 < PQ942.

    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    // ==================== TOSTRING ====================

    @Override
    public String toString() {
        return String.format(
                "Paquete[%s → %s, %.1f kg, prioridad %d, %d min]",
                codigo, destino, peso, prioridad, tiempoEstimado
        );
    }
}