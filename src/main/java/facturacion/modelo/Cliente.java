package facturacion.modelo;

/**
 * Cliente identificado por su cédula ecuatoriana.
 */
public record Cliente(String cedula, String nombres, String apellidos, String telefono, String direccion) {

    public Cliente {
        if (!esCedulaValida(cedula)) {
            throw new IllegalArgumentException("La cédula no es válida");
        }
        nombres = requerido(nombres, "Los nombres");
        apellidos = requerido(apellidos, "Los apellidos");
        telefono = telefono == null ? "" : telefono.strip();
        direccion = direccion == null ? "" : direccion.strip();
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    /** 10 dígitos, provincia 01-24 o 30, tercer dígito menor que 6 y verificador por módulo 10. */
    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if ((provincia < 1 || provincia > 24) && provincia != 30 || cedula.charAt(2) - '0' >= 6) {
            return false;
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int producto = (cedula.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += producto > 9 ? producto - 9 : producto;
        }
        return (10 - suma % 10) % 10 == cedula.charAt(9) - '0';
    }

    static String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " son obligatorios");
        }
        return valor.strip();
    }

    @Override
    public String toString() {
        return nombreCompleto() + " (" + cedula + ")";
    }
}
