package clevertec.util;

/**
 * Class that represents pair of objects.
 */
public record Pair<F, S>(F first, S second) {

    /**
     * @param first
     * @param second
     * @return Pair<F, S>
     */
    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<F, S>(first, second);
    }
}
