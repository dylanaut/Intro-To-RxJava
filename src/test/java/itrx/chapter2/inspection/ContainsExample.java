package itrx.chapter2.inspection;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class ContainsExample {

    public void exampleContains() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.contains(4L)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e)
              );
        // true
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Boolean> tester = values.contains(4L).test();

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

        tester.assertValues(true);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
