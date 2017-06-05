package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class CombineLatestExample {

    public void example() {
        Observable.combineLatest(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                          .doOnNext(i -> System.out.println("Left emits")),
                Observable.interval(150, TimeUnit.MILLISECONDS)
                          .doOnNext(i -> System.out.println("Right emits")),
                (i1, i2) -> i1 + " - " + i2
        )
                  .take(6)
                  .subscribe(System.out::println);

        // Left emits
        // Right emits
        // 0 - 0
        // Left emits
        // 1 - 0
        // Left emits
        // 2 - 0
        // Right emits
        // 2 - 1
        // Left emits
        // 3 - 1
        // Right emits
        // 3 - 2
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> tester = TestObserver.create();

        Observable.combineLatest(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
                Observable.interval(150, TimeUnit.MILLISECONDS, scheduler),
                (i1, i2) -> i1 + " - " + i2
        ).test();

        scheduler.advanceTimeTo(100, TimeUnit.MILLISECONDS);
        scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
        scheduler.advanceTimeTo(200, TimeUnit.MILLISECONDS);
        scheduler.advanceTimeTo(300, TimeUnit.MILLISECONDS);

        tester.assertValues();
    }
}
