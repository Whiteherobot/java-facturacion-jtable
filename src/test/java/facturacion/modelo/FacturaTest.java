package facturacion.modelo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacturaTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2022, 6, 9, 10, 30);
    private final Cliente ana = new Cliente("0102030400", "Ana", "Torres", "", "");

    private LineaFactura linea(String precio, int cantidad) {
        return new LineaFactura("P1", "Producto", new BigDecimal(precio), cantidad);
    }

    @Test
    void calculaSubtotalIvaYTotal() {
        Factura f = new Factura("001-001-000000001", FECHA, ana, List.of(linea("18.50", 2), linea("6.75", 3)));
        assertEquals(new BigDecimal("57.25"), f.subtotal());
        assertEquals(new BigDecimal("8.59"), f.iva());      // 8.5875 redondeado
        assertEquals(new BigDecimal("65.84"), f.total());
    }

    @Test
    void consumidorFinalHastaCincuentaDolares() {
        Factura f = new Factura("1", FECHA, null, List.of(linea("43.47", 1)));   // total 49.99
        assertEquals("Consumidor final", f.nombreCliente());
        assertTrue(f.getCliente().isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> new Factura("2", FECHA, null, List.of(linea("43.49", 1))));   // total 50.01
    }

    @Test
    void necesitaAlMenosUnaLinea() {
        assertThrows(IllegalArgumentException.class, () -> new Factura("1", FECHA, ana, List.of()));
    }

    @Test
    void elProductoControlaSuStock() {
        Producto p = new Producto("p001", "Teclado", new BigDecimal("18.5"), 3);
        assertEquals("P001", p.getCodigo());
        assertEquals(new BigDecimal("18.50"), p.getPrecio());
        p.descontar(2);
        assertEquals(1, p.getStock());
        assertFalse(p.hayStock(2));
        assertThrows(IllegalStateException.class, () -> p.descontar(2));
        p.reponer(5);
        assertEquals(6, p.getStock());
    }

    @Test
    void validaCedulaYDatosDelCliente() {
        assertTrue(Cliente.esCedulaValida("1712345675"));
        assertFalse(Cliente.esCedulaValida("1712345670"));
        assertFalse(Cliente.esCedulaValida("2512345675"));
        assertThrows(IllegalArgumentException.class, () -> new Cliente("1712345670", "A", "B", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Cliente("1712345675", " ", "B", "", ""));
    }

    @Test
    void etiquetaDelIva() {
        assertEquals("IVA 15%", Dinero.ETIQUETA_IVA);
    }
}
