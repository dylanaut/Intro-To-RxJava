package itrx.chapter2.aggregation;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class GroupByExample {

    public void exampleGroupBy() {
        Observable<String> values = Observable.just(
                "first",
                "second",
                "third",
                "forth",
                "fifth",
                "sixth"
        );

        values.groupBy(word -> word.charAt(0))
              .flatMap(group -> {
                          return group.lastElement().toObservable().map(v -> group.getKey() + ": " + v);
                      }
              )
              .subscribe(System.out::println);

        // s: sixth
        // t: third
        // f: fifth
    }


    //
    // Tests
    //


    @Test
    public void testGroupBy() {
        Observable<String> values = Observable.just(
                "first",
                "second",
                "third",
                "forth",
                "fifth",
                "sixth"
        );

        /* v ->  */

        final TestObserver<String> tester =
                values.groupBy(word -> word.charAt(0))
                      .flatMap(group ->
                              group.lastElement()
                                   .toObservable()
                                   .map(v -> group.getKey() + ": " + v)
                      ).test();

        tester.assertValues("s: sixth", "t: third", "f: fifth");
        tester.assertComplete();
        tester.assertNoErrors();
    }
}


