package itrx.chapter1;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import org.junit.Test;

public class BehaviorSubjectExample {

    public void exampleLate() {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);

        // Late: 2
        // Late: 3
    }


    public void exampleCompleted() {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.onComplete();
        s.subscribe(
                v -> System.out.println("Late: " + v),
                e -> System.out.println("Error"),
                () -> System.out.println("Completed")
        );
    }


    public void exampleInitialvalue() {
        BehaviorSubject<Integer> s = BehaviorSubject.createDefault(0);
        s.subscribe(System.out::println);
        s.onNext(1);

        // 0
        // 1
    }


    //
    // Tests
    //


    @Test
    public void testLate() {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        final TestObserver<Integer> tester = s.test();
        s.onNext(3);

        tester.assertValues(2, 3);
    }


    @Test
    public void testCompleted() {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.onComplete();

        final TestObserver<Integer> tester = s.test();

        tester.assertValues();
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testInitialvalue() {
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        final TestObserver<Integer> tester = s.test();
        s.onNext(1);

        tester.assertValues(0, 1);
        tester.assertNoErrors();
    }
}
