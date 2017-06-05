package itrx.chapter3.timeshifted;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class TakeLastBufferExample {

    public void exampleByCount() {
        Observable.range(0, 5)
                  .buffer(2)
                  .lastElement()
                  .subscribe(System.out::println);

        // [3, 4]
    }


    public void exampleByTime() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .take(5)
                  .buffer(200, TimeUnit.MILLISECONDS)
                  .lastElement()
                  .subscribe(System.out::println);

        // [2, 3, 4]
    }


    public void exampleByCountAndTime() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .take(5)
                  .buffer(2, 200, TimeUnit.MILLISECONDS)
                  .lastElement()
                  .subscribe(System.out::println);

        // [3, 4]
    }


    //
    // Tests
    //


    @Test
    public void testByCount() {
        TestObserver<List<Integer>> tester = TestObserver.create();

        Observable.range(0, 5)
                  .buffer(2).lastElement().test();

        tester.assertValueSequence(Arrays.asList(
                Arrays.asList(3, 4)
        ));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testByTime() {
        TestObserver<List<Long>> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                  .take(5)
                  .buffer(200, TimeUnit.MILLISECONDS, scheduler).lastElement().test();

        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester.assertValueSequence(Arrays.asList(
                Arrays.asList(2L, 3L, 4L)
        ));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testByCountAndTime() {
        TestObserver<List<Long>> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                  .take(5)
                  .buffer(2, 200, TimeUnit.MILLISECONDS, scheduler)
                  .lastElement()
                  .test();

        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester.assertValueSequence(Arrays.asList(
                Arrays.asList(3L, 4L)
        ));
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
