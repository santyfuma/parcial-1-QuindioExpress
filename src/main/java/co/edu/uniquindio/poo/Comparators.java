package co.edu.uniquindio.poo;

import java.util.Comparator;

/**
 * Catálogo centralizado de comparadores reutilizables para objetos
 * {@link Paquete}.
 *
 * <p>Todos los comparadores son {@code public static final} para que
 * puedan usarse desde cualquier parte del sistema sin crear instancias.
 * Esto cumple el requisito de reutilización del enunciado.</p>
 *
 * <p>Se distinguen dos grupos:</p>
 * <ul>
 *   <li><b>Comparadores de reporte:</b> para ordenar colecciones en
 *       presentaciones específicas (prioridad desc, peso desc, tiempo asc).</li>
 *   <li><b>Comparador de despacho:</b> criterio compuesto que determina
 *       cuál paquete debe atenderse primero (prioridad, luego tiempo,
 *       luego código).</li>
 * </ul>
 *
 * <p>Nota: los comparadores de prioridad usan orden descendente porque
 * en el dominio del problema el valor 5 representa la máxima urgencia.</p>
 *
 * @author Santiago
 * @version 1.0
 * @see Paquete
 */
public final class Comparators {

    /**
     * Constructor privado: esta clase solo expone constantes estáticas,
     * no debe instanciarse.
     */
    private Comparators() {
        // Clase de utilidades, no instanciable
    }

    // ==================== COMPARADORES DE REPORTE ====================

    /**
     * Ordena paquetes por prioridad de <b>mayor a menor</b>.
     * Un paquete con prioridad 5 aparece antes que uno con prioridad 1.
     */
    public static final Comparator<Paquete> POR_PRIORIDAD_DESC =
            (a, b) -> Integer.compare(b.getPrioridad(), a.getPrioridad());

    /**
     * Ordena paquetes por peso de <b>mayor a menor</b>.
     * Un paquete más pesado aparece antes que uno más liviano.
     */
    public static final Comparator<Paquete> POR_PESO_DESC =
            (a, b) -> Float.compare(b.getPeso(), a.getPeso());

    /**
     * Ordena paquetes por tiempo estimado de <b>menor a mayor</b>.
     * Un paquete con entrega más rápida aparece antes.
     */
    public static final Comparator<Paquete> POR_TIEMPO_ASC =
            Comparator.comparingInt(Paquete::getTiempoEstimado);

    // ==================== COMPARADOR DE DESPACHO ====================

    /**
     * Criterio compuesto para determinar cuál paquete debe despacharse primero.
     *
     * <p>Reglas de prioridad (según el enunciado):</p>
     * <ol>
     *   <li>Se atiende primero el paquete con <b>mayor prioridad</b>.</li>
     *   <li>Si empatan en prioridad, se atiende primero el de <b>menor
     *       tiempo estimado</b>.</li>
     *   <li>Si aún empatan, se atiende primero el de <b>código menor
     *       alfabéticamente</b>.</li>
     * </ol>
     *
     * <p>Este comparador se usará en la {@code PriorityQueue} del
     * {@link Sistema}. Como {@code PriorityQueue} extrae el elemento
     * "menor" según el comparador, invertimos la prioridad para que
     * los paquetes más urgentes salgan primero.</p>
     */
    public static final Comparator<Paquete> PARA_DESPACHO =
            Comparator
                    // 1. Mayor prioridad primero (5 antes que 1)
                    .comparingInt(Paquete::getPrioridad)
                    .reversed()
                    // 2. Menor tiempo estimado primero
                    .thenComparingInt(Paquete::getTiempoEstimado)
                    // 3. Código alfabéticamente ascendente
                    .thenComparing(Paquete::getCodigo);
}
