package itrx.chapter1;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.AsyncSubject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        TestObserver<Integer> tester = new TestObserver<Integer>();

        AsyncSubject<Integer> s = AsyncSubject.create();
        s.test();
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
        TestObserver<Integer> tester = new TestObserver<Integer>();

        AsyncSubject<Integer> s = AsyncSubject.create();
        s.test();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);

        tester.assertValues();
        assertTrue(tester.getEvents().size() == 0);
        tester.assertNoErrors();
    }
}
