package itrx.chapter2.creating;

import java.util.Arrays;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class FromExample {

    public void exampleFromFuture() {
        FutureTask<Integer> f = new FutureTask<Integer>(() -> {
            Thread.sleep(2000);
            return 21;
        });
        new Thread(f).start();

        Observable<Integer> values = Observable.fromFuture(f);

        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Received: 21
        // Completed
    }


    public void exampleFromFutureTimeout() {
        FutureTask<Integer> f = new FutureTask<Integer>(() -> {
            Thread.sleep(2000);
            return 21;
        });
        new Thread(f).start();

        Observable<Integer> values = Observable.fromFuture(f, 1000, TimeUnit.MILLISECONDS);

        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Error: java.util.concurrent.TimeoutException
    }


    public void exampleFromArray() {
        Integer[] is = {1, 2, 3};
        Observable<Integer> values = Observable.fromArray(is);
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Received: 1
        // Received: 2
        // Received: 3
        // Completed
    }


    public void exampleFromIterable() {
        Iterable<Integer> input = Arrays.asList(1, 2, 3);
        Observable<Integer> values = Observable.fromIterable(input);
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Received: 1
        // Received: 2
        // Received: 3
        // Completed
    }


    //
    // Tests
    //


    @Test
    public void testFromFuture() {
        FutureTask<Integer> f = new FutureTask<>(() -> {
            return 21;
        });
        new Thread(f).start();

        Observable<Integer> values = Observable.fromFuture(f);

        final TestObserver<Integer> tester = values.test();

        tester.assertValues(21);
        tester.assertNoErrors();
        tester.assertComplete();
    }


    @Test
    public void testFromArray() {
        Integer[] input = {1, 2, 3};
        Observable<Integer> values = Observable.fromArray(input);
        final TestObserver<Integer> tester = values.test();

        tester.assertValues(input);
        tester.assertNoErrors();
        tester.assertComplete();
    }


    @Test
    public void testFromIterable() {
        Iterable<Integer> input = Arrays.asList(1, 2, 3);
        Observable<Integer> values = Observable.fromIterable(input);
        final TestObserver<Integer> tester = values.test();

        tester.assertValueSequence(input);
        tester.assertNoErrors();
        tester.assertComplete();
    }
}
