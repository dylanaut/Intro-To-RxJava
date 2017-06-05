package itrx.chapter2.transforming;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class ConcatMapExample {

    public void exampleConcatMap() {
        Observable.just(100, 150)
                  .concatMap(i ->
                          Observable.interval(i, TimeUnit.MILLISECONDS)
                                    .map(v -> i)
                                    .take(3))
                  .subscribe(
                          System.out::println,
                          System.out::println,
                          () -> System.out.println("Completed"));

        // 100
        // 100
        // 100
        // 150
        // 150
        // 150
        // Completed
    }


    //
    // Test
    //


    @Test
    public void testConcatMap() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<Integer> tester = Observable.just(100, 150)
                                                       .concatMap(i ->
                                                               Observable.interval(i, TimeUnit.MILLISECONDS, scheduler)
                                                                         .map(v -> i)
                                                                         .take(3)
                                                       ).test();

        scheduler.advanceTimeBy(750, TimeUnit.MILLISECONDS);
        tester.assertValues(0);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
