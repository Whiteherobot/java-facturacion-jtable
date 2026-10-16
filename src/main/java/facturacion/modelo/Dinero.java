package facturacion.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Constantes de cálculo monetario.
 */
public final class Dinero {

    public static final RoundingMode REDONDEO = RoundingMode.HALF_UP;
    /** Tarifa general de IVA en Ecuador desde abril de 2024. */
    public static final BigDecimal IVA = new BigDecimal("0.15");
    public static final String ETIQUETA_IVA =
            "IVA " + IVA.movePointRight(2).stripTrailingZeros().toPlainString() + "%";
    /** Monto máximo de una factura a consumidor final, según el SRI. */
    public static final BigDecimal LIMITE_CONSUMIDOR_FINAL = new BigDecimal("50.00");

    private Dinero() {
    }

    public static BigDecimal leer(String texto) {
        try {
            return new BigDecimal(texto.strip().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException ex) {
            throw new IllegalArgumentException("Monto no válido: " + texto);
        }
    }
}
