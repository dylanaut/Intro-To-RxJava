package itrx.chapter2.aggregation;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;

public class FirstExample {

    public void exampleFirst() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.firstElement()
              .subscribe(printObserver("First"));
        // 0
    }


    public void exampleFirstWithPredicate() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.filter(v -> v > 5)
              .first(999L)
              .subscribe(printObserver("First"));
        // 6
    }


    public void exampleFirstOrDefault() {
        Observable<Long> values = Observable.empty();

        values.first(-1L)
              .subscribe(printObserver("First"));

        // -1
    }


    public void exampleFirstOrDefaultWithPredicate() {
        Observable<Long> values = Observable.empty();

        values.filter(v -> v > 5)
              .first(-1L)
              .subscribe(printObserver("First"));
        // -1
    }


    //
    // Tests
    //


    @Test
    public void testFirst() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.firstElement().test();

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        tester.assertValues(0L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFirstWithPredicate() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.filter(v -> v > 5)
                                              .firstElement().test();

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        tester.assertValues(6L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFirstOrDefault() {
        Observable<Long> values = Observable.empty();

        final TestObserver<Long> tester = values.first(-1L).test();

        tester.assertValues(-1L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFirstOrDefaultWithPredicate() {
        Observable<Long> values = Observable.empty();

        final TestObserver<Long> tester = values.filter(v -> v > 5)
                                              .first(-1L).test();

        tester.assertValues(-1L);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}


