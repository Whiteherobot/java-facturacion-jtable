package facturacion.servicio;

import facturacion.modelo.Factura;

public class FacturaServicio extends Repositorio<Factura, String> {

    public FacturaServicio() {
        super("una factura", Factura::getNumero);
    }

    /** Número con el formato del SRI: establecimiento-punto de emisión-secuencial. */
    public String siguienteNumero() {
        return String.format("001-001-%09d", listar().size() + 1);
    }
}
