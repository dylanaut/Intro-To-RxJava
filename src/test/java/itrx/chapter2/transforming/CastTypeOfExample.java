package itrx.chapter2.transforming;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import itrx.helper.PrintObserver;
import org.junit.Assert;
import org.junit.Test;

public class CastTypeOfExample {

    public void exampleCast() {
        Observable<Object> values = Observable.just(0, 1, 2, 3);

        values.cast(Integer.class)
              .subscribe(PrintObserver.printObserver("Map"));

        // Map: 0
        // Map: 1
        // Map: 2
        // Map: 3
        // Map: Completed
    }


    public void exampleCastFail() {
        Observable<Object> values = Observable.just(0, 1, 2, "3");

        values.cast(Integer.class)
              .subscribe(PrintObserver.printObserver("Map"));

        // Map: 0
        // Map: 1
        // Map: 2
        // Map: Error: java.lang.ClassCastException: Cannot cast java.lang.String to java.lang.Integer
    }


    public void exampleTypeOf() {
        Observable<Object> values = Observable.just(0, 1, "2", 3);

        values.ofType(Integer.class)
              .subscribe(PrintObserver.printObserver("Map"));

        // Map: 0
        // Map: 1
        // Map: 3
        // Map: Completed
    }


    @Test
    public void testCast() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Object> values = Observable.just(0, 1, 2, 3);

        values.cast(Integer.class).test();

        tester.assertValues(3);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testCastFail() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Object> values = Observable.just(0, 1, 2, "3");

        values.cast(Integer.class).test();

        tester.assertValues(2);
        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1); // received 1 error
    }


    @Test
    public void testTypeOf() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Object> values = Observable.just(0, 1, "2", 3);

        values.ofType(Integer.class).test();

        tester.assertValues(3);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
