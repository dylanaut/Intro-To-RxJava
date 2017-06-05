package itrx.chapter1;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;

public class PublishSubjectExample {

    public void example() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.onNext(1);
        subject.subscribe(System.out::println);
        subject.onNext(2);
        subject.onNext(3);
        subject.onNext(4);

        // 2
        // 3
        // 4
    }


    //
    // Test
    //


    @Test
    public void test() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.onNext(1);
        final TestObserver<Integer> tester = subject.test();
        subject.onNext(2);
        subject.onNext(3);
        subject.onNext(4);

        tester.assertValues(2, 3, 4);
    }
}
