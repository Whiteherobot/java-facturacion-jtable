package facturacion.vista;

import facturacion.controlador.ClienteControl;
import facturacion.controlador.ProductoControl;

/**
 * Datos para probar la aplicación sin tener que registrar todo a mano.
 * Las cédulas son números de prueba que cumplen el dígito verificador.
 */
final class DatosEjemplo {

    private DatosEjemplo() {
    }

    static void cargar(ClienteControl clientes, ProductoControl productos) {
        clientes.registrar("0102030400", "Ana Lucía", "Torres Vega", "0991234567", "Av. Solano y 12 de Abril");
        clientes.registrar("1712345675", "Carlos", "Mendoza Ríos", "0987654321", "Av. Amazonas N24-03");

        productos.registrar("P001", "Teclado USB", "18.50", 25);
        productos.registrar("P002", "Mouse inalámbrico", "12.90", 40);
        productos.registrar("P003", "Monitor 24\"", "149.00", 6);
        productos.registrar("P004", "Cable HDMI 2 m", "6.75", 60);
    }
}
