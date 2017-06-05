package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;

public class RepeatExample {

    public void exampleRepeat() {
        Observable<Integer> words = Observable.range(0, 2);

        words.repeat()
             .take(4)
             .subscribe(System.out::println);

        // 0
        // 1
        // 0
        // 1
    }


    public void exampleRepeat2() {
        Observable<Integer> words = Observable.range(0, 2);

        words.repeat(2)
             .subscribe(System.out::println);

        // 0
        // 1
        // 0
        // 1
    }


    public void exampleRepeatWhen2() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(2)
              .repeatWhen(ob -> {
                  return ob.take(2);
              })
              .subscribe(printObserver("repeatWhen"));

        // repeatWhen: 0
        // repeatWhen: 1
        // repeatWhen: 0
        // repeatWhen: 1
        // repeatWhen: Completed
    }


    public void exampleRepeatWithInterval() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(5) // Numbers 0 to 4
              .repeatWhen((ob) -> {
                  ob.subscribe();
                  return Observable.interval(2, TimeUnit.SECONDS);
              });
        // Repeat 0 to 4 every 2s, forever.take(2)
        // Stop after second repetition.subscribe
        // (PrintObserver.printObserver("repeatWhen"));

        // repeatWhen: 0
        // repeatWhen: 1
        // repeatWhen: 2
        // repeatWhen: 3
        // repeatWhen: 4
        // repeatWhen: 0
        // repeatWhen: 1
        // repeatWhen: 2
        // repeatWhen: 3
        // repeatWhen: 4
    }


    @Test
    public void testRepeat() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> words = Observable.range(0, 2);

        words.repeat()
             .take(4).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testRepeat2() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> words = Observable.range(0, 2);

        words.repeat(2).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();

    }


    @Test
    public void testRepeatWhen2() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 2);

        values.repeatWhen(ob -> {
            return ob.take(2);
        }).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testRepeatWithInterval() {
        TestObserver<Long> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        values.take(5) // Numbers 0 to 4
              .repeatWhen((ob) -> {
                  ob.subscribe();
                  return Observable.interval(2, TimeUnit.SECONDS, scheduler);
              }).test(); // Repeat 0 to 4 every 2s, forever

        scheduler.advanceTimeBy(4, TimeUnit.SECONDS);

        tester.assertValues(0L);
        tester.assertNoErrors();
    }
}
