package facturacion.vista;

import facturacion.controlador.ClienteControl;
import facturacion.modelo.Cliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Registro de clientes y tabla con los registrados.
 */
class PanelClientes extends JPanel {

    final JTextField cedula = new JTextField(10);
    final JTextField nombres = new JTextField(14);
    final JTextField apellidos = new JTextField(14);
    final JTextField telefono = new JTextField(10);
    final JTextField direccion = new JTextField(14);
    final JButton registrar = new JButton("Registrar cliente");
    final ModeloTabla<Cliente> modelo = new ModeloTabla<Cliente>()
            .columna("Cédula", Cliente::cedula)
            .columna("Nombres", Cliente::nombres)
            .columna("Apellidos", Cliente::apellidos)
            .columna("Teléfono", Cliente::telefono)
            .columna("Dirección", Cliente::direccion);

    private final ClienteControl control;
    private final Mensajes mensajes;

    PanelClientes(ClienteControl control, Mensajes mensajes) {
        super(new BorderLayout(0, 8));
        this.control = control;
        this.mensajes = mensajes;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Formulario form = new Formulario();
        form.agregar("Cédula", cedula);
        form.agregar("Teléfono", telefono);
        form.agregar("Nombres", nombres);
        form.agregar("Dirección", direccion);
        form.agregar("Apellidos", apellidos);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        acciones.add(registrar);
        registrar.addActionListener(e -> registrar());

        JPanel superior = new JPanel(new BorderLayout());
        superior.setBorder(BorderFactory.createTitledBorder("Nuevo cliente"));
        superior.add(form, BorderLayout.CENTER);
        superior.add(acciones, BorderLayout.PAGE_END);

        add(superior, BorderLayout.PAGE_START);
        add(new JScrollPane(modelo.crearTabla()), BorderLayout.CENTER);
        refrescar();
    }

    private void registrar() {
        try {
            control.registrar(cedula.getText(), nombres.getText(), apellidos.getText(),
                    telefono.getText(), direccion.getText());
            for (JTextField campo : new JTextField[] {cedula, nombres, apellidos, telefono, direccion}) {
                campo.setText("");
            }
            refrescar();
        } catch (IllegalArgumentException ex) {
            mensajes.error(ex.getMessage());
        }
    }

    void refrescar() {
        modelo.mostrar(control.listar());
    }
}
