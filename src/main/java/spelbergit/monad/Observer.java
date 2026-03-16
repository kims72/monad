package spelbergit.monad;

@FunctionalInterface
public interface Observer<V, T extends Throwable> {
    void onNext(V value);

    default void onComplete() {
    }

    default void onError(T error) {
    }
}
