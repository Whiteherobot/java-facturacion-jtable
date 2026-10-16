package facturacion.controlador;

import facturacion.modelo.Dinero;
import facturacion.modelo.Producto;
import facturacion.servicio.ProductoServicio;

import java.util.List;

public class ProductoControl {

    private final ProductoServicio servicio;

    public ProductoControl(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    public Producto registrar(String codigo, String nombre, String precio, int stock) {
        return servicio.crear(new Producto(codigo, nombre, Dinero.leer(precio), stock));
    }

    public Producto reponer(String codigo, int cantidad) {
        Producto producto = servicio.obtener(codigo);
        producto.reponer(cantidad);
        return producto;
    }

    public List<Producto> listar() {
        return servicio.listar();
    }
}
