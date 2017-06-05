package itrx.chapter2.aggregation;

import java.util.*;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

import static itrx.helper.PrintObserver.printObserver;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ToMapExample {

    public void exampleToMap() {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values.toMap(person -> person.name)
              .subscribe(printObserver("toMap"));

        // toMap: {Saul=Person@7cd84586, Nick=Person@30dae81, Will=Person@1b2c6ec2}
        // toMap: Completed
    }


    public void exampleToMapWithSelector() {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values.toMap(
                person -> person.name,
                person -> person.age)
              .subscribe(printObserver("toMap"));

        // toMap: {Saul=35, Nick=40, Will=25}
        // toMap: Completed
    }


    public void exampleToMapWithCustomContainer() {
        Observable<Person> values = Observable.just(
                new Person("Will", 25),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values.toMap(person -> person.name,
                person -> person.age,
                HashMap::new)
              .subscribe(printObserver("toMap"));

        // toMap: {Saul=35, Nick=40, Will=25}
        // toMap: Completed
    }


    public void exampleToMultimap() {
        Observable<Person> values = Observable.just(
                new Person("Will", 35),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        values.toMultimap(
                person -> person.age,
                person -> person.name)
              .subscribe(printObserver("toMap"));

        // toMap: {35=[Will, Saul], 40=[Nick]}
        // toMap: Completed
    }


    public void exampleToMultimapWithCustomContainers() {
        Observable<Person> values = Observable.just(
                new Person("Will", 35),
                new Person("Nick", 40),
                new Person("Saul", 35)
        );

        // // ToDo demonstrate toMultiMap :-) [kreyj, 05.06.2017]

        values.toMultimap(
                person -> person.age,
                person -> person.name,
                () -> new HashMap<Integer, Collection<String>>(),
                (key) -> new ArrayList<String>())
              .subscribe(printObserver("toMap"));

        // toMap: {35=[Will, Saul], 40=[Nick]}
        // toMap: Completed
    }


    @SuppressWarnings("serial")
    @Test
    public void testToMap() {
        TestObserver<Map<String, Person>> tester = TestObserver.create();

        Person will = new Person("Will", 25);
        Person nick = new Person("Nick", 40);
        Person saul = new Person("Saul", 35);

        Observable<Person> values = Observable.just(
                will, nick, saul
        );

        values.toMap(person -> person.name).test();

        Assert.assertEquals(tester.getEvents(), asList(new HashMap<String, Person>() {{
            this.put(will.name, will);
            this.put(nick.name, nick);
            this.put(saul.name, saul);
        }}));
        tester.assertComplete();
        tester.assertNoErrors();

//		toMap: {Saul=Person@7cd84586, Nick=Person@30dae81, Will=Person@1b2c6ec2}
//		toMap: Completed
    }


    //
    // Tests
    //


    @SuppressWarnings("serial")
    @Test
    public void testToMapWithSelector() {
        TestObserver<Map<String, Integer>> tester = TestObserver.create();

        Person will = new Person("Will", 25);
        Person nick = new Person("Nick", 40);
        Person saul = new Person("Saul", 35);

        Observable<Person> values = Observable.just(
                will, nick, saul
        );

        values.toMap(
                person -> person.name,
                person -> person.age).test();

        Assert.assertEquals(tester.getEvents(), asList(new HashMap<String, Integer>() {{
            this.put(will.name, will.age);
            this.put(nick.name, nick.age);
            this.put(saul.name, saul.age);
        }}));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @SuppressWarnings("serial")
    @Test
    public void testToMapWithCustomContainer() {
        TestObserver<Map<String, Integer>> tester = TestObserver.create();

        Person will = new Person("Will", 25);
        Person nick = new Person("Nick", 40);
        Person saul = new Person("Saul", 35);

        Observable<Person> values = Observable.just(
                will, nick, saul
        );

        values.toMap(
                person -> person.name,
                person -> person.age,
                HashMap::new).test();

        Assert.assertEquals(tester.getEvents(), asList(new HashMap<String, Integer>() {{
            this.put(will.name, will.age);
            this.put(nick.name, nick.age);
            this.put(saul.name, saul.age);
        }}));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @SuppressWarnings("serial")
    @Test
    public void testToMultimap() {
        TestObserver<Map<Integer, Collection<String>>> tester = TestObserver.create();

        Person will = new Person("Will", 35);
        Person nick = new Person("Nick", 40);
        Person saul = new Person("Saul", 35);

        Observable<Person> values = Observable.just(
                will, nick, saul
        );

        values.toMultimap(
                person -> person.age,
                person -> person.name).test();

        Assert.assertEquals(tester.getEvents(), asList(new HashMap<Integer, Collection<String>>() {{
            this.put(35, asList(will.name, saul.name));
            this.put(40, asList(nick.name));
        }}));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @SuppressWarnings("serial")
    @Test
    public void testToMultimapWithCustomContainers() {
        TestObserver<Map<Integer, Collection<String>>> tester = TestObserver.create();

        Person will = new Person("Will", 35);
        Person nick = new Person("Nick", 40);
        Person saul = new Person("Saul", 35);

        Observable<Person> values = Observable.just(
                will, nick, saul
        );

        values.toMultimap(
                p -> p.age, p -> p.name,
                () -> new HashMap<Integer, Collection<String>>(),
                (key) -> new ArrayList<String>()).test();

        /*
        YOU MUST USE THIS SOURCECODE, because generic type inference does not work otherwise!!
        values.toMultimap(
                 p -> p.age, p -> p.name,
                () -> new HashMap<Integer, Collection<String>>(),
                (key) -> new ArrayList<String>()).test();
      */

        Assert.assertEquals(tester.getEvents(), asList(new HashMap<Integer, Collection<String>>() {{
            this.put(35, asList(will.name, saul.name));
            this.put(40, singletonList(nick.name));
        }}));
        tester.assertComplete();
        tester.assertNoErrors();
    }


    private static class Person {
        public final String name;

        public final Integer age;


        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Person) {
                Person o = (Person) obj;
                return Objects.equals(this.name, o.name) &&
                        Objects.equals(this.age, o.age);
            }
            return false;
        }
    }
}


