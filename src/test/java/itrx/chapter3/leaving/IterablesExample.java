package itrx.chapter3.leaving;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class IterablesExample {

    public void exampleToIterable() {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        Iterable<Long> iterable = values.take(5).blockingIterable();
        for (long l : iterable) {
            System.out.println(l);
        }

        // 0
        // 1
        // 2
        // 3
        // 4
    }


    public void exampleNext() throws InterruptedException {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        values.take(5)
              .subscribe(v -> System.out.println("Emitted: " + v));

        Iterable<Long> iterable = values.take(5).blockingNext();
        for (long l : iterable) {
            System.out.println(l);
            Thread.sleep(750);
        }

        // Emitted: 0
        // 0
        // Emitted: 1
        // Emitted: 2
        // 2
        // Emitted: 3
        // Emitted: 4
        // 4
    }


    public void exampleLatest() throws InterruptedException {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        values.take(5)
              .subscribe(v -> System.out.println("Emitted: " + v));

        Iterable<Long> iterable = values.take(5).blockingLatest();
        for (long l : iterable) {
            System.out.println(l);
            Thread.sleep(750);
        }

        // Emitted: 0
        // 0
        // Emitted: 1
        // 1
        // Emitted: 2
        // Emitted: 3
        // 3
        // Emitted: 4
    }


    public void exampleMostRecent() throws InterruptedException {
        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS);

        values.take(5)
              .subscribe(v -> System.out.println("Emitted: " + v));

        Iterable<Long> iterable = values.take(5).blockingMostRecent(-1L);
        for (long l : iterable) {
            System.out.println(l);
            Thread.sleep(400);
        }

        // -1
        // -1
        // Emitted: 0
        // 0
        // Emitted: 1
        // 1
        // Emitted: 2
        // 2
        // Emitted: 3
        // 3
        // 3
        // Emitted: 4
    }


    //
    // Tests
    //


    @Test
    public void testToIterable() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        List<Long> received = new ArrayList<>();

        Observable<Long> values = Observable.interval(500, TimeUnit.MILLISECONDS, scheduler);

        Thread thread = new Thread(() -> {
            Iterable<Long> iterable =
                    values.take(5)
                          .blockingIterable();
            for (long l : iterable) {
                received.add(l);
            }
        });
        thread.start();

        Assert.assertEquals(received, asList());
        // Wait for blocking call to block before producing values
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        thread.join(50);
        Assert.assertEquals(received, asList(0L, 1L, 2L, 3L, 4L));
    }


    @Test
    public void testNext() throws InterruptedException {
        Subject<Integer> subject = PublishSubject.create();
        List<Integer> received = new ArrayList<>();
        Semaphore produce = new Semaphore(0);
        Semaphore consume = new Semaphore(0);

        Thread thread = new Thread(() -> {
            Iterable<Integer> iterable = subject.blockingNext();
            Iterator<Integer> iterator = iterable.iterator();
            try {
                while (true) {
                    consume.acquire();
                    produce.release();
                    if (!iterator.hasNext()) {
                        produce.release();
                        break;
                    }
                    received.add(iterator.next());
                    produce.release();
                }
            }
            catch (InterruptedException e) {
            }
        });
        thread.start();

        consume.release();
        produce.acquire();
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        subject.onNext(0);
        produce.acquire();
        Assert.assertEquals(singletonList(0), received);

        subject.onNext(1);
        consume.release();
        produce.acquire();
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        subject.onNext(2);
        produce.acquire();
        Assert.assertEquals(asList(0, 2), received);

        subject.onNext(3);
        consume.release();
        produce.acquire();
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        subject.onNext(4);
        produce.acquire();
        Assert.assertEquals(asList(0, 2, 4), received);

        consume.release();
        subject.onComplete();

        thread.join();
    }


    @Test
    public void testLatest() throws InterruptedException {
        Subject<Integer> subject = PublishSubject.create();
        List<Integer> received = new ArrayList<>();
        Semaphore produce = new Semaphore(0);
        Semaphore consume = new Semaphore(0);

        Thread thread = new Thread(() -> {
            Iterable<Integer> iterable = subject.blockingLatest();
            Iterator<Integer> iterator = iterable.iterator();
            try {
                while (true) {
                    consume.acquire();
                    produce.release();
                    if (!iterator.hasNext()) {
                        produce.release();
                        break;
                    }
                    received.add(iterator.next());
                    produce.release();
                }
            }
            catch (InterruptedException e) {
            }
        });
        thread.start();

        consume.release();
        produce.acquire();
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        Assert.assertEquals(emptyList(), received);

        subject.onNext(0);
        produce.acquire();
        Assert.assertEquals(asList(0), received);

        consume.release();
        produce.acquire();
        while (thread.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        subject.onNext(1);
        produce.acquire();
        Assert.assertEquals(asList(0, 1), received);

        subject.onNext(2);
        subject.onNext(3);
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(0, 1, 3), received);

        subject.onNext(4);
        subject.onComplete();
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(0, 1, 3), received);

        thread.join();
    }


    @Test
    public void testMostRecent() throws InterruptedException {
        Subject<Integer> subject = PublishSubject.create();
        List<Integer> received = new ArrayList<>();
        Semaphore produce = new Semaphore(0);
        Semaphore consume = new Semaphore(0);

        Thread thread = new Thread(() -> {
            Iterable<Integer> iterable = subject.blockingMostRecent(-1);
            Iterator<Integer> iterator = iterable.iterator();
            try {
                while (true) {
                    consume.acquire();
                    produce.release();
                    if (!iterator.hasNext()) {
                        produce.release();
                        break;
                    }
                    received.add(iterator.next());
                    produce.release();
                }
            }
            catch (InterruptedException e) {
            }
        });
        thread.start();

        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1), received);

        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1, -1), received);

        subject.onNext(0);
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1, -1, 0), received);

        subject.onNext(1);
        subject.onNext(2);
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1, -1, 0, 2), received);

        subject.onNext(3);
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1, -1, 0, 2, 3), received);
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1, -1, 0, 2, 3, 3), received);

        subject.onNext(4);
        subject.onComplete();
        consume.release();
        produce.acquire();
        produce.acquire();
        Assert.assertEquals(asList(-1, -1, 0, 2, 3, 3), received);

        thread.join();
    }
}
