package itrx.chapter4.backpressure;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import org.reactivestreams.Subscription;

/**
 * An Rx Subscriber that does not accept any items unless manually requested to.
 *
 * @param <T>
 * @author Chris
 */
public class ControlledPullSubscriber<T> implements Observer<T>, Subscription {

    private final Consumer<T> onNextAction;

    private final Consumer<Throwable> onErrorAction;

    private final Action onCompleteAction;


    public ControlledPullSubscriber(
            Consumer<T> onNextAction,
            Consumer<Throwable> onErrorAction,
            Action onCompleteAction) {
        this.onNextAction = onNextAction;
        this.onErrorAction = onErrorAction;
        this.onCompleteAction = onCompleteAction;
    }


    public ControlledPullSubscriber(
            Consumer<T> onNextAction,
            Consumer<Throwable> onErrorAction) {
        this(onNextAction, onErrorAction, () -> {
        });
    }


    public ControlledPullSubscriber(Consumer<T> onNextAction) {
        this(onNextAction, e -> {
        }, () -> {
        });
    }


    @Override
    public void onSubscribe(Disposable disposable) {
        request(0);
    }


    @Override
    public void onComplete() {
        try {
            onCompleteAction.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(Throwable e) {
        try {
            onErrorAction.accept(e);
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    @Override
    public void onNext(T t) {
        try {
            onNextAction.accept(t);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void requestMore(int n) {
        request(n);
    }


    @Override
    public void request(final long l) {
        throw new UnsupportedOperationException("ControlledPullSubscriber#request([l])");
    }


    @Override
    public void cancel() {

    }
}
