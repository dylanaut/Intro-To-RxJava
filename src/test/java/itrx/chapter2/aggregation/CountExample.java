package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;

public class CountExample {


    public void example() {
        Observable<Integer> values = Observable.range(0, 3);

        values.subscribe(printObserver("Values"));
        values.count().subscribe(printObserver("Count"));

        // Values: 0
        // Values: 1
        // Values: 2
        // Values: Completed
        // Count: 3
        // Count: Completed
    }


    public void exampleCountLong() {
        Observable<Integer> values = Observable.range(0, 3);

        values.subscribe(printObserver("Values"));
        final SingleObserver<Long> singleObserver = printObserver("Count");
        values.count().subscribe(singleObserver);

        // Values: 0
        // Values: 1
        // Values: 2
        // Values: Completed
        // Count: 3
        // Count: Completed
    }


    //
    // Tests
    //


    @Test
    public void test() {
        TestObserver<Integer> testerSource = TestObserver.create();
        TestObserver<Long> testerCount = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 3);

        values.subscribe(testerSource);
        values.count().subscribe(testerCount);

        testerSource.assertValues(0, 1, 2);
        testerSource.assertComplete();
        testerSource.assertNoErrors();

        testerCount.assertValues(3L);
        testerCount.assertComplete();
        testerCount.assertNoErrors();
    }


    @Test
    public void testCountLong() {
        TestObserver<Integer> testerSource = TestObserver.create();
        TestObserver<Long> testerCount = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 3);

        values.subscribe(testerSource);
        values.count().subscribe(testerCount);

        testerSource.assertValues(0, 1, 2);
        testerSource.assertComplete();
        testerSource.assertNoErrors();

        testerCount.assertValues(3L);
        testerCount.assertComplete();
        testerCount.assertNoErrors();
    }
}
