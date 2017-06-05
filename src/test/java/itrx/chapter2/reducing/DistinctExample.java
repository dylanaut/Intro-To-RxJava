package itrx.chapter2.reducing;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class DistinctExample {

    public void exampleDistinct() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onNext(2);
            o.onComplete();
        });

        values.distinct()
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 1
        // 2
        // 3
        // Completed
    }


    public void exampleDistinctKey() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("First");
            o.onNext("Second");
            o.onNext("Third");
            o.onNext("Fourth");
            o.onNext("Fifth");
            o.onComplete();
        });

        values.distinct(v -> v.charAt(0))
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // First
        // Second
        // Third
        // Completed
    }


    public void exampleDistinctUntilChanged() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onNext(2);
            o.onComplete();
        });

        values.distinctUntilChanged()
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 1
        // 2
        // 3
        // 2
        // Completed
    }


    public void exampleDistinctUntilChangedKey() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("First");
            o.onNext("Second");
            o.onNext("Third");
            o.onNext("Fourth");
            o.onNext("Fifth");
            o.onComplete();
        });

        values.distinctUntilChanged(v -> v.charAt(0))
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // First
        // Second
        // Third
        // Fourth
        // Completed
    }


    //
    // Tests
    //


    @Test
    public void testDistinct() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onNext(2);
            o.onComplete();
        });

        final TestObserver<Integer> tester = values.distinct().test();

        tester.assertValues(3);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testDistinctKey() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("First");
            o.onNext("Second");
            o.onNext("Third");
            o.onNext("Fourth");
            o.onNext("Fifth");
            o.onComplete();
        });

        final TestObserver<String> tester = values.distinct(v -> v.charAt(0)).test();

        tester.assertValues("First", "Second", "Third");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testDistinctUntilChanged() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onNext(2);
            o.onComplete();
        });

        final TestObserver<Integer> tester = values.distinctUntilChanged().test();

        tester.assertValues(1, 2, 3, 2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testDistinctUntilChangedKey() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("First");
            o.onNext("Second");
            o.onNext("Third");
            o.onNext("Fourth");
            o.onNext("Fifth");
            o.onComplete();
        });

        final TestObserver<String> tester = values.distinctUntilChanged(v -> v.charAt(0)).test();

        tester.assertValues("First", "Second", "Third", "Fourth");
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
