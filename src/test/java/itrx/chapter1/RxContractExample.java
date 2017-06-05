package itrx.chapter1;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;

public class RxContractExample {

    public void example() {
        Subject<Integer> s = ReplaySubject.create();
        s.subscribe(System.out::println);
        s.onNext(0);
        s.onComplete();
        s.onNext(1);
        s.onNext(2);

        // 0
    }


    public void examplePrintCompletion() {
        Subject<Integer> values = ReplaySubject.create();
        values.subscribe(
                System.out::println,
                System.out::println,
                () -> System.out.println("Completed")
        );
        values.onNext(0);
        values.onNext(1);
        values.onComplete();
        values.onNext(2);

        // 0
        // 1
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Subject<Integer> s = ReplaySubject.create();
        s
.test();
        s.onNext(0);
        s.onComplete();
        s.onNext(1);
        s.onNext(2);

        tester.assertValues(0);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testPrintCompletion() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Subject<Integer> values = ReplaySubject.create();
        values.test();
        values.onNext(0);
        values.onNext(1);
        values.onComplete();
        values.onNext(2);

        tester.assertValues(0, 1);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
