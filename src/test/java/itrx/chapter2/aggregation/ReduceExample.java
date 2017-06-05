package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;


public class ReduceExample {


    public void example() {
        Observable<Integer> values = Observable.range(0, 5);

        values.reduce((i1, i2) -> i1 + i2)
              .subscribe(printObserver("Sum"));
        values.reduce((i1, i2) -> (i1 > i2) ? i2 : i1)
              .subscribe(printObserver("Min"));

        // Sum: 10
        // Sum: Completed
        // Min: 0
        // Min: Completed
    }


    public void exampleWithAccumulator() {
        Observable<String> values = Observable.just("Rx", "is", "easy");

        values.reduce(0, (acc, next) -> acc + 1)
              .subscribe(printObserver("Count"));
        // Count: 3
        // Count: Completed
    }


    //
    // Tests
    //


    @Test
    public void test() {
        TestObserver<Integer> testerSum = TestObserver.create();
        TestObserver<Integer> testerMin = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 5);

        values.reduce((i1, i2) -> i1 + i2)
              .subscribe(testerSum);
        values.reduce((i1, i2) -> (i1 > i2) ? i2 : i1)
              .subscribe(testerMin);

        testerSum.assertValues(10);
        testerSum.assertComplete();
        testerSum.assertNoErrors();

        testerMin.assertValues(0);
        testerMin.assertComplete();
        testerMin.assertNoErrors();
    }


    @Test
    public void testWithAccumulator() {
        Observable<String> values = Observable.just("Rx", "is", "easy");

        final TestObserver<Integer> tester = values.reduce(0, (acc, next) -> acc + 1).test();

        tester.assertValues(3);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}


