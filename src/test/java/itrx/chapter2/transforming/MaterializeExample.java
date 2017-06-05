package itrx.chapter2.transforming;

import java.util.Arrays;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import itrx.helper.PrintObserver;
import org.junit.Test;

public class MaterializeExample {

    public void exampleMaterialize() {
        Observable<Integer> values = Observable.range(0, 3);

        values.take(3)
              .materialize()
              .subscribe(PrintObserver.printObserver("Materialize"));

        // Materialize: [io.reactivex.Notification@a4c802e9 OnNext 0]
        // Materialize: [io.reactivex.Notification@a4c802ea OnNext 1]
        // Materialize: [io.reactivex.Notification@a4c802eb OnNext 2]
        // Materialize: [io.reactivex.Notification@18d48ace OnCompleted]
        // Materialize: Completed
    }


    @Test
    public void testMaterialize() {
        TestObserver<Notification<Integer>> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 3);

        values.take(3)
              .materialize().test();

        tester.assertValueSequence(Arrays.asList(
                Notification.createOnNext(0),
                Notification.createOnNext(1),
                Notification.createOnNext(2),
                Notification.createOnComplete()
        ));
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
