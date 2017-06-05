package itrx.chapter2.inspection;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class ElementAtExample {

    public void exampleElementAt() {
        Observable<Integer> values = Observable.range(100, 10);

        values.elementAt(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 102
        // Completed
    }


    public void exampleElementAtOrDefault() {
        Observable<Integer> values = Observable.range(100, 10);

        values.elementAt(22, 0)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e));

        // 0
    }


    //
    // Tests
    //


    @Test
    public void testElementAt() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Observable<Integer> values = Observable.range(100, 10);

        values.elementAt(2)
              .test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testElementAtOrDefault() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Observable<Integer> values = Observable.range(100, 10);

        values.elementAt(22, 0).test();

        tester.assertValues(0);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
