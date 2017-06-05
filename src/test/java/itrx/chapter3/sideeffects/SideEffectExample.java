package itrx.chapter3.sideeffects;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class SideEffectExample {

    public void exampleBadIndex() {
        Observable<String> values = Observable.just("No", "side", "effects", "please");

        Inc index = new Inc();
        Observable<String> indexed =
                values.map(w -> {
                    index.inc();
                    return w;
                });
        indexed.subscribe(w -> System.out.println(index.getCount() + ": " + w));

        // 1: No
        // 2: side
        // 3: effects
        // 4: please
    }


    public void exampleBadIndexFail() {
        Observable<String> values = Observable.just("No", "side", "effects", "please");

        Inc index = new Inc();
        Observable<String> indexed =
                values.map(w -> {
                    index.inc();
                    return w;
                });
        indexed.subscribe(w -> System.out.println("1st observer: " + index.getCount() + ": " + w));
        indexed.subscribe(w -> System.out.println("2nd observer: " + index.getCount() + ": " + w));

        // 1st observer: 1: No
        // 1st observer: 2: side
        // 1st observer: 3: effects
        // 1st observer: 4: please
        // 2nd observer: 5: No
        // 2nd observer: 6: side
        // 2nd observer: 7: effects
        // 2nd observer: 8: please
    }


    public void exampleSafeIndex() {
        Observable<String> values = Observable.just("No", "side", "effects", "please");

        Observable<Indexed<String>> indexed =
                values.scan(
                        new Indexed<String>(0, null),
                        (prev, v) -> new Indexed<String>(prev.index + 1, v))
                      .skip(1);
        indexed.subscribe(w -> System.out.println("1st observer: " + w.index + ": " + w.item));
        indexed.subscribe(w -> System.out.println("2nd observer: " + w.index + ": " + w.item));

        // 1st observer: 1: No
        // 1st observer: 2: side
        // 1st observer: 3: effects
        // 1st observer: 4: please
        // 2nd observer: 1: No
        // 2nd observer: 2: side
        // 2nd observer: 3: effects
        // 2nd observer: 4: please
    }


    @Test
    public void testBadIndex() {
        Observable<String> values = Observable.just("No", "side", "effects", "please");

        Inc index = new Inc();
        Observable<Integer> indexed =
                values.map(w -> {
                    index.inc();
                    return w;
                })
                      .map(w -> index.getCount());
        final TestObserver<Integer> tester = indexed.test();

        tester.assertValues(4);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testBadIndexFail() {
        TestObserver<Integer> tester1 = TestObserver.create();
        TestObserver<Integer> tester2 = TestObserver.create();

        Observable<String> values = Observable.just("No", "side", "effects", "please");

        Inc index = new Inc();
        Observable<Integer> indexed =
                values.map(w -> {
                    index.inc();
                    return w;
                })
                      .map(w -> index.getCount());
        indexed.subscribe(tester1);
        indexed.subscribe(tester2);

        tester1.assertValues(4);
        tester1.assertComplete();
        tester1.assertNoErrors();
        tester2.assertValues(8);
        tester2.assertComplete();
        tester2.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testSafeIndex() {
        TestObserver<Integer> tester1 = TestObserver.create();
        TestObserver<Integer> tester2 = TestObserver.create();

        Observable<String> values = Observable.just("No", "side", "effects", "please");

        Observable<Integer> indexed =
                values.scan(
                        new Indexed<String>(0, null),
                        (prev, v) -> new Indexed<String>(prev.index + 1, v))
                      .skip(1)
                      .map(i -> i.index);
        indexed.subscribe(tester1);
        indexed.subscribe(tester2);

        tester1.assertValues(4);
        tester1.assertComplete();
        tester1.assertNoErrors();
        tester2.assertValues(4);
        tester2.assertComplete();
        tester2.assertNoErrors();
    }


    private static class Inc {
        private int count = 0;


        public void inc() {
            count++;
        }


        public int getCount() {
            return count;
        }
    }


    private static class Indexed<T> {
        public final int index;

        public final T item;


        public Indexed(int index, T item) {
            this.index = index;
            this.item = item;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Indexed<?>) {
                Indexed<?> other = (Indexed<?>) obj;
                return this.index == other.index &&
                        this.item.equals(other.item);
            }
            return false;
        }
    }
}
