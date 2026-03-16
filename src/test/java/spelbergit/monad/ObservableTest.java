package spelbergit.monad;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObservableTest {

    @Test
    void next() {
        var result = new ArrayList<String>();

        var observable = new Observable<String, Throwable>();
        observable.subscribe(result::add);
        observable.next("First");
        observable.next("Second");
        observable.close();

        assertThrows(IllegalStateException.class, () -> observable.next("Third"));

        assertThat(result).containsExactly("First", "Second");
    }

    @Test
    void close() {
        var observable = new Observable<String, Throwable>();

        var result1 = new ArrayList<String>();
        var result2 = new ArrayList<String>();

        Subscription<String> subscription1 = observable.subscribe(result1::add);
        observable.next("First");

        Subscription<String> subscription2 = observable.subscribe(result2::add);
        observable.next("Second");

        subscription1.close();
        observable.next("Third");

        assertThat(result1).containsExactly("First", "Second");
        assertThat(result2).containsExactly("Second", "Third");
    }

    @Test
    void gc() throws InterruptedException {
        var result1 = new ArrayList<String>();
        var result2 = new ArrayList<String>();

        try (var observable = new Observable<String, Throwable>()) {
            Observer<String, Throwable> observer1 = result1::add;
            observable.subscribe(observer1);
            observable.next("First");

            Observer<String, Throwable> observer2 = result2::add;
            observable.subscribe(observer2);
            observable.next("Second");

            observer1 = null;
            assertThat(observer1).isNull();

            observable.next("Third");
        }

        assertThat(result1).containsExactly("First", "Second");
        assertThat(result2).containsExactly("Second", "Third");
    }


}