package facturacion.controlador;

import facturacion.modelo.Cliente;
import facturacion.servicio.ClienteServicio;

import java.util.List;

public class ClienteControl {

    private final ClienteServicio servicio;

    public ClienteControl(ClienteServicio servicio) {
        this.servicio = servicio;
    }

    public Cliente registrar(String cedula, String nombres, String apellidos, String telefono, String direccion) {
        return servicio.crear(new Cliente(cedula.strip(), nombres, apellidos, telefono, direccion));
    }

    public List<Cliente> listar() {
        return servicio.listar();
    }
}
