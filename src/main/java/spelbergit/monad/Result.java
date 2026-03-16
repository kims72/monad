package spelbergit.monad;

import java.util.Objects;

public class Result<T, E extends Throwable> {

    private final T value;
    private final E error;

    public static <T, E extends Throwable> Result<T, E> of(T value) {
        return new Result<>(value, null);
    }

    public static <T, E extends Throwable> Result<T, E> of(ThrowingSupplier<T, E> supplier) {
        try {
            return of(supplier.get());
        } catch (Throwable t) {
            //noinspection unchecked
            return error((E) t);
        }
    }

    public static <T, E extends Throwable> Result<T, E> error(E error) {
        Objects.requireNonNull(error, "error is required");
        return new Result<>(null, error);
    }

    public static <T, E extends Throwable> Result<T, E> empty() {
        return new Result<>(null, null);
    }

    protected Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public <R> Result<R, E> map(ThrowingFunction<? super T, ? extends R, ? extends E> mapper) {
        ThrowingSupplier<R, E> supplier = (ThrowingSupplier<R, E>) () -> mapper.apply(value);
        return error == null ? of(supplier) : error(error);
    }

}
