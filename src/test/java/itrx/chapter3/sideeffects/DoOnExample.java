package itrx.chapter3.sideeffects;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.ReplaySubject;
import itrx.helper.PrintObserver;
import org.junit.Test;
import org.reactivestreams.Subscription;

import static org.junit.Assert.assertArrayEquals;

public class DoOnExample {

    public void exampleDoOnEach() {
        Observable<String> values = Observable.just("side", "effects");

        values.doOnEach(PrintObserver.printObserver("Log"))
              .map(String::toUpperCase)
              .subscribe(PrintObserver.printObserver("Process"));

        // Log: side
        // Process: SIDE
        // Log: effects
        // Process: EFFECTS
        // Log: Completed
        // Process: Completed
    }


    public void exampleDoOnEachEncapsulation() throws Exception {
        Function<Object, Observable<String>> service = (ign) ->
                Observable.just("First", "Second", "Third")
                          .doOnEach(PrintObserver.printObserver("Log"));

        service.apply(null)
               .map(String::toUpperCase)
               .filter(s -> s.length() > 5)
               .subscribe(PrintObserver.printObserver("Process"));

        // Log: First
        // Log: Second
        // Process: SECOND
        // Log: Third
        // Log: Completed
        // Process: Completed
    }


    public void exampleOnSubscriber() {
        ReplaySubject<Integer> subject = ReplaySubject.create();
        Observable<Integer> values =
                subject.doOnSubscribe(disp -> System.out.println("New subscription"))
                       .doOnDispose(() -> System.out.println("Disposable over"));

        values.subscribe(PrintObserver.printObserver("1st"));
        subject.onNext(0);
        values.subscribe(PrintObserver.printObserver("2st"));
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);
        subject.onComplete();

        // New subscription
        // 1st: 0
        // New subscription
        // 2st: 0
        // 1st: 1
        // 2st: 1
        // Disposable over
        // 2st: 2
        // 2st: 3
        // 2st: Completed
        // Disposable over
    }


    @Test
    public void testDoOnEach() {
        TestObserver<String> testerLog = TestObserver.create();
        TestObserver<String> testerFinal = TestObserver.create();

        Observable<String> values = Observable.just("side", "effects");

        values.doOnEach(testerLog)
              .map(String::toUpperCase)
              .subscribe(testerFinal);

        testerLog.assertValues("side", "effects");
        testerLog.assertComplete();
        testerLog.assertNoErrors();
        testerFinal.assertValues("SIDE", "EFFECTS");
        testerFinal.assertComplete();
        testerFinal.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testDoOnEachEncapsulation() throws Exception {
        TestObserver<String> testerLog = TestObserver.create();
        TestObserver<String> testerFinal = TestObserver.create();

        Function<Object, Observable<String>> service = (ign) ->
                Observable.just("First", "Second", "Third")
                          .doOnEach(testerLog);

        service.apply(null)
               .map(String::toUpperCase)
               .filter(s -> s.length() > 5)
               .subscribe(testerFinal);

        testerLog.assertValues("First", "Second", "Third");
        testerLog.assertComplete();
        testerLog.assertNoErrors();
        testerFinal.assertValues("SECOND");
        testerFinal.assertComplete();
        testerFinal.assertNoErrors();
    }


    @Test
    public void testOnSubscriber() {
        int[] counts = {0, 0};

        ReplaySubject<Integer> subject = ReplaySubject.create();
        Observable<Integer> values = subject.doOnSubscribe((disposable) -> counts[0]++)
                                            .doOnDispose(() -> counts[1]++);

        assertArrayEquals(counts, new int[]{0, 0});
        Disposable s1 = values.subscribe();
        assertArrayEquals(counts, new int[]{1, 0});
        subject.onNext(0);
        values.subscribe();
        assertArrayEquals(counts, new int[]{2, 0});
        subject.onNext(1);
        s1.dispose();
        assertArrayEquals(counts, new int[]{2, 1});
        subject.onNext(2);
        subject.onNext(3);
        subject.onComplete();
        assertArrayEquals(counts, new int[]{2, 2});
    }
}
