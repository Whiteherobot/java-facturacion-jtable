package facturacion.vista;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Modelo de JTable que muestra una lista de objetos, una columna por cada
 * función que extrae un valor del objeto.
 */
class ModeloTabla<T> extends AbstractTableModel {

    private final List<String> columnas = new ArrayList<>();
    private final List<Function<T, Object>> valores = new ArrayList<>();
    private final List<Integer> alineadasDerecha = new ArrayList<>();
    private List<T> filas = List.of();

    ModeloTabla<T> columna(String nombre, Function<T, Object> valor) {
        columnas.add(nombre);
        valores.add(valor);
        return this;
    }

    /** Columna numérica (montos, cantidades): se alinea a la derecha. */
    ModeloTabla<T> numero(String nombre, Function<T, Object> valor) {
        alineadasDerecha.add(columnas.size());
        return columna(nombre, valor);
    }

    /** Crea la tabla con este modelo y las columnas numéricas alineadas a la derecha. */
    JTable crearTabla() {
        JTable tabla = new JTable(this);
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.TRAILING);
        alineadasDerecha.forEach(i -> tabla.getColumnModel().getColumn(i).setCellRenderer(derecha));
        tabla.setFillsViewportHeight(true);
        return tabla;
    }

    void mostrar(List<T> nuevasFilas) {
        filas = List.copyOf(nuevasFilas);
        fireTableDataChanged();
    }

    T fila(int indice) {
        return filas.get(indice);
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.size();
    }

    @Override
    public String getColumnName(int columna) {
        return columnas.get(columna);
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return valores.get(columna).apply(filas.get(fila));
    }
}
