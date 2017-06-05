package itrx.chapter4.backpressure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class OnBackpressureExample {

    public void exampleOnBackpressureBuffer() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
                  .onBackpressureBuffer(1000)
                  .observeOn(Schedulers.newThread())
                  .subscribe(
                          i -> {
                              System.out.println(i);
                              try {
                                  Thread.sleep(100);
                              }
                              catch (Exception e) {
                              }
                          },
                          System.out::println
                  );

        // 0
        // 1
        // 2
        // 3
        // 4
        // 5
        // 6
        // 7
        // 8
        // 9
        // 10
        // 11
        // io.reactivex.exceptions.MissingBackpressureException: Overflowed buffer of 1000
    }


    public void exampleOnBackpressureDrop() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(Schedulers.newThread())
                .subscribe(
                        i -> {
                            System.out.println(i);
                            try {
                                Thread.sleep(100);
                            }
                            catch (Exception e) {
                            }
                        },
                        System.out::println);

        // 0
        // 1
        // 2
        // ...
        // 126
        // 127
        // 12861
        // 12862
        // ...
    }


    //
    // Test
    //


    @Test
    public void testOnBackpressureBuffer() {
        TestScheduler scheduler = new TestScheduler();
        List<Long> received = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        ControlledPullSubscriber<Long> tester = new ControlledPullSubscriber<Long>(
                received::add,
                errors::add);

        // Subscriber accepts items once every 100ms
        scheduler.createWorker().schedulePeriodically(
                () -> tester.requestMore(1),
                0, 100, TimeUnit.MILLISECONDS);

        Flowable.interval(1, TimeUnit.MILLISECONDS, scheduler)
                .onBackpressureBuffer(1000)
                .observeOn(scheduler).test();

        scheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L), received);
        assertThat(
                errors.get(0),
                instanceOf(io.reactivex.exceptions.MissingBackpressureException.class));
    }


    @Test
    public void testOnBackpressureDrop() {
        TestScheduler scheduler = new TestScheduler();
        List<Long> received = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        ControlledPullSubscriber<Long> tester = new ControlledPullSubscriber<Long>(
                received::add,
                errors::add);

        // Subscriber accepts items once every 100ms
        scheduler.createWorker().schedulePeriodically(
                () -> tester.requestMore(1),
                0, 100, TimeUnit.MILLISECONDS);

        Flowable.interval(1, TimeUnit.MILLISECONDS, scheduler)
                .onBackpressureDrop()
                .observeOn(scheduler).test();

        scheduler.advanceTimeBy(13000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(129L, received.get(129).longValue());
        assertNotEquals(130L, received.get(130).longValue());
    }
}
