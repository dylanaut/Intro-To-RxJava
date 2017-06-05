package itrx.helper;


import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PrintObserver<T> implements Observer<T>, SingleObserver<T>, MaybeObserver<T> {
    private final String name;


    private PrintObserver(String name) {
        this.name = name;
    }


    public static <T> PrintObserver<T> printObserver(String name) {
        return new PrintObserver<T>(name);
    }


    @Override
    public void onComplete() {
        System.out.println(name + ": Completed");
    }


    @Override
    public void onError(Throwable e) {
        System.out.println(name + ": Error: " + e);
    }


    @Override
    public void onSubscribe(final Disposable disposable) {
        System.out.println("Start subscription");
    }


    @Override
    public void onNext(T v) {
        System.out.println(name + ": " + v);
    }


    @Override
    public void onSuccess(T v) {
        System.out.println(name + ": " + v);
    }
}
