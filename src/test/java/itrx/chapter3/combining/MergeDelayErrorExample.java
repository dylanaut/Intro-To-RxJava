package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class MergeDelayErrorExample {

    public void example1Error() {
        Observable<Long> failAt200 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS).take(2),
                        Observable.error(new Exception("Failed")));
        Observable<Long> completeAt400 =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                          .take(4);

        Observable.mergeDelayError(failAt200, completeAt400)
                  .subscribe(
                          System.out::println,
                          System.out::println);

        // 0
        // 0
        // 1
        // 1
        // 2
        // 3
        // java.lang.Exception: Failed
    }


    public void example2Errors() {
        Observable<Long> failAt200 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS).take(2),
                        Observable.error(new Exception("Failed")));
        Observable<Long> failAt300 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                        Observable.error(new Exception("Failed")));
        Observable<Long> completeAt400 =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                          .take(4);

        Observable.mergeDelayError(failAt200, failAt300, completeAt400)
                  .subscribe(
                          System.out::println,
                          System.out::println);

        // 0
        // 0
        // 0
        // 1
        // 1
        // 1
        // 2
        // 2
        // 3
        // io.reactivex.exceptions.CompositeException: 2 exceptions occurred.
    }


    //
    // Tests
    //


    @Test
    public void test1Error() {        TestScheduler scheduler = new TestScheduler();

        Observable<Long> failAt200 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(2),
                        Observable.error(new Exception("Failed")));
        Observable<Long> completeAt400 =
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .take(4);

        final TestObserver<Long> tester = Observable.mergeDelayError(failAt200, completeAt400).test();

        scheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 0L, 1L, 1L, 2L, 3L);
        assertThat(tester.errors().get(0), instanceOf(Exception.class));
    }


    @Test
    public void test2Errors() {        TestScheduler scheduler = new TestScheduler();

        Observable<Long> failAt200 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(2),
                        Observable.error(new Exception("Failed")));
        Observable<Long> failAt300 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                        Observable.error(new Exception("Failed")));
        Observable<Long> completeAt400 =
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .take(4);

        final TestObserver<Long> tester = Observable.mergeDelayError(failAt200, failAt300, completeAt400).test();

        scheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 0L, 0L, 1L, 1L, 1L, 2L, 2L, 3L);
        assertThat(tester.errors().get(0), instanceOf(CompositeException.class));
    }
}
