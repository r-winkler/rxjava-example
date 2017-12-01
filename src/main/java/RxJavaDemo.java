import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

public class RxJavaDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--------------------------------------------");
        Observable.range(1, 5).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        List<String> words = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
        );
        Observable.just(words).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        Observable.fromIterable(words).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        String[] arr = {"A", "B", "C", "D"};
        Observable.fromArray(arr).map(String::toLowerCase).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        Observable.fromArray(arr).map(String::toLowerCase).take(2).subscribe(
            System.out::println,
            e -> System.out.println("Error occured: " + e),
            () -> System.out.println("Completed")
        );

        System.out.println("--------------------------------------------");
        Observable.fromArray(arr).flatMap(i -> Observable.fromArray(i.toLowerCase(), i.toUpperCase()))
            .subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        Observable.fromArray(arr).scan((previous, current) -> previous + current).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        Observable.fromArray(arr).buffer(2).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        Observable.fromArray(arr).takeWhile(i -> !i.equals("C")).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        Observable.fromArray(arr).skipWhile(i -> !i.equals("C")).subscribe(System.out::println);

        System.out.println("--------------------------------------------");
        ConnectableObservable connectableObservable = ConnectableObservable.fromArray(arr).publish();
        connectableObservable.subscribe(System.out::println);
        connectableObservable.connect();


        System.out.println("--------------------------------------------");
        List<Double> randomValues = DoubleStream
            .iterate(0, n -> Math.random() * 100)
            .limit(1_000_000)
            .boxed()
            .collect(toList());
        Observable<Double> values = Observable.fromIterable(randomValues);
        Observable<Long> clock = Observable.interval(10, TimeUnit.MILLISECONDS);

        values
            .zipWith(clock, (v, t) -> v)
            .buffer(5, TimeUnit.SECONDS)
            .map(buffer -> buffer.stream().mapToDouble(l -> l).summaryStatistics())
            .subscribe(System.out::println);


        System.out.println("--------------------------------------------");
        Observable.<Double>create(emitter -> {
            while(true) {
                emitter.onNext(Math.random() * 10);
                TimeUnit.SECONDS.sleep(1);
            }
        })
            .buffer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .map(buffer -> buffer.stream().mapToDouble(l -> l).average().getAsDouble())
            .subscribe(d -> System.out.println(Thread.currentThread() + "avg: " + d));

        while(true);
    }

}
