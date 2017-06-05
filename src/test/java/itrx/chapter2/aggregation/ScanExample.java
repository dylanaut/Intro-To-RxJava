package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;

public class ScanExample {


    public void exampleRunningSum() {
        Observable<Integer> values = Observable.range(0, 5);

        values.scan((i1, i2) -> i1 + i2)
              .subscribe(printObserver("Sum"));

        // Sum: 0
        // Sum: 1
        // Sum: 3
        // Sum: 6
        // Sum: 10
        // Sum: Completed
    }


    public void exampleRunningMin() {
        Subject<Integer> values = ReplaySubject.create();

        values.subscribe(printObserver("Values"));
        values.scan((i1, i2) -> (i1 < i2) ? i1 : i2)
              .distinctUntilChanged()
              .subscribe(printObserver("Min"));

        values.onNext(2);
        values.onNext(3);
        values.onNext(1);
        values.onNext(4);
        values.onComplete();

        // Values: 2
        // Min: 2
        // Values: 3
        // Values: 1
        // Min: 1
        // Values: 4
        // Values: Completed
        // Min: Completed
    }


    //
    // Tests
    //


    @Test
    public void testRunningSum() {
        Observable<Integer> values = Observable.range(0, 5);

        final TestObserver<Integer> tester = values.scan((i1, i2) -> i1 + i2).test();

        tester.assertValues(0);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testRunningMin() {
        TestObserver<Integer> testerSource = TestObserver.create();
        TestObserver<Integer> testerScan = TestObserver.create();

        Subject<Integer> values = ReplaySubject.create();

        values.subscribe(testerSource);
        values.scan((i1, i2) -> (i1 < i2) ? i1 : i2)
              .distinctUntilChanged()
              .subscribe(testerScan);

        values.onNext(2);
        values.onNext(3);
        values.onNext(1);
        values.onNext(4);
        values.onComplete();

        testerSource.assertValues(4);
        testerSource.assertComplete();
        testerSource.assertNoErrors();
        testerScan.assertValues(1);
        testerScan.assertComplete();
        testerScan.assertNoErrors();
    }
}


