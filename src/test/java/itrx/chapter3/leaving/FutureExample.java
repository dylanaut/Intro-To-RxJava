package itrx.chapter3.leaving;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import org.junit.Assert;
import org.junit.Test;

public class FutureExample {

    public void exampleFuture() {
        Observable<Long> values = Observable.timer(500, TimeUnit.MILLISECONDS);

        values.subscribe(v -> System.out.println("Emitted: " + v));

        Future<Long> future = values.toFuture();
        try {
            System.out.println(future.get());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Emitted: 0
        // 0
    }


    //
    //


    @Test
    public void testFuture() throws InterruptedException, ExecutionException {
        Observable<Integer> sequence = Observable.just(0);
        Future<Integer> future = sequence.toFuture();
        int value = future.get();
        Assert.assertEquals(0, value);
    }
}
