package itrx.chapter4.testing;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestObserverExample {

    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> subscriber = TestObserver.create();
        List<Long> expected = Arrays.asList(0L, 1L, 2L, 3L, 4L);
        Observable.interval(1, TimeUnit.SECONDS, scheduler)
                  .take(5)
                  .subscribe(subscriber);
        assertTrue(subscriber.getEvents().isEmpty());
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);
        subscriber.assertValueSequence(expected);
        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertNotSubscribed();
    }
}
