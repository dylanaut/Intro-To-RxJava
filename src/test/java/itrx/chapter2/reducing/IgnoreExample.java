package itrx.chapter2.reducing;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class IgnoreExample {

    public void exampleIgnoreElements() {
        Observable<Integer> values = Observable.range(0, 10);

        values.ignoreElements()
              .subscribe(
                      () -> System.out.println("Completed"),
                      e -> System.out.println("Error: " + e)
              );

        // Completed
    }


    //
    // Tests
    //


    @Test
    public void testIgnoreElements() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Observable<Integer> values = Observable.range(0, 10);

        values.ignoreElements().test();

        tester.assertValues();
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
