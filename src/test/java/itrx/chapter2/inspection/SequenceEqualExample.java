package itrx.chapter2.inspection;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

public class SequenceEqualExample {

    public void exampleSequenceEqualTrue() {
        Observable<String> strings = Observable.just("1", "2", "3");
        Observable<Integer> ints = Observable.just(1, 2, 3);

        Observable.sequenceEqual(strings, ints, (s, i) -> s.equals(i.toString()))
                  .subscribe(
                          System.out::println,
                          e -> System.out.println("Error: " + e));

        // true
    }


    public void exampleSequenceEqualFalse() {
        Observable<String> strings = Observable.just("1", "2", "3");
        Observable<Integer> ints = Observable.just(1, 2, 3);

        Observable.sequenceEqual(strings, ints)
                  .subscribe(
                          System.out::println,
                          e -> System.out.println("Error: " + e));

        // false
    }


    public void exampleSequenceEqualError() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception());
        });

        Observable.sequenceEqual(values, values)
                  .subscribe(
                          System.out::println,
                          e -> System.out.println("Error: " + e));

        // Error: java.lang.Exception
    }


    //
    // Tests
    //


    @Test
    public void testSequenceEqualTrue() {
        TestObserver<Boolean> tester = new TestObserver<>();

        Observable<String> strings = Observable.just("1", "2", "3");
        Observable<Integer> ints = Observable.just(1, 2, 3);

        Observable.sequenceEqual(strings, ints, (s, i) -> s.equals(i.toString()))
                  .test();

        tester.assertValues(true);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testSequenceEqualFalse() {
        TestObserver<Boolean> tester = new TestObserver<Boolean>();

        Observable<String> strings = Observable.just("1", "2", "3");
        Observable<Integer> ints = Observable.just(1, 2, 3);

        Observable.sequenceEqual(strings, ints)
                  .test();

        tester.assertValues(false);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testSequenceEqualError() {
        TestObserver<Boolean> tester = new TestObserver<>();

        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception());
        });

        Observable.sequenceEqual(values, values)
                  .test();

        tester.assertValues();
        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1);
    }
}
