package facturacion.vista;

import facturacion.controlador.ClienteControl;
import facturacion.controlador.FacturaControl;
import facturacion.controlador.ProductoControl;
import facturacion.servicio.ClienteServicio;
import facturacion.servicio.FacturaServicio;
import facturacion.servicio.ProductoServicio;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.time.Clock;

/**
 * Ventana principal: una pestaña por módulo.
 */
public class VentanaFacturacion extends JFrame implements Mensajes {

    public VentanaFacturacion() {
        super("Facturación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ClienteServicio clienteServicio = new ClienteServicio();
        ProductoServicio productoServicio = new ProductoServicio();
        ClienteControl clientes = new ClienteControl(clienteServicio);
        ProductoControl productos = new ProductoControl(productoServicio);
        FacturaControl facturas = new FacturaControl(new FacturaServicio(), clienteServicio, productoServicio,
                Clock.systemDefaultZone());
        DatosEjemplo.cargar(clientes, productos);

        PanelClientes panelClientes = new PanelClientes(clientes, this);
        PanelProductos panelProductos = new PanelProductos(productos, this);
        PanelFacturas panelFacturas = new PanelFacturas(facturas, clientes, productos, this);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Facturas", panelFacturas);
        pestanias.addTab("Clientes", panelClientes);
        pestanias.addTab("Productos", panelProductos);
        // Al volver a una pestaña se recargan los datos que cambiaron en las otras.
        pestanias.addChangeListener(e -> {
            panelFacturas.refrescar();
            panelProductos.refrescar();
        });
        setContentPane(pestanias);
        setSize(860, 620);
        setLocationRelativeTo(null);
    }

    @Override
    public void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void informacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Facturación", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignorada) {
                // Se usa el look and feel de Java.
            }
            new VentanaFacturacion().setVisible(true);
        });
    }
}
