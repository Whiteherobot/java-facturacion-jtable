package facturacion.modelo;

import java.math.BigDecimal;

/**
 * Línea de una factura. Guarda el nombre y el precio del momento de la venta,
 * para que la factura no cambie si después cambia el producto.
 */
public record LineaFactura(String codigo, String producto, BigDecimal precioUnitario, int cantidad) {

    public LineaFactura {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    public static LineaFactura de(Producto producto, int cantidad) {
        return new LineaFactura(producto.getCodigo(), producto.getNombre(), producto.getPrecio(), cantidad);
    }

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, Dinero.REDONDEO);
    }
}
