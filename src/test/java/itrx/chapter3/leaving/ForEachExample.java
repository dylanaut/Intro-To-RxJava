package itrx.chapter3.leaving;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

public class ForEachExample {

    public void exampleObservableForEach() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(5)
              .forEach(
                      System.out::println);
        System.out.println("Subscribed");

        // Subscribed
        // 0
        // 1
        // 2
        // 3
        // 4
    }


    public void exampleBlockingForEach() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(5)
              .blockingForEach(System.out::println);
        System.out.println("Subscribed");

        // 0
        // 1
        // 2
        // 3
        // 4
        // Subscribed
    }


    public void exampleBlockingForEachError() {
        Observable<Long> values = Observable.error(new Exception("Oops"));

        try {
            values.take(5)
                  .blockingForEach(System.out::println);
        }
        catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }
        System.out.println("Subscribed");

        // Caught: java.lang.Exception: Oops
        // Subscribed
    }


    //
    // Tests
    //


    @Test
    public void testObservableForEach() {
        List<Long> received = new ArrayList<>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        values.take(5)
              .forEach(i -> received.add(i));
        received.add(-1L); // Mark that forEach statement returned

        Assert.assertEquals(received, Arrays.asList(-1L));
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Assert.assertEquals(received, Arrays.asList(-1L, 0L, 1L, 2L, 3L, 4L));
    }


    @Test
    public void testBlockingForEach() throws InterruptedException {
        List<Long> received = new ArrayList<>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        // Blocking call on new thread
        Thread thread = new Thread(() -> {
            values.take(5)
                  .blockingForEach(
                          i -> received.add(i));
            received.add(-1L); // Mark that forEach statement returned

        });
        thread.start();

        Assert.assertEquals(received, Arrays.asList());
        // Wait for blocking call to block before producing values
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // Wait for processing to complete
        thread.join(50);
        Assert.assertEquals(received, Arrays.asList(0L, 1L, 2L, 3L, 4L, -1L));
    }


    @Test(expected = Exception.class)
    public void testBlockingForEachError() {
        Observable<Long> values = Observable.error(new Exception("Oops"));

        values.take(5)
              .blockingForEach(v -> {
              });
    }
}
