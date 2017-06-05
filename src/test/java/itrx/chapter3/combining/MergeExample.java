package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;
import org.reactivestreams.Subscription;

import static org.junit.Assert.assertTrue;

public class MergeExample {

    public void example() {
        Observable.merge(
                Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First"),
                Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
                  .take(10)
                  .subscribe(System.out::println);

        // Second
        // First
        // Second
        // Second
        // First
        // Second
        // Second
        // First
        // Second
        // First
    }


    public void exampleMergeWith() {
        Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First")
                  .mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
                  .take(10)
                  .subscribe(System.out::println);

        // Second
        // First
        // Second
        // Second
        // First
        // Second
        // First
        // Second
        // Second
        // First
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> tester = TestObserver.create();

        Observable.merge(
                Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
                Observable.interval(150, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
                  .take(10)
                  .distinctUntilChanged().test();

        // Each time that merge switches between the two sources,
        // distinctUntilChanged allows one more value through.
        // If more that 2 values comes through, merge is going back and forth
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        assertTrue(tester.getEvents().size() > 2);
    }


    @Test
    public void testMergeWith() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> tester = TestObserver.create();

        Observable.interval(250, TimeUnit.MILLISECONDS, scheduler).map(i -> "First")
                                            .mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
                                                                   .map(i -> "Second"))
                                            .distinctUntilChanged().test();

        // Each time that merge switches between the two sources,
        // distinctUntilChanged allows one more value through.
        // If more that 2 values comes through, merge is going back and forth
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        assertTrue(tester.getEvents().size() > 2);

        tester.dispose();
    }
}
