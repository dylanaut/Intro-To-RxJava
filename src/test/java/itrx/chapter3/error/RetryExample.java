package itrx.chapter3.error;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

public class RetryExample {

    public void exampleRetry() {
        Random random = new Random();
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(random.nextInt() % 20);
            o.onNext(random.nextInt() % 20);
            o.onError(new Exception());
        });

        values.retry(1)
              .subscribe(System.out::println);

        // 0
        // 13
        // 9
        // 15
        // java.lang.Exception
    }


    //
    // Test
    //


    @Test
    public void testRetry() {
        TestObserver<Integer> tester = TestObserver.create();
        Random random = new Random();
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(random.nextInt() % 20);
            o.onNext(random.nextInt() % 20);
            o.onError(new Exception());
        });

        values.retry(1).test();

        Assert.assertEquals(tester.getEvents().size(), 4);
        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1);
    }
}
