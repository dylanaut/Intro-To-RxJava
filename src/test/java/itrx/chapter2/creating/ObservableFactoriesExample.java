package itrx.chapter2.creating;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;

public class ObservableFactoriesExample {

    public void exampleJust() {
        Observable<String> values = Observable.just("one", "two", "three");
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Received: one
        // Received: two
        // Received: three
        // Completed
    }


    public void exampleEmpty() {
        Observable<String> values = Observable.empty();
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Completed
    }


    public void exampleNever() {
        Observable<String> values = Observable.never();
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
    }


    public void exampleError() {
        Observable<String> values = Observable.error(new Exception("Oops"));
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Error: java.lang.Exception: Oops
    }


    public void exampleShouldDefer() throws InterruptedException {
        Observable<Long> now = Observable.just(System.currentTimeMillis());

        now.subscribe(System.out::println);
        Thread.sleep(1000);
        now.subscribe(System.out::println);

        // 1431443908375
        // 1431443908375
    }


    public void exampleDefer() throws InterruptedException {
        Observable<Long> now = Observable.defer(() ->
                Observable.just(System.currentTimeMillis()));

        now.subscribe(System.out::println);
        Thread.sleep(1000);
        now.subscribe(System.out::println);

        // 1431444107854
        // 1431444108858
    }


    public void exampleCreate() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Hello");
            o.onComplete();
        });

        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );

        // Received: Hello
        // Completed
    }


    //
    // Tests
    //


    @Test
    public void testJust() {
        Observable<String> values = Observable.just("one", "two", "three");
        final TestObserver<String> tester = values.test();

        tester.assertValues("one", "two", "three");
        tester.assertNoErrors();
        tester.assertComplete();
    }


    @Test
    public void testEmpty() {
        Observable<String> values = Observable.empty();
        final TestObserver<String> tester = values.test();

        tester.assertValues();
        tester.assertNoErrors();
        tester.assertComplete();
    }


    @Test
    public void testNever() {
        Observable<String> values = Observable.never();
        final TestObserver<String> tester = values.test();

        tester.assertValues();
        tester.assertNoErrors();
        Assert.assertEquals(tester.getEvents().size(), 0);
    }


    @Test
    public void testError() {
        Observable<String> values = Observable.error(new Exception("Oops"));
        final TestObserver<String> tester = values.test();

        tester.assertValues();
        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1);
        Assert.assertEquals(tester.getEvents().size(), 0);
    }


    @Test
    public void testShouldDefer() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = TestObserver.create();
        TestObserver<Long> tester2 = TestObserver.create();

        Observable<Long> now = Observable.just(scheduler.now(MILLISECONDS));

        now.subscribe(tester1);
        scheduler.advanceTimeBy(1000, MILLISECONDS);
        now.subscribe(tester2);

        Assert.assertEquals(tester1.getEvents().get(0),
                tester2.getEvents().get(0));
    }


    @Test
    public void testDefer() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = TestObserver.create();
        TestObserver<Long> tester2 = TestObserver.create();

        Observable<Long> now = Observable.defer(() ->
                Observable.just(scheduler.now(MILLISECONDS)));

        now.subscribe(tester1);
        scheduler.advanceTimeBy(1000, MILLISECONDS);
        now.subscribe(tester2);

        final List<Object> objects1 = tester1.getEvents().get(0);
        final List<Object> objects2 = tester2.getEvents().get(0);
        assertTrue(objects1.size() < objects2.size());
    }


    @Test
    public void testCreate() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Hello");
            o.onComplete();
        });

        final TestObserver<String> tester = values.test();

        tester.assertValues("Hello");
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
