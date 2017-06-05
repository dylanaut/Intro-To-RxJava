package itrx.chapter3.sideeffects;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class MutablePipelineExample {

    public void example() {
        Observable<Data> data = Observable.just(
                new Data(1, "Microsoft"),
                new Data(2, "Netflix")
        );

        data.subscribe(d -> d.name = "Garbage");
        data.subscribe(d -> System.out.println(d.id + ": " + d.name));

        // 1: Garbage
        // 2: Garbage
    }


    @Test
    public void test() {
        Observable<Data> data = Observable.just(
                new Data(1, "Microsoft"),
                new Data(2, "Netflix")
        );

        data.subscribe(d -> d.name = "Garbage");
        final TestObserver<String> tester = data.map(d -> d.name).test();

        tester.assertValues("Garbage", "Garbage");
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Test
    //


    private static class Data {
        public int id;

        public String name;


        public Data(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
