package itrx.chapter2.inspection;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class IsEmptyExample {

    public void exampleIsEmpty() {
        Observable<Long> values = Observable.timer(1000, TimeUnit.MILLISECONDS);

        Disposable disposable = values.isEmpty()
                                      .subscribe(
                                              System.out::println,
                                              e -> System.out.println("Error: " + e)
                                      );

        disposable.dispose();

        // false
    }


    //
    // Test
    //


    @Test
    public void testIsEmpty() {
        TestObserver<Boolean> tester = new TestObserver<Boolean>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.timer(1000, TimeUnit.MILLISECONDS, scheduler);

        values.isEmpty().test();

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

        tester.assertValues(false);
        tester.assertComplete();
        tester.assertNoErrors();
        // // ToDo oder noch subscribed? [kreyj, 05.06.2017]
        tester.assertNotSubscribed();
    }
}
