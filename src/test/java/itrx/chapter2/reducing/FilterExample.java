package itrx.chapter2.reducing;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class FilterExample {


    public void example() {
        Observable<Integer> values = Observable.range(0, 10);
        values.filter(v -> v % 2 == 0)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 0
        // 2
        // 4
        // 6
        // 8
        // Completed
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Observable<Integer> values = Observable.range(0, 10);
        values.filter(v -> v % 2 == 0).test();

        tester.assertValues(8);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
