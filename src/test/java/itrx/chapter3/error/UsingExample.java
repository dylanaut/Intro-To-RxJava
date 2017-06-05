package itrx.chapter3.error;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

public class UsingExample {

    public void exampleUsing() {
        Observable<Character> values = Observable.using(
                () -> {
                    String resource = "MyResource";
                    System.out.println("Leased: " + resource);
                    return resource;
                },
                (resource) -> Observable.create(o -> {
                    for (Character c : resource.toCharArray()) {
                        o.onNext(c);
                    }
                    o.onComplete();
                }),
                (resource) -> System.out.println("Disposed: " + resource));

        values.subscribe(
                System.out::println,
                System.out::println);

        // Leased: MyResource
        // M
        // y
        // R
        // e
        // s
        // o
        // u
        // r
        // c
        // e
        // Disposed: MyResource
    }


    //
    // Test
    //


    @Test
    public void testUsing() {        String[] leaseRelease = {"", ""};

        Observable<Character> values = Observable.using(
                () -> {
                    String resource = "MyResource";
                    leaseRelease[0] = resource;
                    return resource;
                },
                (resource) -> Observable.create(o -> {
                    for (Character c : resource.toCharArray()) {
                        o.onNext(c);
                    }
                    o.onComplete();
                }),
                (resource) -> leaseRelease[1] = resource);

        final TestObserver<Character> tester = values.test();

        Assert.assertEquals(leaseRelease[0], leaseRelease[1]);
        tester.assertValues('M', 'y', 'R', 'e', 's', 'o', 'u', 'r', 'c', 'e');
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
