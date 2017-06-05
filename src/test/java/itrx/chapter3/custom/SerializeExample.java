package itrx.chapter3.custom;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

public class SerializeExample {

    public void exampleSafeSubscribe() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
            o.onNext(3);
            o.onComplete();
        });

        source// .onUnsubscribe(() -> System.out.println("Unsubscribed"))
              .subscribe(
                      System.out::println,
                      System.out::println,
                      () -> System.out.println("Completed"));
        // 1
        // 2
        // Completed
        // Unsubscribed
    }


    public void exampleUnsafeSubscribe() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
            o.onNext(3);
            o.onComplete();
        });

        source.doOnDispose(() -> System.out.println("Unsubscribed"))
              .subscribe(new Observer<Integer>() {
                  @Override
                  public void onSubscribe(Disposable disposable) {
                      System.out.println("Completed");
                  }


                  @Override
                  public void onComplete() {
                      System.out.println("Completed");
                  }


                  @Override
                  public void onError(Throwable e) {
                      System.out.println(e);
                  }


                  @Override
                  public void onNext(Integer t) {
                      System.out.println(t);
                  }
              });

        // 1
        // 2
        // Completed
        // 3
        // Completed
    }


    public void exampleSerialize() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
            o.onNext(3);
            o.onComplete();
        })
                                               .cast(Integer.class)
                                               .serialize();
        ;


        source.doOnDispose(() -> System.out.println("Unsubscribed"))
              .subscribe(new Observer<Integer>() {
                  @Override
                  public void onSubscribe(Disposable disposable) {
                      System.out.println("Subscribed");
                  }


                  @Override
                  public void onComplete() {
                      System.out.println("Completed");
                  }


                  @Override
                  public void onError(Throwable e) {
                      System.out.println(e);
                  }


                  @Override
                  public void onNext(Integer t) {
                      System.out.println(t);
                  }
              });

//		1
//		2
//		Completed
    }


    //
    // Tests
    //


    @Test
    public void testSafeSubscribe() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
            o.onNext(3);
            o.onComplete();
        });

        final TestObserver<Integer> tester = source.test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
        tester.assertNotSubscribed();
    }


    @Test
    public void testUnsafeSubscribe() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
            o.onNext(3);
            o.onComplete();
        });

        final TestObserver<Integer> tester =
                source.doOnDispose(() -> System.out.println("Unsubscribed")).test();

        tester.assertValues(3);
        Assert.assertEquals(2, tester.getEvents().size());
        tester.assertNoErrors();
        tester.assertSubscribed();
    }


    @Test
    public void testSerialize() {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
            o.onNext(3);
            o.onComplete();
        })
                                               .cast(Integer.class)
                                               .serialize();
        ;


        final TestObserver<Integer> tester =
                source.doOnDispose(() -> System.out.println("Unsubscribed")).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();
        tester.assertSubscribed();
    }
}
