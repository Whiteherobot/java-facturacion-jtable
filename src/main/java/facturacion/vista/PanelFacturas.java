package facturacion.vista;

import facturacion.controlador.ClienteControl;
import facturacion.controlador.FacturaControl;
import facturacion.controlador.ProductoControl;
import facturacion.modelo.Cliente;
import facturacion.modelo.Dinero;
import facturacion.modelo.Factura;
import facturacion.modelo.LineaFactura;
import facturacion.modelo.Producto;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Emisión de facturas: se elige el cliente, se agregan productos y se emite.
 * Abajo, la tabla de facturas emitidas.
 */
class PanelFacturas extends JPanel {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    final JRadioButton conDatos = new JRadioButton("Con datos", true);
    final JRadioButton consumidorFinal = new JRadioButton("Consumidor final");
    final JComboBox<Cliente> cliente = new JComboBox<>();
    final JComboBox<Producto> producto = new JComboBox<>();
    final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    final JButton agregar = new JButton("Agregar");
    final JButton quitar = new JButton("Quitar línea");
    final JButton emitir = new JButton("Emitir factura");
    final JLabel totales = new JLabel();

    /** Pedido en preparación: código de producto → cantidad. */
    final Map<String, Integer> pedido = new LinkedHashMap<>();
    final ModeloTabla<LineaFactura> lineas = new ModeloTabla<LineaFactura>()
            .columna("Código", LineaFactura::codigo)
            .columna("Producto", LineaFactura::producto)
            .numero("Cantidad", LineaFactura::cantidad)
            .numero("P. unitario", l -> "$" + l.precioUnitario())
            .numero("Subtotal", l -> "$" + l.subtotal());
    final JTable tablaLineas = lineas.crearTabla();
    final ModeloTabla<Factura> facturas = new ModeloTabla<Factura>()
            .columna("Número", Factura::getNumero)
            .columna("Fecha", f -> f.getFecha().format(FECHA))
            .columna("Cliente", Factura::nombreCliente)
            .numero("Subtotal", f -> "$" + f.subtotal())
            .numero(Dinero.ETIQUETA_IVA, f -> "$" + f.iva())
            .numero("Total", f -> "$" + f.total());

    private final FacturaControl control;
    private final ClienteControl clientes;
    private final ProductoControl productos;
    private final Mensajes mensajes;

    PanelFacturas(FacturaControl control, ClienteControl clientes, ProductoControl productos, Mensajes mensajes) {
        super(new BorderLayout(0, 8));
        this.control = control;
        this.clientes = clientes;
        this.productos = productos;
        this.mensajes = mensajes;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ButtonGroup tipo = new ButtonGroup();
        tipo.add(conDatos);
        tipo.add(consumidorFinal);
        conDatos.addActionListener(e -> cliente.setEnabled(true));
        consumidorFinal.addActionListener(e -> cliente.setEnabled(false));

        JPanel filaCliente = new JPanel(new FlowLayout(FlowLayout.LEADING));
        filaCliente.add(conDatos);
        filaCliente.add(cliente);
        filaCliente.add(consumidorFinal);

        JPanel filaProducto = new JPanel(new FlowLayout(FlowLayout.LEADING));
        filaProducto.add(new JLabel("Producto"));
        filaProducto.add(producto);
        filaProducto.add(new JLabel("Cantidad"));
        filaProducto.add(cantidad);
        filaProducto.add(agregar);
        filaProducto.add(quitar);
        agregar.addActionListener(e -> agregarLinea());
        quitar.addActionListener(e -> quitarLinea());

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.add(filaCliente, BorderLayout.PAGE_START);
        cabecera.add(filaProducto, BorderLayout.PAGE_END);

        totales.setFont(totales.getFont().deriveFont(Font.BOLD));
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(totales, BorderLayout.LINE_START);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        botones.add(emitir);
        pie.add(botones, BorderLayout.LINE_END);
        emitir.addActionListener(e -> emitir());

        JPanel nueva = new JPanel(new BorderLayout());
        nueva.setBorder(BorderFactory.createTitledBorder("Nueva factura"));
        nueva.add(cabecera, BorderLayout.PAGE_START);
        nueva.add(new JScrollPane(tablaLineas), BorderLayout.CENTER);
        nueva.add(pie, BorderLayout.PAGE_END);

        JScrollPane emitidas = new JScrollPane(facturas.crearTabla());
        emitidas.setBorder(BorderFactory.createTitledBorder("Facturas emitidas"));

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nueva, emitidas);
        division.setResizeWeight(0.6);
        add(division, BorderLayout.CENTER);
        refrescar();
    }

    /** Recarga clientes y productos, que pueden haber cambiado en las otras pestañas. */
    void refrescar() {
        Object clienteElegido = cliente.getSelectedItem();
        cliente.setModel(new DefaultComboBoxModel<>(clientes.listar().toArray(Cliente[]::new)));
        if (clienteElegido != null) {
            cliente.setSelectedItem(clienteElegido);
        }
        producto.setModel(new DefaultComboBoxModel<>(productos.listar().toArray(Producto[]::new)));
        actualizarPedido();
        facturas.mostrar(control.listar());
    }

    private void agregarLinea() {
        Producto p = (Producto) producto.getSelectedItem();
        if (p == null) {
            mensajes.error("Registre productos primero.");
            return;
        }
        int pedida = pedido.getOrDefault(p.getCodigo(), 0) + (Integer) cantidad.getValue();
        if (!p.hayStock(pedida)) {
            mensajes.error("Stock insuficiente de " + p.getNombre() + ": hay " + p.getStock() + ".");
            return;
        }
        pedido.put(p.getCodigo(), pedida);
        actualizarPedido();
    }

    private void quitarLinea() {
        int fila = tablaLineas.getSelectedRow();
        if (fila >= 0) {
            pedido.remove(lineas.fila(fila).codigo());
            actualizarPedido();
        }
    }

    private void emitir() {
        try {
            String cedula = consumidorFinal.isSelected() ? null : clienteElegido();
            Factura f = control.emitir(cedula, pedido);
            pedido.clear();
            refrescar();
            mensajes.informacion("Factura " + f.getNumero() + " emitida a " + f.nombreCliente()
                    + " por $" + f.total() + ".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mensajes.error(ex.getMessage());
        }
    }

    private String clienteElegido() {
        Cliente c = (Cliente) cliente.getSelectedItem();
        if (c == null) {
            throw new IllegalArgumentException("Seleccione un cliente o marque consumidor final.");
        }
        return c.cedula();
    }

    private void actualizarPedido() {
        List<LineaFactura> detalle = new ArrayList<>();
        pedido.forEach((codigo, cant) -> productos.listar().stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .ifPresent(p -> detalle.add(LineaFactura.de(p, cant))));
        lineas.mostrar(detalle);

        BigDecimal subtotal = detalle.stream().map(LineaFactura::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal iva = subtotal.multiply(Dinero.IVA).setScale(2, Dinero.REDONDEO);
        totales.setText("Subtotal $" + subtotal.setScale(2) + "   " + Dinero.ETIQUETA_IVA + " $" + iva
                + "   Total $" + subtotal.add(iva).setScale(2));
        emitir.setEnabled(!detalle.isEmpty());
    }
}
