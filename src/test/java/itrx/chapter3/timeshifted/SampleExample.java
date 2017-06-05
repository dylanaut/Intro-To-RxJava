package itrx.chapter3.timeshifted;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class SampleExample {

    public void exampleSample() {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                  .sample(1, TimeUnit.SECONDS)
                  .take(3)
                  .subscribe(System.out::println);

        // 5
        // 12
        // 18
    }


    //
    // Test
    //


    @Test
    public void testSample() {
        TestScheduler scheduler = new TestScheduler();
        final TestObserver<Long> tester = Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
                                                  .sample(1, TimeUnit.SECONDS, scheduler)
                                                  .take(3).test();

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        tester.assertValues(5L, 12L, 18L);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
