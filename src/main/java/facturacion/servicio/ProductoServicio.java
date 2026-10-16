package facturacion.servicio;

import facturacion.modelo.Producto;

public class ProductoServicio extends Repositorio<Producto, String> {

    public ProductoServicio() {
        super("un producto", Producto::getCodigo);
    }
}
