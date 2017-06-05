package itrx.chapter2.inspection;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class ExistsExample {

    public void exampleFalse() {
        Observable<Integer> values = Observable.range(0, 2);

        values.any(i -> i > 2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e));

        // false
    }


    public void exampleTrue() {
        Observable<Integer> values = Observable.range(0, 4);

        values.any(i -> i > 2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e));

        // true
    }


    //
    // Tests
    //


    @Test
    public void testFalse() {
        Observable<Integer> values = Observable.range(0, 2);

        final TestObserver<Boolean> tester = values.any(i -> i > 2).test();

        tester.assertValues(false);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testTrue() {
        Observable<Integer> values = Observable.range(0, 4);

        final TestObserver<Boolean> tester = values.any(i -> i > 2).test();

        tester.assertValues(true);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
