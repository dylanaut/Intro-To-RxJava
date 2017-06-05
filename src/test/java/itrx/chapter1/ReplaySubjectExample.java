package itrx.chapter1;

import java.util.concurrent.TimeUnit;

import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.ReplaySubject;
import org.junit.Test;

public class ReplaySubjectExample {

    public void exampleEarlyLate() {
        ReplaySubject<Integer> s = ReplaySubject.create();
        s.subscribe(v -> System.out.println("Early:" + v));
        s.onNext(0);
        s.onNext(1);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(2);

        // Early:0
        // Early:1
        // Late: 0
        // Late: 1
        // Early:2
        // Late: 2
    }


    public void exampleWithSize() {
        ReplaySubject<Integer> s = ReplaySubject.createWithSize(2);
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);

        // Late: 1
        // Late: 2
        // Late: 3
    }


    public void exampleWithTime() throws InterruptedException {
        ReplaySubject<Integer> s = ReplaySubject.createWithTime(150, TimeUnit.MILLISECONDS, Schedulers.trampoline());
        s.onNext(0);
        Thread.sleep(100);
        s.onNext(1);
        Thread.sleep(100);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);

        // Late: 1
        // Late: 2
        // Late: 3
    }


    //
    // Test
    //


    @Test
    public void testEarlyLate() {
        ReplaySubject<Integer> s = ReplaySubject.create();
        final TestObserver<Integer> tester = s.test();

        s.onNext(0);
        s.onNext(1);
        s.onNext(2);

        tester.assertValues(0, 1, 0, 1, 2, 2);
    }


    @Test
    public void testWithSize() {
        ReplaySubject<Integer> s = ReplaySubject.createWithSize(2);
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        final TestObserver<Integer> tester = s.test();
        s.onNext(3);

        tester.assertValues(1, 2, 3);
    }


    @Test
    public void testWithTime() {
        TestScheduler scheduler = new TestScheduler();

        ReplaySubject<Integer> s = ReplaySubject.createWithTime(150, TimeUnit.MILLISECONDS, scheduler);
        s.onNext(0);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        s.onNext(1);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        s.onNext(2);
        final TestObserver<Integer> tester = s.test();
        s.onNext(3);

        tester.assertValues(1, 2, 3);
    }
}
