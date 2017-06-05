package itrx.chapter3.combining;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class ConcatExample {

    public void exampleConcat() {
        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);

        Observable.concat(seq1, seq2)
                  .subscribe(System.out::println);

        // 0
        // 1
        // 2
        // 10
        // 11
        // 12
    }


    public void exampleConcatDynamic() {
        Observable<String> words = Observable.just(
                "First",
                "Second",
                "Third",
                "Fourth",
                "Fifth",
                "Sixth"
        );

        Observable.concat(words.groupBy(v -> v.charAt(0)))
                  .subscribe(System.out::println);

        // First
        // Fourth
        // Fifth
        // Second
        // Sixth
        // Third
    }


    public void exampleConcatWith() {
        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);
        Observable<Integer> seq3 = Observable.just(20);

        seq1.concatWith(seq2)
            .concatWith(seq3)
            .subscribe(System.out::println);

        // 0
        // 1
        // 2
        // 10
        // 11
        // 12
        // 20
    }


    //
    // Tests
    //


    @Test
    public void testConcat() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);

        Observable.concat(seq1, seq2).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testConcatDynamic() {
        TestObserver<String> tester = TestObserver.create();

        Observable<String> words = Observable.just(
                "First",
                "Second",
                "Third",
                "Fourth",
                "Fifth",
                "Sixth"
        );

        Observable.concat(words.groupBy(v -> v.charAt(0))).test();

        tester.assertValues(
                "First",
                "Fourth",
                "Fifth",
                "Second",
                "Sixth",
                "Third");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testConcatWith() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);
        Observable<Integer> seq3 = Observable.just(20);

        seq1.concatWith(seq2)
            .concatWith(seq3).test();

        tester.assertValues(0, 1, 2, 10, 11, 12, 20);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
