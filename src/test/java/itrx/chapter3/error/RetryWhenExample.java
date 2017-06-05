package itrx.chapter3.error;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class RetryWhenExample {

    public void example() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Failed"));
        });

        source.retryWhen((o) -> o.take(2)
                                 .delay(100, TimeUnit.MILLISECONDS))
              .timeInterval()
              .subscribe(
                      System.out::println,
                      System.out::println);

        // TimeInterval [intervalInMilliseconds=17, value=1]
        // TimeInterval [intervalInMilliseconds=0, value=2]
        // TimeInterval [intervalInMilliseconds=102, value=1]
        // TimeInterval [intervalInMilliseconds=0, value=2]
        // TimeInterval [intervalInMilliseconds=102, value=1]
        // TimeInterval [intervalInMilliseconds=0, value=2]
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> intervals = TestObserver.create();

        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Failed"));
        });
        source.retryWhen((o) -> o.take(2).delay(100, TimeUnit.MILLISECONDS, scheduler))
              .timeInterval(scheduler)
              .map(timed -> timed.time(TimeUnit.MILLISECONDS))
              .subscribe(intervals);

        scheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS);
        intervals.assertValues(0L, 0L, 100L, 0L, 100L, 0L);
        intervals.assertComplete();
        intervals.assertNoErrors();
    }
}
