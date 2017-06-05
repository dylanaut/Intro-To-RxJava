package itrx.chapter2.inspection;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

public class AllExample {

    public void exampleAll() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(0);
            o.onNext(10);
            o.onNext(10);
            o.onNext(2);
            o.onComplete();
        });


        values.all(i -> i % 2 == 0)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e));

        // true
        // Completed
    }


    public void exampleAllEarlyFalse() {
        Observable<Long> values = Observable.interval(150, TimeUnit.MILLISECONDS).take(5);

        Disposable subscription = values.all(i -> i < 3)
                                        .subscribe(
                                                v -> System.out.println("All: " + v),
                                                e -> System.out.println("All: Error: " + e)
                                        );
        Disposable subscription2 = values.subscribe(
                System.out::println,
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        subscription.dispose();
        subscription2.dispose();

        // 0
        // 1
        // 2
        // All: false
        // 3
        // 4
        // Completed
    }


    public void exampleAllError() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(0);
            o.onNext(2);
            o.onError(new Exception());
        });

        values.all(i -> i % 2 == 0)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e)
              );

        // Error: java.lang.Exception
    }


    public void exampleAllErrorAfterComplete() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception());
        });

        values.all(i -> i % 2 == 0)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e)
              );

        // false
        // Completed
    }


    //
    // Tests for examples
    //


    @Test
    public void testAll() {
        TestObserver<Boolean> tester = new TestObserver<Boolean>();

        Observable<Integer> values = Observable.create(o -> {
            o.onNext(0);
            o.onNext(10);
            o.onNext(10);
            o.onNext(2);
            o.onComplete();
        });


        values.all(i -> i % 2 == 0).test();

        tester.assertValues(true);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testAllEarlyFalse() {
        TestObserver<Long> testerSrc = new TestObserver<Long>();
        TestObserver<Boolean> testerAll = new TestObserver<Boolean>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values =
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .take(5);

        values.all(i -> i < 3).subscribe(testerAll);
        values.subscribe(testerSrc);

        scheduler.advanceTimeBy(450, TimeUnit.MILLISECONDS);

        testerAll.assertValues(false);
        testerAll.assertComplete();
        testerAll.assertNoErrors();
        testerSrc.assertValues(0L, 1L, 2L, 3L);

        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);

        testerSrc.assertValues(0L, 1L, 2L, 3L, 4L);
        testerSrc.assertComplete();
        testerSrc.assertNoErrors();
    }


    @Test
    public void testAllError() {
        TestObserver<Boolean> tester = TestObserver.create();

        Observable<Integer> values = Observable.create(o -> {
            o.onNext(0);
            o.onNext(2);
            o.onError(new Exception());
        });

        values.all(i -> i % 2 == 0).test();

        tester.assertValues();
        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1);
    }


    @Test
    public void testAllErrorAfterComplete() {
        TestObserver<Boolean> tester = TestObserver.create();

        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception());
        });

        values.all(i -> i % 2 == 0).test();

        tester.assertValues(false);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
