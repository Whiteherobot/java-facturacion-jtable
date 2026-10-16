package facturacion.controlador;

import facturacion.modelo.Factura;
import facturacion.servicio.ClienteServicio;
import facturacion.servicio.FacturaServicio;
import facturacion.servicio.ProductoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacturaControlTest {

    private ClienteControl clientes;
    private ProductoControl productos;
    private FacturaControl facturas;

    @BeforeEach
    void preparar() {
        ClienteServicio cs = new ClienteServicio();
        ProductoServicio ps = new ProductoServicio();
        clientes = new ClienteControl(cs);
        productos = new ProductoControl(ps);
        facturas = new FacturaControl(new FacturaServicio(), cs, ps,
                Clock.fixed(Instant.parse("2022-06-09T15:00:00Z"), ZoneId.of("America/Guayaquil")));
        clientes.registrar("0102030400", "Ana", "Torres", "", "");
        productos.registrar("P001", "Teclado", "18.50", 5);
        productos.registrar("P002", "Monitor", "149.00", 1);
    }

    private Map<String, Integer> pedido(Object... codigoCantidad) {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < codigoCantidad.length; i += 2) {
            m.put((String) codigoCantidad[i], (Integer) codigoCantidad[i + 1]);
        }
        return m;
    }

    @Test
    void emiteNumeraYDescuentaStock() {
        Factura f = facturas.emitir("0102030400", pedido("P001", 2, "P002", 1));
        assertEquals("001-001-000000001", f.getNumero());
        assertEquals(new BigDecimal("186.00"), f.subtotal());
        assertEquals(10, f.getFecha().getHour());
        assertEquals(3, productos.listar().get(0).getStock());
        assertEquals(0, productos.listar().get(1).getStock());
        assertEquals("001-001-000000002", facturas.emitir(null, pedido("P001", 1)).getNumero());
    }

    @Test
    void facturaRechazadaNoTocaElStock() {
        assertThrows(IllegalStateException.class, () -> facturas.emitir("0102030400", pedido("P001", 2, "P002", 2)));
        assertThrows(IllegalArgumentException.class, () -> facturas.emitir(null, pedido("P001", 3)));  // > $50
        assertEquals(5, productos.listar().get(0).getStock());
        assertEquals(1, productos.listar().get(1).getStock());
        assertEquals(0, facturas.listar().size());
    }

    @Test
    void rechazaClienteOProductoInexistente() {
        assertThrows(IllegalArgumentException.class, () -> facturas.emitir("1712345675", pedido("P001", 1)));
        assertThrows(IllegalArgumentException.class, () -> facturas.emitir(null, pedido("X999", 1)));
    }

    @Test
    void noRepiteCodigos() {
        assertThrows(IllegalArgumentException.class, () -> productos.registrar("p001", "Otro", "1", 1));
        assertThrows(IllegalArgumentException.class, () -> clientes.registrar("0102030400", "B", "C", "", ""));
    }
}
