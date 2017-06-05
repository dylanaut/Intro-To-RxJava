package itrx.chapter4.scheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SubscribeOnExample {

    public void exampleBlocking() {
        System.out.println("Main: " + Thread.currentThread().getId());

        Observable.create(o -> {
            System.out.println("Created on " + Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        })
                  .subscribe(i -> {
                      System.out.println("Received " + i + " on " + Thread.currentThread().getId());
                  });

        System.out.println("Finished main: " + Thread.currentThread().getId());

        // Main: 1
        // Created on 1
        // Received 1 on 1
        // Received 2 on 1
        // Finished main: 1
    }


    public void exampleSubscribeOn() {
        System.out.println("Main: " + Thread.currentThread().getId());

        Observable.create(o -> {
            System.out.println("Created on " + Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        })
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(i -> {
                      System.out.println("Received " + i + " on " + Thread.currentThread().getId());
                  });

        System.out.println("Finished main: " + Thread.currentThread().getId());

        // Main: 1
        // Created on 1
        // Received 1 on 11
        // Received 2 on 11
        // Finished main: 11
    }


    public void exampleIntervalThread() {
        System.out.println("Main: " + Thread.currentThread().getId());

        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .subscribe(i -> {
                      System.out.println("Received " + i + " on " + Thread.currentThread().getId());
                  });

        System.out.println("Finished main: " + Thread.currentThread().getId());

        // Main: 1
        // Finished main: 1
        // Received 0 on 11
        // Received 1 on 11
        // Received 2 on 11
    }


    //
    // Test
    //


    @Test
    public void testBlocking() {
        Map<String, Long> threads = new HashMap<>();

        threads.put("main", Thread.currentThread().getId());

        Observable.create(o -> {
            threads.put("create", Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        })
                  .subscribe(i -> {
                      threads.put("receive", Thread.currentThread().getId());
                  });

        assertEquals(Thread.currentThread().getId(), threads.get("main").longValue());
        assertEquals(Thread.currentThread().getId(), threads.get("create").longValue());
        assertEquals(Thread.currentThread().getId(), threads.get("receive").longValue());
    }


    @Test
    public void testSubscribeOn() {
        Map<String, Long> threads = new HashMap<>();

        threads.put("main", Thread.currentThread().getId());

        Observable.create(o -> {
            threads.put("create", Thread.currentThread().getId());
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        })
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(i -> {
                      threads.put("receive", Thread.currentThread().getId());
                  });

        assertEquals("Emitting and receiving on the same thread",
                threads.get("receive"),
                threads.get("create"));
        assertNotEquals("Emitting and receiving not on the main thread",
                threads.get("main"),
                threads.get("receive"));
    }


    @Test
    public void testIntervalThread() {
        long[] threads = {0, 0};

        threads[0] = Thread.currentThread().getId();

        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .subscribe(i -> {
                      threads[1] = Thread.currentThread().getId();
                  });

        assertNotEquals("interval not executing on main thread", threads[0], threads[1]);
    }
}
