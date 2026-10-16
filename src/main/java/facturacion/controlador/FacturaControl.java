package facturacion.controlador;

import facturacion.modelo.Cliente;
import facturacion.modelo.Factura;
import facturacion.modelo.LineaFactura;
import facturacion.modelo.Producto;
import facturacion.servicio.ClienteServicio;
import facturacion.servicio.FacturaServicio;
import facturacion.servicio.ProductoServicio;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FacturaControl {

    private final FacturaServicio facturas;
    private final ClienteServicio clientes;
    private final ProductoServicio productos;
    private final Clock reloj;

    public FacturaControl(FacturaServicio facturas, ClienteServicio clientes, ProductoServicio productos, Clock reloj) {
        this.facturas = facturas;
        this.clientes = clientes;
        this.productos = productos;
        this.reloj = reloj;
    }

    /**
     * Emite una factura. Primero valida todo (cliente, stock y límite de
     * consumidor final) y solo entonces descuenta el stock, para que una
     * factura rechazada no deje el inventario a medias.
     *
     * @param cedula   cédula del cliente, o null para consumidor final
     * @param cantidades código de producto → cantidad; un mismo producto se suma
     */
    public Factura emitir(String cedula, Map<String, Integer> cantidades) {
        Cliente cliente = cedula == null ? null : clientes.obtener(cedula);

        Map<Producto, Integer> pedido = new LinkedHashMap<>();
        cantidades.forEach((codigo, cantidad) -> pedido.merge(productos.obtener(codigo), cantidad, Integer::sum));

        List<LineaFactura> lineas = new ArrayList<>();
        pedido.forEach((producto, cantidad) -> {
            if (!producto.hayStock(cantidad)) {
                throw new IllegalStateException("Stock insuficiente de " + producto.getNombre()
                        + ": hay " + producto.getStock() + ", se piden " + cantidad);
            }
            lineas.add(LineaFactura.de(producto, cantidad));
        });

        Factura factura = new Factura(facturas.siguienteNumero(), LocalDateTime.now(reloj), cliente, lineas);
        pedido.forEach(Producto::descontar);
        return facturas.crear(factura);
    }

    public List<Factura> listar() {
        return facturas.listar();
    }
}
