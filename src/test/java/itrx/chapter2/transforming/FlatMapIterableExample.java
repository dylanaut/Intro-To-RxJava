package itrx.chapter2.transforming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class FlatMapIterableExample {

    public static Iterable<Integer> range(int start, int count) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i < start + count; i++) {
            list.add(i);
        }
        return list;
    }


    public void exampleFlatMapIterable() {
        Observable.range(1, 3)
                  .flatMapIterable(i -> range(1, i))
                  .subscribe(System.out::println);

        // 1
        // 1
        // 2
        // 1
        // 2
        // 3
    }


    public void exampleFlatMapIterableWithSelector() {
        Observable.range(1, 3)
                  .flatMapIterable(
                          i -> range(1, i),
                          (ori, rv) -> ori * rv)
                  .subscribe(System.out::println);

        // 1
        // 2
        // 4
        // 3
        // 6
        // 9
    }


    public void exampleFlatMapLazyIterable() {
        Observable.range(1, 3)
                  .flatMapIterable(
                          i -> new Range(1, i),
                          (ori, rv) -> ori * rv)
                  .subscribe(System.out::println);

        // 1
        // 2
        // 4
        // 3
        // 6
        // 9
    }


    @Test
    public void testFlatMapIterable() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable.range(1, 3)
                  .flatMapIterable(i -> range(1, i)).test();

        tester.assertValues(3);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Test
    //


    @Test
    public void testFlatMapIterableWithSelector() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable.range(1, 3)
                  .flatMapIterable(
                          i -> range(1, i),
                          (ori, rv) -> ori * rv).test();

        tester.assertValues(9);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFlatMapLazyIterable() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable.range(1, 3)
                  .flatMapIterable(
                          i -> new Range(1, i),
                          (ori, rv) -> ori * rv).test();

        tester.assertValues(9);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    public static class Range implements Iterable<Integer> {

        private final int start;

        private final int count;


        public Range(int start, int count) {
            this.start = start;
            this.count = count;
        }


        @Override
        public Iterator<Integer> iterator() {
            return new RangeIterator(start, count);
        }


        private static class RangeIterator implements Iterator<Integer> {
            private final int end;

            private int next;


            RangeIterator(int start, int count) {
                this.next = start;
                this.end = start + count;
            }


            @Override
            public boolean hasNext() {
                return next < end;
            }


            @Override
            public Integer next() {
                return next++;
            }
        }
    }
}
