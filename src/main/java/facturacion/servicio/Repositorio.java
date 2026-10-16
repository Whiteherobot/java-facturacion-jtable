package facturacion.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Almacenamiento en memoria de entidades identificadas por una clave.
 *
 * @param <T> entidad
 * @param <K> tipo de la clave
 */
public class Repositorio<T, K> {

    private final List<T> elementos = new ArrayList<>();
    private final Function<T, K> clave;
    private final String entidad;

    protected Repositorio(String entidad, Function<T, K> clave) {
        this.entidad = entidad;
        this.clave = clave;
    }

    /** Guarda el elemento; falla si ya existe otro con la misma clave. */
    public T crear(T elemento) {
        K k = clave.apply(elemento);
        if (buscar(k).isPresent()) {
            throw new IllegalArgumentException("Ya existe " + entidad + " con " + k);
        }
        elementos.add(elemento);
        return elemento;
    }

    public Optional<T> buscar(K k) {
        return elementos.stream().filter(e -> clave.apply(e).equals(k)).findFirst();
    }

    public T obtener(K k) {
        return buscar(k).orElseThrow(() -> new IllegalArgumentException("No existe " + entidad + " con " + k));
    }

    public List<T> listar() {
        return Collections.unmodifiableList(elementos);
    }
}
