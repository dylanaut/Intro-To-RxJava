package itrx.chapter3.leaving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import org.junit.Assert;
import org.junit.Test;

public class FirstLastSingleExample {

    public void exampleFirst() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        long value = values.take(5)
                           .filter(i -> i > 2)
                           .blockingFirst();
        System.out.println(value);

        // 3
    }


    public void exampleSingleError() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        try {
            long value = values.take(5)
                               .filter(i -> i > 2)
                               .blockingSingle();
            System.out.println(value);
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
        }

        // Caught: java.lang.IllegalArgumentException: Sequence contains too many elements
    }


    //
    // Tests
    //


    @Test
    public void testFirst() throws InterruptedException {
        List<Integer> received = new ArrayList<>();

        Observable<Integer> values = Observable.range(0, 5);

        int value = values.take(5)
                          .filter(i -> i > 2)
                          .blockingFirst();
        received.add(value);

        Assert.assertEquals(received, Arrays.asList(3));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSingleError() {
        Observable<Integer> values = Observable.range(0, 5);

        long value = values.take(5)
                           .filter(i -> i > 2)
                           .blockingSingle();
        System.out.println(value);
    }
}
