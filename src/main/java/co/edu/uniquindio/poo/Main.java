package co.edu.uniquindio.poo;

import java.util.List;

/**
 * Clase de demostración del sistema QuindíoExpress.
 *
 * <p>Registra los datos mínimos exigidos por el enunciado del parcial:</p>
 * <ul>
 *   <li>30 paquetes con diferentes pesos y prioridades.</li>
 *   <li>Al menos 5 municipios destino distintos.</li>
 *   <li>Al menos 6 repartidores.</li>
 *   <li>Varios paquetes destinados a un mismo municipio.</li>
 *   <li>Códigos no consecutivos (generados aleatoriamente por el sistema).</li>
 * </ul>
 *
 * <p>Además, ejecuta cada una de las operaciones solicitadas para
 * demostrar su funcionamiento: registro, consulta por código, atención
 * FIFO, atención por prioridad, entrega, historial, análisis recursivos
 * y divide y vencerás.</p>
 *
 * @author Santiago
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {

        Sistema sistema = new Sistema();

        System.out.println("====================================================");
        System.out.println("   QUINDÍOEXPRESS - DEMOSTRACIÓN DEL SISTEMA");
        System.out.println("====================================================\n");

        // =====================================================
        // 1. REGISTRO DE REPARTIDORES (6)
        // =====================================================
        System.out.println("--- 1. Registrando 6 repartidores ---");
        sistema.registrarRepartidor("Juan Pérez",      Municipio.ARMENIA);
        sistema.registrarRepartidor("María López",     Municipio.SALENTO);
        sistema.registrarRepartidor("Carlos Ruiz",     Municipio.CALARCA);
        sistema.registrarRepartidor("Ana Gómez",       Municipio.MONTENEGRO);
        sistema.registrarRepartidor("Luis Torres",     Municipio.QUIMBAYA);
        sistema.registrarRepartidor("Sofía Ramírez",   Municipio.ARMENIA);

        for (Repartidor r : sistema.getRepartidores()) {
            System.out.println("  " + r);
        }
        System.out.println("Total repartidores: " + sistema.getCantidadRepartidores());

        // =====================================================
        // 2. REGISTRO DE 30 PAQUETES
        // =====================================================
        System.out.println("\n--- 2. Registrando 30 paquetes ---");
        //          destino                peso    prioridad   tiempo
        sistema.registrarPaquete(Municipio.ARMENIA,     4.5f,   5,   30);
        sistema.registrarPaquete(Municipio.SALENTO,     3.0f,   4,   45);
        sistema.registrarPaquete(Municipio.ARMENIA,     7.2f,   3,   20);
        sistema.registrarPaquete(Municipio.CALARCA,     2.3f,   5,   15);
        sistema.registrarPaquete(Municipio.MONTENEGRO,  5.8f,   2,   60);
        sistema.registrarPaquete(Municipio.QUIMBAYA,    1.5f,   4,   25);
        sistema.registrarPaquete(Municipio.ARMENIA,     9.1f,   1,   90);
        sistema.registrarPaquete(Municipio.SALENTO,     2.7f,   5,   35);
        sistema.registrarPaquete(Municipio.CALARCA,     6.4f,   3,   40);
        sistema.registrarPaquete(Municipio.MONTENEGRO,  0.9f,   4,   18);
        sistema.registrarPaquete(Municipio.ARMENIA,     3.3f,   2,   55);
        sistema.registrarPaquete(Municipio.CORDOBA,     8.0f,   5,   28);
        sistema.registrarPaquete(Municipio.SALENTO,     4.1f,   3,   42);
        sistema.registrarPaquete(Municipio.CALARCA,     1.2f,   2,   75);
        sistema.registrarPaquete(Municipio.FILANDIA,    5.5f,   4,   22);
        sistema.registrarPaquete(Municipio.ARMENIA,     2.0f,   5,   12);
        sistema.registrarPaquete(Municipio.QUIMBAYA,    6.8f,   3,   38);
        sistema.registrarPaquete(Municipio.MONTENEGRO,  3.7f,   1,   95);
        sistema.registrarPaquete(Municipio.SALENTO,     7.9f,   5,   19);
        sistema.registrarPaquete(Municipio.CALARCA,     4.4f,   4,   27);
        sistema.registrarPaquete(Municipio.ARMENIA,     1.8f,   2,   65);
        sistema.registrarPaquete(Municipio.CORDOBA,     9.5f,   5,   16);
        sistema.registrarPaquete(Municipio.FILANDIA,    2.4f,   3,   48);
        sistema.registrarPaquete(Municipio.QUIMBAYA,    5.1f,   4,   33);
        sistema.registrarPaquete(Municipio.SALENTO,     3.9f,   1,   85);
        sistema.registrarPaquete(Municipio.CALARCA,    11.2f,   5,   14);
        sistema.registrarPaquete(Municipio.ARMENIA,     6.6f,   3,   36);
        sistema.registrarPaquete(Municipio.MONTENEGRO,  2.1f,   2,   58);
        sistema.registrarPaquete(Municipio.FILANDIA,    7.5f,   4,   24);
        sistema.registrarPaquete(Municipio.CORDOBA,     4.8f,   5,   21);

        System.out.println("Total paquetes registrados: " + sistema.getCantidadPaquetesRegistrados());
        System.out.println("Total pendientes (FIFO):    " + sistema.getCantidadPendientes());
        System.out.println("Total municipios con paquetes: " + sistema.getCantidadMunicipiosConPaquetes());

        // =====================================================
        // 3. MOSTRAR PAQUETES EN ORDEN DE REGISTRO
        // =====================================================
        System.out.println("\n--- 3. Paquetes en orden exacto de registro (FIFO) ---");
        sistema.mostrarPaquetesEnOrdenDeRegistro();

        // =====================================================
        // 4. MOSTRAR PAQUETES EN ORDEN DE DESPACHO
        // =====================================================
        System.out.println("\n--- 4. Paquetes en orden de despacho (prioridad + desempates) ---");
        sistema.mostrarPaquetesPorPrioridadDeDespacho();

        // =====================================================
        // 5. MUNICIPIOS ORDENADOS ALFABÉTICAMENTE
        // =====================================================
        System.out.println("\n--- 5. Municipios con paquetes (orden alfabético, sin repetir) ---");
        sistema.mostrarMunicipiosOrdenados();

        // =====================================================
        // 6. CONSULTA POR CÓDIGO (búsqueda binaria recursiva)
        // =====================================================
        System.out.println("\n--- 6. Consulta por código (búsqueda binaria recursiva) ---");
        // Tomamos el primer paquete registrado como ejemplo
        List<Paquete> registrados = sistema.getPaquetesRegistrados();
        String codigoBuscado = registrados.get(0).getCodigo();
        Paquete encontrado = sistema.buscarPaquetePorCodigo(codigoBuscado);
        System.out.println("Buscando " + codigoBuscado + ": " + encontrado);

        // Búsqueda de uno que no existe
        try {
            sistema.buscarPaquetePorCodigo("PQ999");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("Búsqueda de PQ999 → " + e.getMessage());
        }

        // =====================================================
        // 7. PAQUETES POR MUNICIPIO (relación Municipio → Lista)
        // =====================================================
        System.out.println("\n--- 7. Paquetes destinados a ARMENIA ---");
        for (Paquete p : sistema.getPaquetesPorMunicipio(Municipio.ARMENIA)) {
            System.out.println("  " + p);
        }
        System.out.println("Total a ARMENIA: " + sistema.getPaquetesPorMunicipio(Municipio.ARMENIA).size());
        System.out.println("Total a SALENTO: " + sistema.getPaquetesPorMunicipio(Municipio.SALENTO).size());
        System.out.println("Total a CALARCA: " + sistema.getPaquetesPorMunicipio(Municipio.CALARCA).size());

        // =====================================================
        // 8. ATENCIÓN FIFO (orden de registro)
        // =====================================================
        System.out.println("\n--- 8. Atención por FIFO (3 paquetes) ---");
        for (int i = 1; i <= 3; i++) {
            Paquete atendido = sistema.atenderFifo();
            System.out.println("  Atendido #" + i + ": " + atendido);
            sistema.entregarPaquete(atendido);
        }

        // =====================================================
        // 9. ATENCIÓN POR PRIORIDAD (regla del despacho)
        // =====================================================
        System.out.println("\n--- 9. Atención por prioridad (3 paquetes) ---");
        for (int i = 1; i <= 3; i++) {
            Paquete atendido = sistema.atenderPorPrioridad();
            System.out.println("  Atendido #" + i + ": " + atendido);
            sistema.entregarPaquete(atendido);
        }

        // =====================================================
        // 10. HISTORIAL DE ENTREGAS (lista propia)
        // =====================================================
        System.out.println("\n--- 10. Historial de entregas (orden cronológico) ---");
        sistema.mostrarHistorialDeEntregas();
        System.out.println("Total entregados: " + sistema.getCantidadEntregados());
        System.out.println("Total pendientes: " + sistema.getCantidadPendientes());

        // =====================================================
        // 11. PESO TOTAL POR MUNICIPIO (recursivo)
        // =====================================================
        System.out.println("\n--- 11. Análisis recursivo: peso total por municipio ---");
        System.out.printf("Peso total a ARMENIA:    %.1f kg%n",
                sistema.pesoTotalPorMunicipio(Municipio.ARMENIA));
        System.out.printf("Peso total a SALENTO:    %.1f kg%n",
                sistema.pesoTotalPorMunicipio(Municipio.SALENTO));
        System.out.printf("Peso total a CALARCA:    %.1f kg%n",
                sistema.pesoTotalPorMunicipio(Municipio.CALARCA));
        System.out.printf("Peso total a FILANDIA:   %.1f kg%n",
                sistema.pesoTotalPorMunicipio(Municipio.FILANDIA));
        System.out.printf("Peso total a GENOVA:     %.1f kg%n",
                sistema.pesoTotalPorMunicipio(Municipio.GENOVA));

        // =====================================================
        // 12. CONTAR PAQUETES POR PRIORIDAD MÍNIMA (recursivo)
        // =====================================================
        System.out.println("\n--- 12. Análisis recursivo: paquetes con prioridad ≥ X ---");
        for (int prioridad = 5; prioridad >= 1; prioridad--) {
            int cantidad = sistema.contarPaquetesConPrioridadMinima(prioridad);
            System.out.printf("Paquetes con prioridad >= %d: %d%n", prioridad, cantidad);
        }

        // =====================================================
        // 13. PAQUETE DE MAYOR PESO (divide y vencerás)
        // =====================================================
        System.out.println("\n--- 13. Divide y vencerás: paquete de mayor peso ---");
        Paquete masPesado = sistema.paqueteDeMayorPeso();
        System.out.println("Paquete de mayor peso: " + masPesado);

        // =====================================================
        // 14. RESUMEN FINAL
        // =====================================================
        System.out.println("\n====================================================");
        System.out.println("   RESUMEN FINAL");
        System.out.println("====================================================");
        System.out.println("Paquetes registrados: " + sistema.getCantidadPaquetesRegistrados());
        System.out.println("Paquetes entregados:  " + sistema.getCantidadEntregados());
        System.out.println("Paquetes pendientes:  " + sistema.getCantidadPendientes());
        System.out.println("Repartidores:         " + sistema.getCantidadRepartidores());
        System.out.println("Municipios con paquetes: " + sistema.getCantidadMunicipiosConPaquetes());
        System.out.println("\n¡Demostración completada con éxito!");
    }
}