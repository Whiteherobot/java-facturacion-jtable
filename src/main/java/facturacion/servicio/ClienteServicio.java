package facturacion.servicio;

import facturacion.modelo.Cliente;

public class ClienteServicio extends Repositorio<Cliente, String> {

    public ClienteServicio() {
        super("un cliente", Cliente::cedula);
    }
}
