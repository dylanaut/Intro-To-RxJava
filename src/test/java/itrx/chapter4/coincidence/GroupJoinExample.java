package itrx.chapter4.coincidence;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import static io.reactivex.Observable.interval;

public class GroupJoinExample {

    public void example() {
        Observable<String> left =
                interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> "L" + i)
                        .take(6);
        Observable<String> right =
                interval(200, TimeUnit.MILLISECONDS)
                        .map(i -> "R" + i)
                        .take(3);

        left.groupJoin(
                right,
                i -> Observable.never(),
                i -> Observable.timer(0, TimeUnit.MILLISECONDS),
                (l, rs) -> rs.toList().subscribe(list -> System.out.println(l + ": " + list))
        )
            .subscribe();

        // L0: [R0, R1, R2]
        // L1: [R0, R1, R2]
        // L2: [R1, R2]
        // L3: [R1, R2]
        // L4: [R2]
        // L5: [R2]
    }


    @Test
    public void test() {
        TestObserver<Object> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> left = interval(100, TimeUnit.MILLISECONDS, scheduler)
                .take(6);
        Observable<Long> right = interval(200, TimeUnit.MILLISECONDS, scheduler)
                .take(3);

        left.groupJoin(right,
                i -> Observable.never(),
                i -> Observable.timer(0, TimeUnit.MILLISECONDS, scheduler),
                (l, rs) -> rs.toList().map(rl -> Tuple.create(l, rl)))
            .flatMap((Function<Single<Tuple<Long, List<Long>>>, ObservableSource<?>>) Single::toObservable).test();

        scheduler.advanceTimeTo(600, TimeUnit.MILLISECONDS);
        tester.assertValues(Arrays.asList(
                Tuple.create(0L, Arrays.asList(0L, 1L, 2L)),
                Tuple.create(1L, Arrays.asList(0L, 1L, 2L)),
                Tuple.create(2L, Arrays.asList(1L, 2L)),
                Tuple.create(3L, Arrays.asList(1L, 2L)),
                Tuple.create(4L, Arrays.asList(2L)),
                Tuple.create(5L, Arrays.asList(2L))
        ));
    }


    //
    // Test
    //


    @Test
    public void exampleEquivalence() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Object> testerJoin = TestObserver.create();
        TestObserver<Object> testerGroupJoin = TestObserver.create();

        Observable<Long> left =
                interval(100, TimeUnit.MILLISECONDS, scheduler);
        Observable<Long> right =
                interval(100, TimeUnit.MILLISECONDS, scheduler);

        left.join(
                right,
                i -> Observable.timer(150, TimeUnit.MILLISECONDS, scheduler),
                i -> Observable.timer(0, TimeUnit.MILLISECONDS, scheduler),
                (l, r) -> Tuple.create(l, r)
        )
            .take(10)
            .subscribe(testerJoin);

        left.groupJoin(
                right,
                i -> Observable.timer(150, TimeUnit.MILLISECONDS, scheduler),
                i -> Observable.timer(0, TimeUnit.MILLISECONDS, scheduler),
                (l, rs) -> rs.map(r -> Tuple.create(l, r))
        )
            .flatMap(i -> i)
            .take(10)
            .subscribe(testerGroupJoin);

        scheduler.advanceTimeTo(600, TimeUnit.MILLISECONDS);
        testerJoin.assertValueSequence(testerGroupJoin.getEvents());
    }


    private static class Tuple<T1, T2> {
        public final T1 item1;

        public final T2 item2;


        public Tuple(T1 item1, T2 item2) {
            this.item1 = item1;
            this.item2 = item2;
        }


        public static <T1, T2> Tuple<T1, T2> create(T1 item1, T2 item2) {
            return new Tuple<T1, T2>(item1, item2);
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Tuple<?, ?>) {
                Tuple<?, ?> other = (Tuple<?, ?>) obj;
                return this.item1.equals(other.item1) &&
                        this.item2.equals(other.item2);
            }
            return false;
        }


        @Override
        public String toString() {
            return "(" + item1 + ", " + item2 + ")";
        }
    }
}
