package itrx.chapter1;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UnsubscribingExample {

    public void exampleUnsubscribe() {
        Subject<Integer> values = ReplaySubject.create();
        Disposable subscription = values.subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Done"));
        values.onNext(0);
        values.onNext(1);
        subscription.dispose();
        values.onNext(2);

        // 0
        // 1
    }


    public void exampleIndependentSubscriptions() {
        Subject<Integer> values = ReplaySubject.create();
        Disposable subscription = values.subscribe(v -> System.out.println("First: " + v));
        values.subscribe(v -> System.out.println("Second: " + v));
        values.onNext(0);
        values.onNext(1);
        subscription.dispose();
        System.out.println("Unsubscribed first");
        values.onNext(2);

        // First: 0
        // Second: 0
        // First: 1
        // Second: 1
        // Unsubscribed first
        // Second: 2
    }


    public void exampleDisposeAction() {
        final Observable<Object> observable = Observable.create(o -> {
            System.out.println("Clean");
        });

        observable.subscribe();
        // Clean
    }


    //
    // Tests
    //


    @Test
    public void testUnsubscribe() {
        Subject<Integer> values = ReplaySubject.create();

        final TestObserver<Integer> tester = values.test();
        values.onNext(0);
        values.onNext(1);
        values.onNext(2);

        tester.assertValues(1);
    }


    @Test
    public void testIndependentSubscriptions() {
        TestObserver<Integer> tester1 = TestObserver.create();
        TestObserver<Integer> tester2 = TestObserver.create();

        Subject<Integer> values = ReplaySubject.create();

        values.subscribe(tester1);
        values.subscribe(tester2);

        values.onNext(0);
        values.onNext(1);
        values.onNext(2);

        tester1.assertValues(0, 1);
        tester2.assertValues(0, 1, 2);
    }


    @Test
    public void testUnsubscribeAction() {
        boolean[] ran = {false};

        Disposable s = Observable.create(o -> ran[0] = true).subscribe();
        s.dispose();

        assertTrue(ran[0]);
    }
}
