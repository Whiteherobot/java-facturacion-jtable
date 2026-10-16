package facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Factura de venta. Sin cliente es una factura a consumidor final, que solo se
 * permite hasta {@link Dinero#LIMITE_CONSUMIDOR_FINAL}.
 */
public final class Factura {

    private final String numero;
    private final LocalDateTime fecha;
    private final Cliente cliente;
    private final List<LineaFactura> lineas;

    public Factura(String numero, LocalDateTime fecha, Cliente cliente, List<LineaFactura> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("La factura necesita al menos un producto");
        }
        this.numero = numero;
        this.fecha = fecha;
        this.cliente = cliente;
        this.lineas = List.copyOf(lineas);
        if (cliente == null && total().compareTo(Dinero.LIMITE_CONSUMIDOR_FINAL) > 0) {
            throw new IllegalArgumentException("Una factura a consumidor final no puede superar $"
                    + Dinero.LIMITE_CONSUMIDOR_FINAL + ". Ingrese los datos del cliente.");
        }
    }

    public BigDecimal subtotal() {
        return lineas.stream().map(LineaFactura::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal iva() {
        return subtotal().multiply(Dinero.IVA).setScale(2, Dinero.REDONDEO);
    }

    public BigDecimal total() {
        return subtotal().add(iva());
    }

    public String getNumero() {
        return numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Optional<Cliente> getCliente() {
        return Optional.ofNullable(cliente);
    }

    public String nombreCliente() {
        return cliente == null ? "Consumidor final" : cliente.nombreCompleto();
    }

    public List<LineaFactura> getLineas() {
        return lineas;
    }
}
