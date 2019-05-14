package com.meyling.guava;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * @author Jan-Willem Gmelig Meyling - https://gist.github.com/JWGmeligMeyling/9ff670adda720b482ee136a8cbbd3738#file-tablecollector-java
 */
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TableCollector<T, A> implements Collector<T, A, A> {

    static final Set<Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, A> finisher;
    private final Set<Characteristics> characteristics = CH_ID;

    TableCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner) {
        this(supplier, accumulator, combiner, Function.identity());
    }

    /**
     * @param supplier2
     * @param accumulator2
     * @param combiner2
     * @param identity
     */
    public TableCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, A> finisher) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
    }

    /**
     * Returns a merge function. This can be used to enforce the
     * assumption that the elements being collected are distinct.
     *
     * @param <T> the type of input arguments to the merge function
     * @return a merge function which always throw {@code IllegalStateException}
     */
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    /**
     * {@code BinaryOperator<Table>} that merges the contents of its right
     * argument into its left argument, using the provided merge function to
     * handle duplicate keys.
     *
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @param <M> type of the map
     * @param mergeFunction A merge function
     * @return a merge function for two maps
     */
    private static <R, C, V, M extends Table<R, C, V>> BinaryOperator<M> tableMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Cell<R, C, V> cell : m2.cellSet()) {
                R rowKey = cell.getRowKey();
                C columnKey = cell.getColumnKey();
                V value = cell.getValue();

                if (m1.contains(rowKey, columnKey)) {
                    V existingValue = m1.get(rowKey, columnKey);
                    value = mergeFunction.apply(existingValue, value);
                }

                m1.put(rowKey, columnKey, value);
            }
            return m1;
        };
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     * @param tableSupplier a function which returns a new, empty {@code Table} into
     *                    which the results will be inserted
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @param <M> type of the map
     * @return a {@code Collector} which collects elements into a {@code Table}
     */
    public static <T, R, C, V, M extends Table<R, C, V>> TableCollector<T, M> toTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper,
            BinaryOperator<V> mergeFunction,
            Supplier<M> tableSupplier) {
        BiConsumer<M, T> accumulator = (table, element) -> {
            R rowKey = rowMapper.apply(element);
            C columnKey = columnMapper.apply(element);
            V value = valueMapper.apply(element);

            if (table.contains(rowKey, columnKey)) {
                V existingValue = table.get(rowKey, columnKey);
                value = mergeFunction.apply(existingValue, value);
            }

            table.put(rowKey, columnKey, value);
        };

        return new TableCollector<>(tableSupplier, accumulator, tableMerger(mergeFunction));
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @return a {@code Collector} which collects elements into a {@code Table}
     * @see #toTable(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, V> TableCollector<T, HashBasedTable<R, C, V>> toHashBasedTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper,
            BinaryOperator<V> mergeFunction) {
        return toTable(rowMapper, columnMapper, valueMapper, mergeFunction, HashBasedTable::create);
    }


    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @return a {@code Collector} which collects elements into a {@code Table}
     * @see #toTable(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, V> TableCollector<T, HashBasedTable<R, C, V>> toHashBasedTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper) {
        return toTable(rowMapper, columnMapper, valueMapper, throwingMerger(), HashBasedTable::create);
    }


    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @return a {@code Collector} which collects elements into a {@code Table}
     * @see #toTable(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R extends Comparable, C extends Comparable, V> TableCollector<T, TreeBasedTable<R, C, V>> toTreeBasedTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper,
            BinaryOperator<V> mergeFunction) {
        return toTable(rowMapper, columnMapper, valueMapper, mergeFunction, TreeBasedTable::create);
    }


    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @return a {@code Collector} which collects elements into a {@code Table}
     * @see #toTable(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R extends Comparable, C extends Comparable, V> TableCollector<T, TreeBasedTable<R, C, V>> toTreeBasedTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper) {
        return toTable(rowMapper, columnMapper, valueMapper, throwingMerger(), TreeBasedTable::create);
    }


    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param rowComparator a comparator used for sorting row keys
     * @param columnComparator a comparator used for sorting column keys
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @return a {@code Collector} which collects elements into a {@code Table}
     * @see #toTable(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, V> TableCollector<T, TreeBasedTable<R, C, V>> toTreeBasedTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper,
            Comparator<? super R> rowComparator,
            Comparator<? super C> columnComparator,
            BinaryOperator<V> mergeFunction) {
        return toTable(rowMapper, columnMapper, valueMapper, mergeFunction, () -> TreeBasedTable.create(rowComparator, columnComparator));
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Table} whose column keys, row keys and values are the result of applying the provided
     * mapping functions to the input elements.
     *
     * @param rowMapper a mapping function to produce row keys
     * @param columnMapper a mapping function to produce column keys
     * @param valueMapper a mapping function to produce values
     * @param rowComparator a comparator used for sorting row keys
     * @param columnComparator a comparator used for sorting column
     * @param <T> type of the input elements
     * @param <R> type of the table rows
     * @param <C> type of the table columns
     * @param <V> type of the table values
     * @return a {@code Collector} which collects elements into a {@code Table}
     * @see #toTable(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, V> TableCollector<T, TreeBasedTable<R, C, V>> toTreeBasedTable(Function<? super T, ? extends R> rowMapper,
            Function<? super T, ? extends C> columnMapper,
            Function<? super T, ? extends V> valueMapper,
            Comparator<? super R> rowComparator,
            Comparator<? super C> columnComparator) {
        return toTable(rowMapper, columnMapper, valueMapper, throwingMerger(), () -> TreeBasedTable.create(rowComparator, columnComparator));
    }

    @Override
    public Supplier<A> supplier() {
        return supplier;
    }

    @Override
    public BiConsumer<A, T> accumulator() {
        return accumulator;
    }

    @Override
    public BinaryOperator<A> combiner() {
        return combiner;
    }

    @Override
    public Function<A, A> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return characteristics;
    }

}