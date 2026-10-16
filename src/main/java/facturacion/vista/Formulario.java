package facturacion.vista;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Formulario en dos pares de columnas (etiqueta, campo, etiqueta, campo).
 */
class Formulario extends JPanel {

    private int indice;

    Formulario() {
        super(new GridBagLayout());
    }

    void agregar(String etiqueta, JComponent campo) {
        int fila = indice / 2;
        int columna = (indice % 2) * 2;
        indice++;

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.gridx = columna;
        c.insets = new Insets(3, 6, 3, 6);
        c.anchor = GridBagConstraints.LINE_END;
        add(new JLabel(etiqueta), c);

        c.gridx = columna + 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add(campo, c);
    }
}
