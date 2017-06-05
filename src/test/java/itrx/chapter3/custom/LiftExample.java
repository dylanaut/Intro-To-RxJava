package itrx.chapter3.custom;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class LiftExample {

    public void exampleLift() {
        Observable.range(0, 5)
                  .lift(MyMap.create(i -> i + "!"))
                  .subscribe(System.out::println);
        // 0!
        // 1!
        // 2!
        // 3!
        // 4!
    }


    @Test
    public void testLift() {
        TestObserver<String> tester = TestObserver.create();

        Observable.range(0, 5)
                  .lift(MyMap.create(i -> i + "!"))
                  .test();

        tester.assertValues("0!", "1!", "2!", "3!", "4!");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    public static class MyMap<T, R> implements ObservableOperator<R, T> {
        private Function<T, R> transformer;


        public MyMap(Function<T, R> transformer) {
            this.transformer = transformer;
        }


        public static <T, R> MyMap<T, R> create(Function<T, R> transformer) {
            return new MyMap<T, R>(transformer);
        }


        @Override
        public Observer<? super T> apply(Observer<? super R> observer) {
            return new Observer<T>() {

                @Override
                public void onSubscribe(Disposable disposable) {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }


                @Override
                public void onComplete() {
                    observer.onComplete();

                }


                @Override
                public void onError(Throwable e) {
                    observer.onError(e);

                }


                @Override
                public void onNext(T t) {
                    try {
                        observer.onNext(transformer.apply(t));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
        }
    }
}
