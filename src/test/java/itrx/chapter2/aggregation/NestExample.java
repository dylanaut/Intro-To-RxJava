package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        final TestObserver<Observable<Integer>> tester
                = Observable.range(0, 3)
                            .map(Observable::just).test();

        tester.assertValueCount(3);
        assertThat(tester.values().stream().allMatch(oi -> oi instanceof Observable)).isTrue();
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
