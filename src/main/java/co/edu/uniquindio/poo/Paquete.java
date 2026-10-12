package co.edu.uniquindio.poo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Paquete implements Identificador {

    private static final Set<String> codigosGenerados = new HashSet<>();
    private static final Random random = new Random();

    private String codigo;
    private Municipio destino;
    private float peso;
    private int prioridad;
    private int tiempoEstimado;

    public Paquete(Municipio destino, float peso, int prioridad, int tiempoEstimado) {
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

        this.destino = destino;
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
        this.codigo = generarIdentificador();
    }


    /**
     * Metodo para generar el identificador unico del paquete (PQ) - se limita en 999
     * @return codigo unico de paquete
     */
    @Override
    public synchronized String generarIdentificador() {

        if (codigosGenerados.size() >= 1000) {
            throw new IllegalStateException("Se agotaron los códigos de paquetes disponibles.");
        }
        String codigoGenerado;
        do {
            int numero = random.nextInt(1000);
            codigoGenerado = String.format("PQ%03d", numero);
        } while (codigosGenerados.contains(codigoGenerado));
        codigosGenerados.add(codigoGenerado);

        return codigoGenerado;
    }

    public String getCodigo() {
        return codigo;
    }
}


