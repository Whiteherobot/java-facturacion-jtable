package facturacion.modelo;

import java.math.BigDecimal;

/**
 * Producto del inventario. El precio no incluye IVA.
 */
public class Producto {

    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private int stock;

    public Producto(String codigo, String nombre, BigDecimal precio, int stock) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio == null || precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.codigo = codigo.strip().toUpperCase();
        this.nombre = nombre.strip();
        this.precio = precio.setScale(2, Dinero.REDONDEO);
        this.stock = stock;
    }

    public boolean hayStock(int cantidad) {
        return cantidad <= stock;
    }

    /** Descuenta unidades vendidas; falla si no alcanza el stock. */
    public void descontar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (!hayStock(cantidad)) {
            throw new IllegalStateException("Stock insuficiente de " + nombre + ": hay " + stock);
        }
        stock -= cantidad;
    }

    public void reponer(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        stock += cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return codigo + " · " + nombre + " · $" + precio;
    }
}
