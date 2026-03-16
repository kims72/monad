package spelbergit.monad;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed abstract class Response<T, E extends Throwable> {

    public static <T, E extends Throwable> Response<T, E> of(T value) {
        return value == null ? new Empty<>() : new Value<>(value);
    }

    public static <T, E extends Throwable> Response<T, E> error(E cause) {
        return new Error<>(cause);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, E extends Throwable> Response<T, E> of(Optional<T> optional) {
        return optional.<Response<T, E>>map(Response::value).orElseGet(Response::empty);
    }

    public static <T, E extends Throwable> Response<T, E> of(ThrowingSupplier<T, E> throwingSupplier) {
        try {
            T value = throwingSupplier.get();
            return of(value);
        } catch (Throwable e) {
            @SuppressWarnings("unchecked") E cause = (E) e;
            return error(cause);
        }
    }

    public static <T, E extends Throwable> Response<T, E> empty() {
        return new Empty<>();
    }

    public static <T, E extends Throwable> Response<T, E> value(T value) {
        return new Value<>(value);
    }

    private static final class Empty<T, E extends Throwable> extends Response<T, E> {
        @Override
        public T getOrElse(T other) {
            return other;
        }

        @Override
        public T getOrElse(Supplier<T> otherSupplier) {
            return otherSupplier.get();
        }

        @Override
        public <F extends Throwable> Response<T, F> getOrElseTry(ThrowingSupplier<T, F> otherSupplier) {
            return Response.of(otherSupplier);
        }

        @Override
        public T getOrThrow() throws NoSuchElementException {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public <R> Response<R, E> map(ThrowingFunction<? super T, R, ? extends E> transform) {
            return of(() -> transform.apply(null));
        }

        @Override
        public <F extends Throwable> Response<T, F> mapError(Function<? super E, ? extends F> transformCause) {
            return empty();
        }

        @Override
        public <R> Response<R, E> flatMap(Function<? super T, Response<R, ? extends E>> transform) {
            @SuppressWarnings("unchecked") Response<R, E> response = (Response<R, E>) transform.apply(null);
            return response;
        }

        @Override
        public Stream<T> streamOrThrow() {
            return Stream.empty();
        }

        @Override
        public Optional<T> optionalOrThrow() {
            return Optional.empty();
        }

    }

    private static final class Value<T, E extends Throwable> extends Response<T, E> {
        private final T value;

        public Value(T value) {
            this.value = value;
        }

        @Override
        public T getOrElse(T other) {
            return value;
        }

        @Override
        public T getOrElse(Supplier<T> otherSupplier) {
            return value;
        }

        @Override
        public <F extends Throwable> Response<T, F> getOrElseTry(ThrowingSupplier<T, F> otherSupplier) {
            return new Value<>(value);
        }

        @Override
        public T getOrThrow() {
            return value;
        }

        @Override
        public <R> Response<R, E> map(ThrowingFunction<? super T, R, ? extends E> transform) {
            return Response.of(() -> transform.apply(value));
        }

        @Override
        public <F extends Throwable> Response<T, F> mapError(Function<? super E, ? extends F> transformCause) {
            return new Value<>(value);
        }

        @Override
        public <R> Response<R, E> flatMap(Function<? super T, Response<R, ? extends E>> transform) {
            @SuppressWarnings("unchecked") Response<R, E> response = (Response<R, E>) transform.apply(value);
            return response;
        }

        @Override
        public Stream<T> streamOrThrow() {
            return Stream.of(value);
        }

        @Override
        public Optional<T> optionalOrThrow() {
            return Optional.ofNullable(value);
        }
    }

    private static final class Error<T, E extends Throwable> extends Response<T, E> {

        private final E cause;

        public Error(E cause) {
            this.cause = cause;
        }

        @Override
        public T getOrElse(T other) {
            return other;
        }

        @Override
        public T getOrElse(Supplier<T> otherSupplier) {
            return otherSupplier.get();
        }

        @Override
        public <F extends Throwable> Response<T, F> getOrElseTry(ThrowingSupplier<T, F> otherSupplier) {
            return Response.of(otherSupplier);
        }

        @Override
        public T getOrThrow() throws E {
            throw cause;
        }

        @Override
        public <R> Response<R, E> flatMap(Function<? super T, Response<R, ? extends E>> transform) {
            return error(cause);
        }

        @Override
        public <R> Response<R, E> map(ThrowingFunction<? super T, R, ? extends E> transform) {
            return error(cause);
        }

        @Override
        public <F extends Throwable> Response<T, F> mapError(Function<? super E, ? extends F> transformCause) {
            return new Error<>(transformCause.apply(cause));
        }

        @Override
        public Stream<T> streamOrThrow() throws E {
            throw cause;
        }

        @Override
        public Optional<T> optionalOrThrow() throws E {
            throw cause;
        }
    }

    public abstract T getOrElse(T other);

    public abstract T getOrElse(Supplier<T> otherSupplier);

    public abstract <F extends Throwable> Response<T, F> getOrElseTry(ThrowingSupplier<T, F> otherSupplier);

    public abstract T getOrThrow() throws E, NoSuchElementException;

    public abstract <R> Response<R, E> map(ThrowingFunction<? super T, R, ? extends E> transform);

    public abstract <R> Response<R, E> flatMap(Function<? super T, Response<R, ? extends E>> transform);

    public abstract <F extends Throwable> Response<T, F> mapError(Function<? super E, ? extends F> transformCause);

    public abstract Stream<T> streamOrThrow() throws E;

    public abstract Optional<T> optionalOrThrow() throws E;
}
