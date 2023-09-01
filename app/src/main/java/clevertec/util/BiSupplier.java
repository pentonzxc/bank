package clevertec.util;

@FunctionalInterface
public interface BiSupplier<F, S> {
    public Pair<F, S> get();
}
