package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class SwitchMapExample {

    public void example() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .switchMap(i ->
                          Observable.interval(30, TimeUnit.MILLISECONDS)
                                    .map(l -> i))
                  .take(9)
                  .subscribe(System.out::println);

        // 0
        // 0
        // 0
        // 1
        // 1
        // 1
        // 2
        // 2
        // 2
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<Long> tester =
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .switchMap(i ->
                                  Observable.interval(30, TimeUnit.MILLISECONDS, scheduler)
                                            .map(l -> i))
                          .take(9)
                          .distinctUntilChanged().test();

        scheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 1L, 2L);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
