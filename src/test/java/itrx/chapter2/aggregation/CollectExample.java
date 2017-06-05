package itrx.chapter2.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;


public class CollectExample {

    public void example() {
        Observable<Integer> values = Observable.range(10, 5);

        values.collect((Callable<ArrayList<Integer>>) ArrayList::new, ArrayList::add)
              .subscribe(System.out::println);
    }


    @Test
    public void test() {
        TestObserver<List<Integer>> tester = TestObserver.create();
        Observable<Integer> values = Observable.range(10, 5);

        values.collect(
                (Callable<ArrayList<Integer>>) ArrayList::new, ArrayList::add).test();

        tester.assertValues(Arrays.asList(10, 11, 12, 13, 14));
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
