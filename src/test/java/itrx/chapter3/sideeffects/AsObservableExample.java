package itrx.chapter3.sideeffects;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AsObservableExample {

    public void exampleModifyReference() {
        BrakeableService service = new BrakeableService();
        service.items.subscribe((i) -> System.out.println("Before: " + i));
        service.items = BehaviorSubject.createDefault("Later");
        service.items.subscribe((i) -> System.out.println("After: " + i));
        service.play();

        // Before: Greet
        // After: Greet
        // After: Hello
        // After: and
        // After: goodbye
    }


    public void examplePush() {
        BrakeableService2 service = new BrakeableService2();

        service.getValuesUnsafe().subscribe(System.out::println);
        service.getValuesUnsafe().onNext("GARBAGE");
        service.play();

        // Greet
        // GARBAGE
        // Hello
        // and
        // goodbye
    }


    public void examplePush2() {
        BrakeableService2 service = new BrakeableService2();

        service.getValuesUnsafe2().subscribe(System.out::println);
        if (service.getValuesUnsafe2() instanceof Subject<?>) {
            @SuppressWarnings("unchecked")
            Subject<String> subject = (Subject<String>) service.getValuesUnsafe2();
            subject.onNext("GARBAGE");
        }
        service.play();

        // Greet
        // GARBAGE
        // Hello
        // and
        // goodbye
    }


    public void exampleSafe() {
        SafeService service = new SafeService();

        service.getValues().subscribe(System.out::println);
        if (service.getValues() instanceof Subject<?>) {
            System.out.println("Not safe!");
        }
        service.play();

        // Greet
        // Hello
        // and
        // goodbye
    }


    @Test
    public void testModifyReference() {
        TestObserver<String> testerBefore = TestObserver.create();
        TestObserver<String> testerAfter = TestObserver.create();

        BrakeableService service = new BrakeableService();
        service.items.subscribe(testerBefore);
        service.items = BehaviorSubject.createDefault("Later");
        service.items.subscribe(testerAfter);
        service.play();

        testerBefore.assertValues("Greet");
        testerAfter.assertValues(
                "Later",
                "Hello",
                "and",
                "goodbye"
        );
    }


    @Test
    public void testPush() {
        TestObserver<String> tester = TestObserver.create();

        BrakeableService2 service = new BrakeableService2();
        service.getValuesUnsafe()
               .test();
        service.getValuesUnsafe().onNext("GARBAGE");
        service.play();

        tester.assertValues(
                "Greet",
                "GARBAGE",
                "Hello",
                "and",
                "goodbye"
        );
    }


    @Test
    public void testPush2() {
        BrakeableService2 service = new BrakeableService2();

        assertTrue(service.getValuesUnsafe2() instanceof Subject<?>);
    }


    //
    // Tests
    //


    @Test
    public void testSafe() {
        SafeService service = new SafeService();

        assertFalse(service.getValues() instanceof Subject<?>);
    }


    public static class BrakeableService {
        public BehaviorSubject<String> items = BehaviorSubject.createDefault("Greet");


        public void play() {
            items.onNext("Hello");
            items.onNext("and");
            items.onNext("goodbye");
        }
    }


    public static class BrakeableService2 {
        private final BehaviorSubject<String> items = BehaviorSubject.createDefault("Greet");


        public BehaviorSubject<String> getValuesUnsafe() {
            return items;
        }


        public Observable<String> getValuesUnsafe2() {
            return items;
        }


        public void play() {
            items.onNext("Hello");
            items.onNext("and");
            items.onNext("goodbye");
        }
    }


    public static class SafeService {
        private final BehaviorSubject<String> items = BehaviorSubject.createDefault("Greet");


        public Observable<String> getValues() {
            return Observable.just(items.getValue());
        }


        public void play() {
            items.onNext("Hello");
            items.onNext("and");
            items.onNext("goodbye");
        }
    }
}
