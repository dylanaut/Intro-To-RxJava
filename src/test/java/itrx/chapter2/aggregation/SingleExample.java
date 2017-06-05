package itrx.chapter2.aggregation;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;

public class SingleExample {
    public void exampleSingle() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(10)
              .single(5L); // Emits a result.subscribe(PrintObserver.printObserver("Single1"));
        values.single(5L); // Never emits.subscribe(PrintObserver.printObserver("Single2"));

        // Single1: 5
        // Single1: Completed
    }


    public void exampleSingleOrDefault() {
        Single<Integer> values = Single.just(null);

        values.onErrorReturnItem(-1)
              .subscribe(printObserver("SingleOrDefault"));

        // SingleOrDefault: -1
        // SingleOrDefault: Completed
    }


    //
    // Tests
    //


    @Test
    public void testSingle() {
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        // Emits a result.subscribe(tester1);
        final TestObserver<Long> tester1 = values.take(10).single(5L).test();
        // Never emits.subscribe(tester2);
        final TestObserver<Long> tester2 = values.single(5L).test();

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        tester1.assertValues(5L);
        tester1.assertComplete();
        tester1.assertNoErrors();
        tester2.assertValues();
        Assert.assertEquals(tester2.getEvents().size(), 0);
        tester2.assertNoErrors();
    }


    @Test
    public void testSingleOrDefault() {
        Observable<Long> values = Observable.just(0L);

        final TestObserver<Long> tester = values.first(-1L).test();

        tester.assertValues(-1L);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
