package co.edu.uniquindio.poo;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Repartidor  {

    private static final Set<String> codigosGenerados = new HashSet<>();
    private static final Random random =  new Random();

    private String codigo;
    private String nombre;
    private Municipio zona;
    private boolean disponible = true;

    public  Repartidor( String nombre, Municipio zona) {
        if(nombre == null) throw new IllegalArgumentException("El nombre es obligatorio");
        if(zona == null) throw new IllegalArgumentException("El Zona es obligatorio");
        this.nombre = nombre;
        this.zona = zona;
        this.codigo = generarIdentificador();
    }


    /**
     *  Metodo para generar identificador del repartidor(RP) - se limita en 999
     * @return codigo unico de repartidor
     */

    public synchronized String generarIdentificador() {

        if(codigosGenerados.size()>=1000) {
            throw new IllegalArgumentException("Se agotaron los codigos de repartidores (RP) disponibles.");
        }
        String codigoGenerado;
        do{
            int numero = random.nextInt(1000);
            codigoGenerado = String.format("RP%03d", numero);
        }while(codigosGenerados.contains(codigoGenerado));
        codigosGenerados.add(codigoGenerado);
        return codigoGenerado;

    }
}
