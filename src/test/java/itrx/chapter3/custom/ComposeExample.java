package itrx.chapter3.custom;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class ComposeExample {

    public void exampleComposeFromClass() {
        Observable.just(2, 3, 10, 12, 4)
                  .compose(new RunningAverage())
                  .subscribe(System.out::println);

        // 2.0
        // 2.5
        // 5.0
        // 6.75
        // 6.2
    }


    public void exampleComposeParameterised() {
        Observable.just(2, 3, 10, 12, 4)
                  .compose(new RunningAverage(5))
                  .subscribe(System.out::println);

        // 2.0
        // 2.5
        // 3.0
    }


    @Test
    public void testComposeFromClass() {
        final TestObserver<Double> tester = Observable.just(2, 3, 10, 12, 4)
                                                    .compose(new RunningAverage()).test();

        tester.assertValues(2.0, 2.5, 5.0, 6.75, 6.2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Test
    //


    @Test
    public void testComposeParameterised() {
        final TestObserver<Double> tester = Observable.just(2, 3, 10, 12, 4)
                                                    .compose(new RunningAverage(5)).test();

        tester.assertValues(2.0, 2.5, 3.0);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    /**
     * A custom operator for calculating a running average
     *
     * @author Chris
     */
    public static class RunningAverage implements ObservableTransformer<Integer, Double> {
        final int threshold;


        public RunningAverage() {
            this.threshold = Integer.MAX_VALUE;
        }


        public RunningAverage(int threshold) {
            this.threshold = threshold;
        }


        @Override
        public Observable<Double> apply(Observable<Integer> source) {
            return source.filter(i -> i < this.threshold)
                         .scan(
                                 new AverageAcc(0, 0),
                                 (acc, v) -> new AverageAcc(acc.sum + v, acc.count + 1))
                         .filter(acc -> acc.count > 0)
                         .map(acc -> acc.sum / (double) acc.count);
        }


        private static class AverageAcc {
            public final int sum;

            public final int count;


            public AverageAcc(int sum, int count) {
                this.sum = sum;
                this.count = count;
            }
        }
    }
}
