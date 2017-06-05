package itrx.chapter2.reducing;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class TakeSkipExample {

    public void exampleTake() {
        Observable<Integer> values = Observable.range(0, 5);

        values.take(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 0
        // 1
        // Completed
    }


    public void exampleSkip() {
        Observable<Integer> values = Observable.range(0, 5);

        values.skip(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 2
        // 3
        // 4
        // Completed
    }


    public void exampleTakeTime() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        Disposable disposable = values.take(250, TimeUnit.MILLISECONDS)
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e),
                                              () -> System.out.println("Completed")
                                      );

        disposable.dispose();

        // 0
        // 1
        // Completed
    }


    public void exampleSkipTime() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        Disposable disposable = values.skip(250, TimeUnit.MILLISECONDS)
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e),
                                              () -> System.out.println("Completed")
                                      );

        disposable.dispose();

        // 2
        // 3
        // 4
        // Completed
    }


    public void exampleTakeWhile() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        Disposable disposable = values.takeWhile(v -> v < 2)
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e),
                                              () -> System.out.println("Completed")
                                      );

        disposable.dispose();

        // 0
        // 1
        // Completed
    }


    public void exampleSkipWhile() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        Disposable disposable = values.skipWhile(v -> v < 2)
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e),
                                              () -> System.out.println("Completed")
                                      );

        disposable.dispose();

        // 2
        // 3
        // 4
        // ...
    }


    public void exampleSkipLast() {
        Observable<Integer> values = Observable.range(0, 5);

        values.skipLast(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 0
        // 1
        // 2
        // Completed
    }


    public void exampleTakeLast() {
        Observable<Integer> values = Observable.range(0, 5);

        values.takeLast(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 3
        // 4
        // ...
    }


    public void exampleTakeUntil() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

        Disposable disposable = values.takeUntil(cutoff)
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e),
                                              () -> System.out.println("Completed")
                                      );

        disposable.dispose();

        // 0
        // 1
        // Completed
    }


    public void exampleSkipUntil() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

        Disposable disposable = values.skipUntil(cutoff)
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e),
                                              () -> System.out.println("Completed")
                                      );

        disposable.dispose();

        // 2
        // 3
        // 4
        // ...
    }


    //
    // Tests
    //


    @Test
    public void testTake() {
        Observable<Integer> values = Observable.range(0, 5);

        final TestObserver<Integer> tester = values.take(2).test();

        tester.assertValues(0, 1);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testSkip() {
        Observable<Integer> values = Observable.range(0, 5);

        final TestObserver<Integer> tester = values.skip(2).test();

        tester.assertValues(2, 3, 4);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testTakeTime() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.take(250, TimeUnit.MILLISECONDS, scheduler).test();

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        tester.dispose();

        tester.assertValues(0L, 1L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testSkipTime() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.skip(250, TimeUnit.MILLISECONDS, scheduler).test();

        scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
        tester.dispose();

        tester.assertValues(2L, 3L, 4L);
        tester.assertNoErrors();
    }


    @Test
    public void testTakeWhile() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.takeWhile(v -> v < 2).test();

        scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
        tester.dispose();

        tester.assertValues(0L, 1L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testSkipWhile() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.skipWhile(v -> v < 2).test();

        scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
        tester.dispose();

        tester.assertValues(2L, 3L, 4L);
        tester.assertNoErrors();
    }


    @Test
    public void testerSkipLast() {
        Observable<Integer> values = Observable.range(0, 5);

        final TestObserver<Integer> tester = values.skipLast(2).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testTakeLast() {
        Observable<Integer> values = Observable.range(0, 5);

        final TestObserver<Integer> tester = values.takeLast(2).test();

        tester.assertValues(3, 4);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testTakeUntil() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);
        Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.takeUntil(cutoff).test();

        scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
        tester.dispose();

        tester.assertValues(0L, 1L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testSkipUntil() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);
        Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Long> tester = values.skipUntil(cutoff).test();

        scheduler.advanceTimeBy(550, TimeUnit.MILLISECONDS);
        tester.dispose();

        tester.assertValues(2L, 3L, 4L);
        tester.assertNoErrors();
    }
}
