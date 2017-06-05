package itrx.chapter2.transforming;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.schedulers.Timed;
import itrx.helper.PrintObserver;
import org.junit.Assert;
import org.junit.Test;

public class TimestampTimeIntervalExample {

    public void exampleTimestamp() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(3)
              .timestamp()
              .subscribe(PrintObserver.printObserver("Timestamp"));

        // Timestamp: Timestamped(timestampMillis = 1428611094943, value = 0)
        // Timestamp: Timestamped(timestampMillis = 1428611095037, value = 1)
        // Timestamp: Timestamped(timestampMillis = 1428611095136, value = 2)
        // Timestamp: Completed
    }


    public void exampleTimeInteval() {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values.take(3)
              .timeInterval()
              .subscribe(PrintObserver.printObserver("TimeInterval"));

        // TimeInterval: TimeInterval [intervalInMilliseconds=131, value=0]
        // TimeInterval: TimeInterval [intervalInMilliseconds=75, value=1]
        // TimeInterval: TimeInterval [intervalInMilliseconds=100, value=2]
        // TimeInterval: Completed
    }


    @Test
    public void testTimestamp() {
        TestObserver<Timed<Long>> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Timed<Long>> test = values.take(3)
                                                     .timestamp(scheduler).test();

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        Assert.assertEquals(test.values().get(0).time(TimeUnit.MILLISECONDS), 100);
        Assert.assertEquals(test.values().get(1).time(TimeUnit.MILLISECONDS), 200);
        Assert.assertEquals(test.values().get(2).time(TimeUnit.MILLISECONDS), 300);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testTimeInterval() {
        TestObserver<Timed<Long>> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

        final TestObserver<Timed<Long>> test = values.take(3).timeInterval(scheduler).test();

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        Assert.assertEquals(test.values().get(0).time(TimeUnit.MILLISECONDS), 100);
        Assert.assertEquals(test.values().get(1).time(TimeUnit.MILLISECONDS), 100);
        Assert.assertEquals(test.values().get(2).time(TimeUnit.MILLISECONDS), 100);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
