package co.edu.uniquindio.poo;

import java.util.LinkedHashMap;
import java.util.Map;

public class Sistema {

    private Map<String, Paquete> paquetes;

    public Sistema() {
        paquetes = new LinkedHashMap<>();
    }


    /**
     * Metodo de registro de paque
     * @param paquete
     * @return
     */
    public void registrarPaquete(Paquete paquete) {

        if (paquete == null) {
            throw new IllegalArgumentException(
                    "El paquete no puede ser nulo."
            );
        }

        String codigo = paquete.getCodigo();

        if (paquetes.containsKey(codigo)) {
            throw new IllegalArgumentException(
                    "Ya existe un paquete registrado con el código: " + codigo
            );
        }

        paquetes.put(codigo, paquete);
    }




}
