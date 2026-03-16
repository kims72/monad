package spelbergit.monad;

import java.io.Closeable;

public interface Subscription<T> extends Closeable {

    @Override
    public void close();
}
