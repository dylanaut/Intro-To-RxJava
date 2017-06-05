package itrx.chapter4.scheduling;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class UnsubscribeOnExample {

    public static void example() {
        Observable<Object> source = Observable.using(
                () -> {
                    System.out.println("Subscribed on " + Thread.currentThread().getId());
                    return Arrays.asList(1, 2);
                },
                (ints) -> {
                    System.out.println("Producing on " + Thread.currentThread().getId());
                    return Observable.fromIterable(ints);
                },
                (ints) -> {
                    System.out.println("Unubscribed on " + Thread.currentThread().getId());
                }
        );

        source.unsubscribeOn(Schedulers.newThread())
              .subscribe(System.out::println);

        // Subscribed on 1
        // Producing on 1
        // 1
        // 2
        // Unubscribed on 11
    }


    //
    // Test
    //


    @Test
    public void test() {
        long[] threads = {0, 0, 0};

        Observable<Object> source = Observable.using(
                () -> {
                    threads[0] = Thread.currentThread().getId();
                    return Arrays.asList(1, 2);
                },
                (ints) -> {
                    threads[1] = Thread.currentThread().getId();
                    return Observable.fromIterable(ints);
                },
                (ints) -> {
                    threads[2] = Thread.currentThread().getId();
                }
        );

        source.unsubscribeOn(Schedulers.newThread())
              .subscribe();

        Assert.assertEquals(threads[0], threads[1]);
        assertNotEquals(threads[0], threads[2]);
    }
}
