package com.xtrade.order.book.misc;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.function.Function;

@EqualsAndHashCode
@RequiredArgsConstructor
class Tuple2Impl<T, U> implements Tuple2<T, U> {
    @Getter
    private final T _1;

    @Getter
    private final U _2;
}

public interface Tuple2<T, U> {
    T get_1();
    U get_2();

    static <T,U> Tuple2<T, U> newInstance(T x, U y) {
        return new Tuple2Impl<>(x, y);
    }

    static <X extends Comparable<X>, Y extends Comparable<Y>> Comparator<Tuple2<X, Y>> getComparator(Class<X> cls1, Class<Y> cls2) {
        return Comparator
            .comparing((Function<Tuple2<X, Y>, X>) Tuple2::get_1)
            .thenComparing(Tuple2::get_2);
    }

    static <X extends Comparable<X>, Y extends Comparable<Y>> Comparator<Tuple2<X, Y>> getComparator(Tuple2<X, Y> tuple) {
        return Comparator
            .comparing((Function<Tuple2<X, Y>, X>) Tuple2::get_1)
            .thenComparing(Tuple2::get_2);
    }
}

