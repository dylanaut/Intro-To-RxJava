package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class ZipExample {

    public void example() {
        Observable.zip(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                          .doOnNext(i -> System.out.println("Left emits " + i)),
                Observable.interval(150, TimeUnit.MILLISECONDS)
                          .doOnNext(i -> System.out.println("Right emits " + i)),
                (i1, i2) -> i1 + " - " + i2
        )
                  .take(6)
                  .subscribe(System.out::println);

        // Left emits
        // Right emits
        // 0 - 0
        // Left emits
        // Right emits
        // Left emits
        // 1 - 1
        // Left emits
        // Right emits
        // 2 - 2
        // Left emits
        // Left emits
        // Right emits
        // 3 - 3
        // Left emits
        // Right emits
        // 4 - 4
        // Left emits
        // Right emits
        // Left emits
        // 5 - 5
    }


    public void exampleZipMultiple() {
        Observable.zip(
                Observable.interval(100, TimeUnit.MILLISECONDS),
                Observable.interval(150, TimeUnit.MILLISECONDS),
                Observable.interval(050, TimeUnit.MILLISECONDS),
                (i1, i2, i3) -> i1 + " - " + i2 + " - " + i3)
                  .take(6)
                  .subscribe(System.out::println);

        // 0 - 0 - 0
        // 1 - 1 - 1
        // 2 - 2 - 2
        // 3 - 3 - 3
        // 4 - 4 - 4
        // 5 - 5 - 5
    }


    public void exampleZipUneven() {
        Observable.zip(
                Observable.range(0, 5),
                Observable.range(0, 3),
                Observable.range(0, 8),
                (i1, i2, i3) -> i1 + " - " + i2 + " - " + i3)
                  .count()
                  .subscribe(System.out::println);

        // 3
    }


    public void exampleZipWith() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .zipWith(
                          Observable.interval(150, TimeUnit.MILLISECONDS),
                          (i1, i2) -> i1 + " - " + i2)
                  .take(6)
                  .subscribe(System.out::println);

        // 0 - 0
        // 1 - 1
        // 2 - 2
        // 3 - 3
        // 4 - 4
        // 5 - 5
    }


    public void exampleZipWithIterable() {
        Observable.range(0, 5)
                  .zipWith(
                          Arrays.asList(0, 2, 4, 6, 8),
                          (i1, i2) -> i1 + " - " + i2)
                  .subscribe(System.out::println);

        // 0 - 0
        // 1 - 2
        // 2 - 4
        // 3 - 6
        // 4 - 8
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        final TestObserver<String> tester = Observable.zip(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
                Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
                (i1, i2) -> i1 + " - " + i2
        ).test();

        scheduler.advanceTimeBy(600, TimeUnit.MILLISECONDS);
        tester.assertValues();
        tester.assertNoErrors();

        tester.dispose();
    }


    @Test
    public void testZipMultiple() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<String> tester = Observable.zip(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
                Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
                Observable.interval(050, TimeUnit.MILLISECONDS, scheduler),
                (i1, i2, i3) -> i1 + " - " + i2 + " - " + i3).test();

        scheduler.advanceTimeBy(600, TimeUnit.MILLISECONDS);
        tester.assertValues();
        tester.assertNoErrors();

        tester.dispose();
    }


    @Test
    public void testZipUneven() {
        final TestObserver<Long> tester = Observable.zip(
                Observable.range(0, 5),
                Observable.range(0, 3),
                Observable.range(0, 8),
                (i1, i2, i3) -> i1 + " - " + i2 + " - " + i3)
                                                    .count().test();

        tester.assertValue(3L);

        tester.dispose();
    }


    @Test
    public void testZipWith() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<String> tester =
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .zipWith(Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
                                  (i1, i2) -> i1 + " - " + i2).test();

        scheduler.advanceTimeBy(600, TimeUnit.MILLISECONDS);
        tester.assertValues();
        tester.assertNoErrors();

        tester.dispose();
    }


    @Test
    public void testZipWithIterable() {
        final TestObserver<String> tester =
                Observable.range(0, 5)
                          .zipWith(
                                  Arrays.asList(0, 2, 4, 6, 8),
                                  (i1, i2) -> i1 + " - " + i2).test();

        tester.assertValues();
        tester.assertComplete();
        tester.assertNoErrors();

        tester.dispose();
    }
}
