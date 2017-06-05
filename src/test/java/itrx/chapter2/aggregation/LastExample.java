package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;

public class LastExample {

    public void exampleLast() {
        Observable<Integer> values = Observable.range(0, 10);

        values.lastElement()
              .subscribe(printObserver("Last"));

        // 9
    }


    public void exampleLastWithPredicate() {
        Observable<Integer> values = Observable.range(0, 10);

        values.filter(v -> v < 5)
              .lastElement()
              .subscribe(printObserver("Last"));

        // 4
    }


    public void exampleLastOrDefault() {
        Observable<Integer> values = Observable.empty();

        values.last(-1)
              .subscribe(printObserver("Last"));

        // -1
    }


    public void exampleLastOrDefaultWithPredicate() {
        Observable<Integer> values = Observable.empty();

        values.filter(v -> v > 5)
              .last(-1)
              .subscribe(printObserver("Last"));

        // -1
    }


    //
    // Tests
    //


    @Test
    public void testLast() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 10);

        values.lastElement().test();

        tester.assertValues(9);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testLastWithPredicate() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 10);

        values.filter(v -> v < 5).lastElement().test();

        tester.assertValues(4);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testLastOrDefault() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.empty();

        values.last(-1).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testLastOrDefaultWithPredicate() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.empty();

        values.filter(v -> v < 5).last(-1).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}


