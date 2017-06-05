package itrx.chapter2.inspection;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

public class DefaultIfEmptyExample {

    public void exampleDefaultIfEmpty() {
        Observable<Integer> values = Observable.empty();

        values.defaultIfEmpty(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // 2
        // Completed
    }


    public void exampleDefaultIfEmptyError() {
        Observable<Integer> values = Observable.error(new Exception());

        values.defaultIfEmpty(2)
              .subscribe(
                      System.out::println,
                      e -> System.out.println("Error: " + e),
                      () -> System.out.println("Completed")
              );

        // Error: java.lang.Exception
    }


    //
    // Tests
    //


    @Test
    public void testDefaultIfEmpty() {
        Observable<Integer> values = Observable.empty();

        final TestObserver<Integer> tester = values.defaultIfEmpty(2).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testDefaultIfEmptyError() {
        Observable<Integer> values = Observable.error(new Exception());

        final TestObserver<Integer> tester = values.defaultIfEmpty(2).test();

        tester.assertValues();
        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1);
    }
}
