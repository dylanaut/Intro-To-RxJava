package itrx.chapter2.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class ToCollectionExample {

    public void exampleCustom() {
        Observable<Integer> values = Observable.range(10, 5);

        values.reduce(
                new ArrayList<Integer>(),
                (acc, value) -> {
                    acc.add(value);
                    return acc;
                })
              .subscribe(System.out::println);

        // [10, 11, 12, 13, 14]
    }


    public void exampleToList() {
        Observable<Integer> values = Observable.range(10, 5);

        values.toList()
              .subscribe(System.out::println);

        // [10, 11, 12, 13, 14]
    }


    public void exampleToSortedList() {
        Observable<Integer> values = Observable.range(10, 5);

        values.toSortedList((i1, i2) -> i2 - i1)
              .subscribe(System.out::println);

        // [14, 13, 12, 11, 10]
    }


    //
    // Tests
    //


    @Test
    public void testCustom() {
        TestObserver<List<Integer>> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(10, 5);

        values.reduce(
                new ArrayList<Integer>(),
                (acc, value) -> {
                    acc.add(value);
                    return acc;
                }).test();

        tester.assertValueSequence(Arrays.asList(Arrays.asList(10, 11, 12, 13, 14)));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testToList() {
        TestObserver<List<Integer>> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(10, 5);

        values.toList().test();

        tester.assertValueSequence(Arrays.asList(Arrays.asList(10, 11, 12, 13, 14)));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testToSortedList() {
        TestObserver<List<Integer>> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(10, 5);

        values.toSortedList((i1, i2) -> i2 - i1).test();

        tester.assertValueSequence(Arrays.asList(Arrays.asList(14, 13, 12, 11, 10)));
        tester.assertComplete();
        tester.assertNoErrors();
    }
}


