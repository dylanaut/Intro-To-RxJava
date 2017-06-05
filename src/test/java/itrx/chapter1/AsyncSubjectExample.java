package itrx.chapter1;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.AsyncSubject;
import org.junit.Test;

public class AsyncSubjectExample {

    public void exampleLastValue() {
        AsyncSubject<Integer> s = AsyncSubject.create();
        s.subscribe(System.out::println);
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.onComplete();

        // 2
    }


    public void exampleNoCompletion() {
        AsyncSubject<Integer> s = AsyncSubject.create();
        s.subscribe(System.out::println);
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
    }

    //
    // Tests
    //


    @Test
    public void testLastValue() {
        AsyncSubject<Integer> s = AsyncSubject.create();

        final TestObserver<Integer> tester = s.test();

        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.onComplete();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testNoCompletion() {
        AsyncSubject<Integer> s = AsyncSubject.create();
        final TestObserver<Integer> tester = s.test();

        s.onNext(0);
        s.onNext(1);
        s.onNext(2);

        tester.assertNotComplete();
        tester.assertNoValues();
        tester.assertNoErrors();
    }
}
