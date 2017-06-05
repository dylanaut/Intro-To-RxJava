package itrx.chapter2.transforming;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import itrx.helper.PrintObserver;
import org.junit.Test;

public class MapExample {

    public void exampleMap() {
        Observable<Integer> values = Observable.range(0, 4);

        values.map(i -> i + 3)
              .subscribe(PrintObserver.printObserver("Map"));

        // Map: 3
        // Map: 4
        // Map: 5
        // Map: 6
        // Map: Completed
    }


    public void exampleMap2() {
        Observable<Integer> values =
                Observable.just("0", "1", "2", "3")
                          .map(Integer::parseInt);

        values.subscribe(PrintObserver.printObserver("Map"));

        // Map: 0
        // Map: 1
        // Map: 2
        // Map: 3
        // Map: Completed
    }


    @Test
    public void testMap() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 4);

        values.map(i -> i + 3).test();

        tester.assertValues(6);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testMap2() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values =
                Observable.just("0", "1", "2", "3")
                          .map(Integer::parseInt);

        values.test();

        tester.assertValues(3);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
