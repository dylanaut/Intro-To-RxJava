package itrx.chapter4.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TestSchedulerExample {

    public void exampleAdvanceTo() {
        TestScheduler s = new TestScheduler();

        s.createWorker().schedule(
                () -> System.out.println("Immediate"));
        s.createWorker().schedule(
                () -> System.out.println("20s"),
                20, SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("40s"),
                40, SECONDS);

        System.out.println("Advancing to 1ms");
        s.advanceTimeTo(1, MILLISECONDS);
        System.out.println("Virtual time: " + s.now(MILLISECONDS));

        System.out.println("Advancing to 10s");
        s.advanceTimeTo(10, SECONDS);
        System.out.println("Virtual time: " + s.now(SECONDS));

        System.out.println("Advancing to 40s");
        s.advanceTimeTo(40, SECONDS);
        System.out.println("Virtual time: " + s.now(SECONDS));

        // Advancing to 1ms
        // Immediate
        // Virtual time: 1
        // Advancing to 10s
        // Virtual time: 10000
        // Advancing to 40s
        // 20s
        // 40s
        // Virtual time: 40000
    }


    public void exampleTimeBy() {
        TestScheduler s = new TestScheduler();

        s.createWorker().schedule(
                () -> System.out.println("Immediate"));
        s.createWorker().schedule(
                () -> System.out.println("20s"),
                20, SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("40s"),
                40, SECONDS);

        System.out.println("Advancing by 1ms");
        s.advanceTimeBy(1, MILLISECONDS);
        System.out.println("Virtual time: " + s.now(TimeUnit.MILLISECONDS));

        System.out.println("Advancing by 10s");
        s.advanceTimeBy(10, SECONDS);
        System.out.println("Virtual time: " + s.now(TimeUnit.MILLISECONDS));

        System.out.println("Advancing by 40s");
        s.advanceTimeBy(40, SECONDS);
        System.out.println("Virtual time: " + s.now(TimeUnit.MILLISECONDS));

        // Advancing by 1ms
        // Immediate
        // Virtual time: 1
        // Advancing by 10s
        // Virtual time: 10001
        // Advancing by 40s
        // 20s
        // 40s
        // Virtual time: 50001
    }


    public void exampleTriggerActions() {
        TestScheduler s = new TestScheduler();

        s.createWorker().schedule(
                () -> System.out.println("Immediate"));
        s.createWorker().schedule(
                () -> System.out.println("20s"),
                20, SECONDS);

        s.triggerActions();
        System.out.println("Virtual time: " + s.now(TimeUnit.MILLISECONDS));

        // Immediate
        // Virtual time: 0
    }


    public void exampleCollision() {
        TestScheduler s = new TestScheduler();

        s.createWorker().schedule(
                () -> System.out.println("First"),
                20, SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("Second"),
                20, SECONDS);
        s.createWorker().schedule(
                () -> System.out.println("Third"),
                20, SECONDS);

        s.advanceTimeTo(20, SECONDS);

        // First
        // Second
        // Third
    }


    //
    // Test
    //


    @Test
    public void testAdvanceTo() {
        List<String> execution = new ArrayList<>();
        TestScheduler scheduler = new TestScheduler();

        scheduler.createWorker().schedule(
                () -> execution.add("Immediate"));
        scheduler.createWorker().schedule(
                () -> execution.add("20s"),
                20, SECONDS);
        scheduler.createWorker().schedule(
                () -> execution.add("40s"),
                40, SECONDS);


        Assert.assertEquals(0, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList(), execution);

        scheduler.advanceTimeTo(1, MILLISECONDS);
        Assert.assertEquals(1, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate"), execution);

        scheduler.advanceTimeTo(10, SECONDS);
        Assert.assertEquals(10000, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate"), execution);

        scheduler.advanceTimeTo(40, SECONDS);
        Assert.assertEquals(40000, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate", "20s", "40s"), execution);
    }


    @Test
    public void testTimeBy() {
        List<String> execution = new ArrayList<>();
        TestScheduler scheduler = new TestScheduler();

        scheduler.createWorker().schedule(
                () -> execution.add("Immediate"));
        scheduler.createWorker().schedule(
                () -> execution.add("20s"),
                20, SECONDS);
        scheduler.createWorker().schedule(
                () -> execution.add("40s"),
                40, SECONDS);

        Assert.assertEquals(0, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList(), execution);

        scheduler.advanceTimeBy(1, MILLISECONDS);
        Assert.assertEquals(1, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate"), execution);

        scheduler.advanceTimeBy(10, SECONDS);
        Assert.assertEquals(10001, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate"), execution);

        scheduler.advanceTimeBy(40, SECONDS);
        Assert.assertEquals(50001, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate", "20s", "40s"), execution);
    }


    @Test
    public void testTriggerActions() {
        List<String> execution = new ArrayList<>();
        TestScheduler scheduler = new TestScheduler();

        scheduler.createWorker().schedule(
                () -> execution.add("Immediate"));
        scheduler.createWorker().schedule(
                () -> execution.add("20s"),
                20, SECONDS);

        Assert.assertEquals(0, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList(), execution);
        scheduler.triggerActions();
        Assert.assertEquals(0, scheduler.now(TimeUnit.MILLISECONDS));
        Assert.assertEquals(Arrays.asList("Immediate"), execution);
    }


    @Test
    public void testCollision() {
        List<String> execution = new ArrayList<>();
        TestScheduler scheduler = new TestScheduler();

        scheduler.createWorker().schedule(
                () -> execution.add("First"),
                20, SECONDS);
        scheduler.createWorker().schedule(
                () -> execution.add("Second"),
                20, SECONDS);
        scheduler.createWorker().schedule(
                () -> execution.add("Third"),
                20, SECONDS);

        Assert.assertEquals(Arrays.asList(), execution);
        scheduler.advanceTimeTo(20, SECONDS);
        Assert.assertEquals(Arrays.asList("First", "Second", "Third"), execution);
    }
}
