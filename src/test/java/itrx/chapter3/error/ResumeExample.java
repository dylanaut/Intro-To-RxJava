package itrx.chapter3.error;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import itrx.helper.PrintObserver;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class ResumeExample {

    public void exampleOnErrorReturn() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            o.onError(new Exception("adjective unknown"));
        });

        values.onErrorReturn(e -> "Error: " + e.getMessage())
              .subscribe(System.out::println);

        // Rx
        // is
        // Error: adjective unknown
    }


    public void exampleOnErrorResumeNext() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Oops"));
        });

        values.onErrorResumeNext(Observable.just(Integer.MAX_VALUE))
              .subscribe(PrintObserver.printObserver("with onError: "));

        // with onError: 1
        // with onError: 2
        // with onError: 2147483647
        // with onError: Completed
    }


    public void exampleOnErrorResumeNextRethrow() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Oops"));
        });

        values.onErrorResumeNext((Throwable e) -> Observable.error(new UnsupportedOperationException(e)))
              .subscribe(PrintObserver.printObserver("with onError: "));

        // with onError: : 1
        // with onError: : 2
        // with onError: : Error: java.lang.UnsupportedOperationException: java.lang.Exception: Oops
    }


    public void exampleOnExceptionResumeNext() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            o.onError(new Exception()); // this will be caught
        });

        values.onExceptionResumeNext(Observable.just("hard"))
              .subscribe(System.out::println);

        // Rx
        // is
        // hard
    }


    @SuppressWarnings("serial")
    public void exampleOnExceptionResumeNextNoException() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            o.onError(new Throwable() {
            }); // this won't be caught
        });

        values.onExceptionResumeNext(Observable.just("hard"))
              .subscribe(System.out::println);

        // Rx
        // is
        // uncaught exception
    }


    @Test
    public void testOnErrorReturn() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            o.onError(new Exception("adjective unknown"));
        });

        final TestObserver<String> tester = values.onErrorReturn(e -> "Error: " + e.getMessage()).test();

        tester.assertValues(
                "Rx",
                "is",
                "Error: adjective unknown");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testerOnErrorResumeNext() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Oops"));
        });

        final TestObserver<Integer> tester = values.onErrorResumeNext(Observable.just(Integer.MAX_VALUE)).test();

        tester.assertValues(1, 2, Integer.MAX_VALUE);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testOnErrorResumeNextRethrow() {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception("Oops"));
        });

        final TestObserver<Integer> tester = values.onErrorResumeNext((Throwable e) -> Observable.error(new UnsupportedOperationException(e)))
                                                 .test();

        tester.assertValues(1, 2);
        tester.assertComplete();
        assertThat(tester.errors().get(0),
                org.hamcrest.CoreMatchers.instanceOf(UnsupportedOperationException.class));
    }


    @Test
    public void testOnExceptionResumeNext() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            o.onError(new Exception()); // this will be caught
        });

        final TestObserver<String> tester = values.onExceptionResumeNext(Observable.just("hard")).test();

        tester.assertValues("Rx", "is", "hard");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @SuppressWarnings("serial")
    @Test
    public void testOnExceptionResumeNextNoException() {
        Observable<String> values = Observable.create(o -> {
            o.onNext("Rx");
            o.onNext("is");
            o.onError(new Throwable() {
            }); // this won't be caught
        });

        final TestObserver<String> tester = values.onExceptionResumeNext(Observable.just("hard")).test();

        tester.assertComplete();
        Assert.assertEquals(tester.errorCount(), 1);
    }
}
