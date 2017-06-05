package itrx.chapter4.testing;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;


public class TestObserverExample {

    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        List<Long> expected = Arrays.asList(0L, 1L, 2L, 3L, 4L);
        final TestObserver<Long> tester =
                Observable.interval(1, TimeUnit.SECONDS, scheduler)
                          .take(5)
                          .test();

        tester.assertNoErrors();
        tester.assertNoValues();

        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        tester.assertValueSequence(expected);
        tester.assertComplete();
        tester.assertNoErrors();
        tester.assertSubscribed();
    }
}
