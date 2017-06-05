package itrx.chapter4.backpressure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoBackpressureExample {

    public void exampleSynchronous() {
        // Produce
        Observable<Integer> producer = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        });
        // Consume
        producer.subscribe(i -> {
            try {
                Thread.sleep(1000);
                System.out.println(i);
            }
            catch (Exception e) {
            }
        });

        // 1
        // 2
    }


    public void exampleNoBackpressure() {
        Observable.interval(1, TimeUnit.MILLISECONDS)
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
        // io.reactivex.exceptions.MissingBackpressureException
    }


    //
    // Tests
    //


    @Test
    public void testSynchronous() {
        List<String> execution = new ArrayList<String>();

        // Produce
        Observable<Integer> producer = Observable.create(o -> {
            execution.add("Producing 1");
            o.onNext(1);
            execution.add("Producing 2");
            o.onNext(2);
            o.onComplete();
        });
        // Consume
        producer.subscribe(i -> execution.add("Processed " + i));

        Assert.assertEquals(
                Arrays.asList(
                        "Producing 1",
                        "Processed 1",
                        "Producing 2",
                        "Processed 2"
                ),
                execution);
    }


    @Test
    public void testNoBackpressure() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester = new TestObserver<Long>() {
            @Override
            public void onNext(Long t) {
                scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
                super.onNext(t);
            }
        };

        Observable.interval(1, TimeUnit.MILLISECONDS, scheduler)
                  .observeOn(scheduler).test();

        scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
        assertThat(tester.errors().get(0))
                .isInstanceOf(MissingBackpressureException.class);
    }
}
