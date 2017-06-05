package itrx.chapter4.scheduling;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

public class SchedulerExample {

    public void exampleSchedule() {
        Scheduler scheduler = Schedulers.trampoline();

        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> System.out.println("Action"));
    }


    public void exampleScheduleFuture() {
        Scheduler scheduler = Schedulers.newThread();
        long start = System.currentTimeMillis();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis() - start),
                5, SECONDS);
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis() - start),
                5, SECONDS);

        // 5033
        // 5035
    }


    public void exampleCancelWork() {
        Scheduler scheduler = Schedulers.newThread();
        long start = System.currentTimeMillis();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> {
                    System.out.println(System.currentTimeMillis() - start);
                    worker.dispose();
                },
                5, SECONDS);
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis() - start),
                5, SECONDS);

        // 5032
    }


    public void exampleCancelWithInterrupt() throws InterruptedException {
        Scheduler scheduler = Schedulers.newThread();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Action completed");
            }
            catch (InterruptedException e) {
                System.out.println("Action interrupted");
            }
        });
        Thread.sleep(500);
        worker.dispose();

        // Action interrupted
    }


    //
    // Test
    //


    @Test
    public void testSchedule() {
        List<Boolean> executed = new ArrayList<>();

        Scheduler scheduler = Schedulers.trampoline();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> executed.add(true));

        Assert.assertEquals(Arrays.asList(new Boolean(true)), executed);
    }


    @Test
    public void testScheduleFuture() {
        long[] executionTimes = {0, 0};

        TestScheduler scheduler = new TestScheduler();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> executionTimes[0] = scheduler.now(SECONDS),
                5, SECONDS);
        worker.schedule(
                () -> executionTimes[1] = scheduler.now(SECONDS),
                5, SECONDS);

        scheduler.advanceTimeTo(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals("First task executed on time", 5000, executionTimes[0]);
        Assert.assertEquals("Second task executed on time", 5000, executionTimes[1]);
    }


    @Test
    public void testCancelWork() {
        long[] executionTimes = {0, 0};

        TestScheduler scheduler = new TestScheduler();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> {
                    executionTimes[0] = scheduler.now(SECONDS);
                    worker.dispose();
                },
                5, SECONDS);
        worker.schedule(
                () -> executionTimes[1] = scheduler.now(SECONDS),
                5, SECONDS);

        scheduler.advanceTimeTo(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals("First task executed on time", 5000, executionTimes[0]);
        Assert.assertEquals("Second task never executed", 0, executionTimes[1]);
    }


    @Test
    public void testCancelWithInterrupt() throws InterruptedException {
        Scheduler scheduler = Schedulers.newThread();
        Scheduler.Worker worker = scheduler.createWorker();
        Thread[] workerThread = {null};
        Boolean[] interrupted = {false};
        worker.schedule(() -> {
            try {
                workerThread[0] = Thread.currentThread();
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                interrupted[0] = true;
            }
        });

        while (workerThread[0] == null ||
                workerThread[0].getState() != State.TIMED_WAITING) {
            Thread.sleep(1); // Wait for task to sleep
        }
        worker.dispose();
        workerThread[0].join();
        assertTrue("Task must be interrupted before completing", interrupted[0]);
    }
}
