package facturacion.vista;

/**
 * Cómo la vista avisa al usuario. En la ventana son diálogos; en las pruebas se
 * reemplaza para leer los mensajes sin abrir ventanas.
 */
interface Mensajes {

    void error(String mensaje);

    void informacion(String mensaje);
}
