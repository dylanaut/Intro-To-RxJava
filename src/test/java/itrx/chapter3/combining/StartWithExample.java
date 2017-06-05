package itrx.chapter3.combining;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class StartWithExample {

    public void example() {
        Observable<Integer> values = Observable.range(0, 3);

        values.startWithArray(-1, -2)
              .subscribe(System.out::println);

        // -1
        // -2
        // 0
        // 1
        // 2
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 3);

        values.startWithArray(-1, -2).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
