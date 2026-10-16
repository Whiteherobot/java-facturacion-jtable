package facturacion.vista;

import facturacion.controlador.ProductoControl;
import facturacion.modelo.Producto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Registro de productos, reposición de stock y tabla del inventario.
 */
class PanelProductos extends JPanel {

    final JTextField codigo = new JTextField(8);
    final JTextField nombre = new JTextField(16);
    final JTextField precio = new JTextField(8);
    final JSpinner stock = new JSpinner(new SpinnerNumberModel(10, 0, 100_000, 1));
    final JButton registrar = new JButton("Registrar producto");
    final JSpinner reposicion = new JSpinner(new SpinnerNumberModel(10, 1, 100_000, 1));
    final JButton reponer = new JButton("Reponer seleccionado");
    final ModeloTabla<Producto> modelo = new ModeloTabla<Producto>()
            .columna("Código", Producto::getCodigo)
            .columna("Nombre", Producto::getNombre)
            .numero("Precio (sin IVA)", p -> "$" + p.getPrecio())
            .numero("Stock", Producto::getStock);
    final JTable tabla = modelo.crearTabla();

    private final ProductoControl control;
    private final Mensajes mensajes;

    PanelProductos(ProductoControl control, Mensajes mensajes) {
        super(new BorderLayout(0, 8));
        this.control = control;
        this.mensajes = mensajes;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Formulario form = new Formulario();
        form.agregar("Código", codigo);
        form.agregar("Precio", precio);
        form.agregar("Nombre", nombre);
        form.agregar("Stock inicial", stock);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        acciones.add(registrar);
        registrar.addActionListener(e -> registrar());

        JPanel superior = new JPanel(new BorderLayout());
        superior.setBorder(BorderFactory.createTitledBorder("Nuevo producto"));
        superior.add(form, BorderLayout.CENTER);
        superior.add(acciones, BorderLayout.PAGE_END);

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        inferior.add(reposicion);
        inferior.add(reponer);
        reponer.addActionListener(e -> reponer());

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(superior, BorderLayout.PAGE_START);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(inferior, BorderLayout.PAGE_END);
        refrescar();
    }

    private void registrar() {
        try {
            control.registrar(codigo.getText(), nombre.getText(), precio.getText(), (Integer) stock.getValue());
            codigo.setText("");
            nombre.setText("");
            precio.setText("");
            refrescar();
        } catch (IllegalArgumentException ex) {
            mensajes.error(ex.getMessage());
        }
    }

    private void reponer() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mensajes.error("Seleccione un producto de la tabla.");
            return;
        }
        control.reponer(modelo.fila(fila).getCodigo(), (Integer) reposicion.getValue());
        refrescar();
        tabla.setRowSelectionInterval(fila, fila);
    }

    void refrescar() {
        modelo.mostrar(control.listar());
    }
}
