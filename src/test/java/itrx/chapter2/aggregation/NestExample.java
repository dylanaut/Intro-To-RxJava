package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class NestExample {

    public void example() {
        Observable.range(0, 3)
                  .map(Observable::just)
                  .subscribe(ob -> ob.subscribe(System.out::println));
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

        Observable.range(0, 3)
                  .map(Observable::just)
                  .subscribe(ob -> ob.test());

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
