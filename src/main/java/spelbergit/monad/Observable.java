package spelbergit.monad;

import java.io.Closeable;
import java.util.Map;
import java.util.WeakHashMap;

public final class Observable<V, T extends Throwable> implements Closeable {

    private boolean closed = false;
    private final Map<Observer<V, T>, Subscription<V, T>> subscriptions = new WeakHashMap<>();

    public synchronized void next(V value) {
        guardClosed();
        this.subscriptions.forEach((observer, subscription) -> observer.onNext(value));
    }

    public synchronized spelbergit.monad.Subscription<V> subscribe(Observer<V, T> observer) {
        guardClosed();
        var subscription = new Subscription<>(this);
        this.subscriptions.put(observer, subscription);
        return subscription;
    }

    public synchronized void close() {
        guardClosed();
        this.subscriptions.forEach((key, value) -> key.onComplete());
        this.closed = true;
    }

    private synchronized void unsubscribe(Subscription<V, T> subscription) {
        this.subscriptions.entrySet()
                          .stream()
                          .filter(e -> e.getValue() == subscription)
                          .findFirst()
                          .ifPresent(e -> this.subscriptions.remove(e.getKey()));
    }

    private record Subscription<V, T extends Throwable>(Observable<V, T> observable)
            implements spelbergit.monad.Subscription<V> {
        @Override
        public void close() {
            this.observable.unsubscribe(this);
        }
    }

    private void guardClosed() {
        if (this.closed) {
            throw new IllegalStateException("Observable is closed");
        }
    }
}
