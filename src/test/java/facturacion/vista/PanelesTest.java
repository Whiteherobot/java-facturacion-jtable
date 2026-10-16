package facturacion.vista;

import facturacion.controlador.ClienteControl;
import facturacion.controlador.FacturaControl;
import facturacion.controlador.ProductoControl;
import facturacion.servicio.ClienteServicio;
import facturacion.servicio.FacturaServicio;
import facturacion.servicio.ProductoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelesTest {

    private final List<String> errores = new ArrayList<>();
    private final List<String> avisos = new ArrayList<>();
    private final Mensajes mensajes = new Mensajes() {
        @Override
        public void error(String mensaje) {
            errores.add(mensaje);
        }

        @Override
        public void informacion(String mensaje) {
            avisos.add(mensaje);
        }
    };

    private ClienteControl clientes;
    private ProductoControl productos;
    private FacturaControl facturas;

    @BeforeEach
    void preparar() {
        ClienteServicio cs = new ClienteServicio();
        ProductoServicio ps = new ProductoServicio();
        clientes = new ClienteControl(cs);
        productos = new ProductoControl(ps);
        facturas = new FacturaControl(new FacturaServicio(), cs, ps, Clock.systemDefaultZone());
        DatosEjemplo.cargar(clientes, productos);
    }

    @Test
    void registrarClienteLoMuestraEnLaTabla() {
        PanelClientes panel = new PanelClientes(clientes, mensajes);
        assertEquals(2, panel.modelo.getRowCount());
        panel.cedula.setText("0923456784");
        panel.nombres.setText("Luis");
        panel.apellidos.setText("Pérez");
        panel.registrar.doClick();
        assertEquals(3, panel.modelo.getRowCount());
        assertEquals("Luis", panel.modelo.getValueAt(2, 1));
        assertEquals("", panel.cedula.getText());

        panel.cedula.setText("123");
        panel.registrar.doClick();
        assertEquals(List.of("La cédula no es válida"), errores);
    }

    @Test
    void reponerStockDesdeLaTabla() {
        PanelProductos panel = new PanelProductos(productos, mensajes);
        panel.reponer.doClick();
        assertEquals(1, errores.size());
        panel.tabla.setRowSelectionInterval(2, 2);
        panel.reposicion.setValue(4);
        panel.reponer.doClick();
        assertEquals(10, panel.modelo.getValueAt(2, 3));
    }

    @Test
    void armarYEmitirUnaFactura() {
        PanelFacturas panel = new PanelFacturas(facturas, clientes, productos, mensajes);
        assertFalse(panel.emitir.isEnabled());

        panel.producto.setSelectedIndex(0);   // Teclado $18.50
        panel.cantidad.setValue(2);
        panel.agregar.doClick();
        panel.agregar.doClick();               // se suma a la misma línea
        assertEquals(1, panel.lineas.getRowCount());
        assertEquals(4, panel.lineas.getValueAt(0, 2));
        assertTrue(panel.totales.getText().contains("Total $85.10"));

        panel.emitir.doClick();
        assertEquals(1, panel.facturas.getRowCount());
        assertEquals("Ana Lucía Torres Vega", panel.facturas.getValueAt(0, 2));
        assertEquals(0, panel.lineas.getRowCount());
        assertTrue(avisos.get(0).startsWith("Factura 001-001-000000001"));
    }

    @Test
    void avisaSiNoAlcanzaElStockOElLimiteDeConsumidorFinal() {
        PanelFacturas panel = new PanelFacturas(facturas, clientes, productos, mensajes);
        panel.producto.setSelectedIndex(2);   // Monitor, stock 6
        panel.cantidad.setValue(7);
        panel.agregar.doClick();
        assertTrue(errores.get(0).startsWith("Stock insuficiente"));

        panel.cantidad.setValue(1);
        panel.agregar.doClick();
        panel.consumidorFinal.doClick();
        panel.emitir.doClick();
        assertTrue(errores.get(1).contains("consumidor final"));
        assertEquals(0, panel.facturas.getRowCount());
    }
}
