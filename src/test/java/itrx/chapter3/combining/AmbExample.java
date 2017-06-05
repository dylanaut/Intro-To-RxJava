package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class AmbExample {

    public void exampleAmb() {
        final Observable<Long> timer1 = Observable.timer(100, TimeUnit.MILLISECONDS);
        final Observable<Long> timer2 = Observable.timer(50, TimeUnit.MILLISECONDS);

        Observable.ambArray(timer1.map((l -> "First")),
                timer2.map((l1 -> "Second"))).subscribe(System.out::println);

        // Second
    }


    public void exampleAmbWith() {
        Observable.timer(100, TimeUnit.MILLISECONDS).map(i -> "First")
                  .ambWith(Observable.timer(50, TimeUnit.MILLISECONDS).map(i -> "Second"))
                  .ambWith(Observable.timer(70, TimeUnit.MILLISECONDS).map(i -> "Third"))
                  .subscribe(System.out::println);

        // Second
    }


    //
    // Test
    //


    @Test
    public void testAmb() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<String> tester = Observable.ambArray(
                Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).map(i -> "First"),
                Observable.timer(50, TimeUnit.MILLISECONDS, scheduler).map(i -> "Second"))
                                                      .test();

        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        tester.assertValues("Second");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testAmbWith() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<String> tester =
                Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).map(i -> "First")
                          .ambWith(Observable.timer(50, TimeUnit.MILLISECONDS, scheduler)
                                             .map(i -> "Second"))
                          .ambWith(Observable.timer(70, TimeUnit.MILLISECONDS, scheduler)
                                             .map(i -> "Third"))
                          .test();

        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        tester.assertValues("Second");
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
