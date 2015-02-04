package com.extended.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SingleThreadMicrobenchmark {
  private final static int      BUFFER_SIZE         = 10000;
  private final static A[]      BUFFER             = new A[BUFFER_SIZE];

  private final LinkedList<A>   linkedListForSet   = new LinkedList<A>();
  private final LinkedList<A>   linkedListForGet   = new LinkedList<A>();
  private LinkedList<A>         linkedListForRemove;

  private final ArrayList<A>    arrayListForSet    = new ArrayList<A>();
  private final ArrayList<A>    arrayListForGet    = new ArrayList<A>();
  private ArrayList<A>          arrayListForRemove = new ArrayList<A>();

  private final ExtendedList<A> extendedListForGet = new ExtendedList<A>();
  private final ExtendedList<A> extendedListForSet = new ExtendedList<A>();
  private ExtendedList<A>       extendedListForRemove;

  @Setup
  public void setUp() {
    final Random random = new Random();
    for (int i = 0; i < BUFFER_SIZE; i++)
      BUFFER[i] = new A(random.nextInt());

    for (int i = 0; i < BUFFER_SIZE; i++) {
      linkedListForSet.add(BUFFER[i]);
      linkedListForGet.add(BUFFER[i]);

      arrayListForSet.add(BUFFER[i]);
      arrayListForGet.add(BUFFER[i]);

      extendedListForGet.add(BUFFER[i]);
      extendedListForSet.add(BUFFER[i]);
    }
  }

  @Setup(Level.Invocation)
  public void setUpEachTime() {
    linkedListForRemove = new LinkedList<A>();
    arrayListForRemove = new ArrayList<A>();
    extendedListForRemove = new ExtendedList<A>();

    for (int i = 0; i < BUFFER_SIZE; i++) {
      linkedListForRemove.add(BUFFER[i]);
      arrayListForRemove.add(BUFFER[i]);
      extendedListForRemove.add(BUFFER[i]);
    }
  }

  @Benchmark
  public List<A> addLinkedList() {
    final LinkedList<A> linkedList = new LinkedList<>();

    for (final A a : BUFFER)
      linkedList.add(a);

    return linkedList;
  }

  @Benchmark
  public List<A> addArrayList() {
    final ArrayList<A> arrayList = new ArrayList<>();

    for (final A a : BUFFER)
      arrayList.add(a);

    return arrayList;
  }

  @Benchmark
  public List<A> addExtendedList() {
    final ExtendedList<A> arrayList = new ExtendedList<>();

    for (final A a : BUFFER)
      arrayList.add(a);

    return arrayList;
  }

  @Benchmark
  public List<A> setLinkedList() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      linkedListForSet.set(i, BUFFER[j]);

    return linkedListForSet;
  }

  @Benchmark
  public List<A> setArrayList() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      arrayListForSet.set(i, BUFFER[j]);

    return arrayListForSet;
  }

  @Benchmark
  public List<A> setExtendedList() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      extendedListForSet.set(i, BUFFER[j]);

    return extendedListForSet;
  }

  @Benchmark
  public long getLinkedListWithIterator() {
    long result = 0;

    for (final A a : linkedListForGet)
      result += a.getI();

    return result;
  }

  @Benchmark
  public long getArrayList() {
    long result = 0;

    final int sizeOfList = arrayListForGet.size();

    for (int i = 0; i < sizeOfList; i++)
      result += arrayListForGet.get(i).getI();

    return result;
  }

  @Benchmark
  public long getExtendedList() {
    long result = 0;

    final int sizeOfList = extendedListForGet.size();

    for (int i = 0; i < sizeOfList; i++)
      result += extendedListForGet.get(i).getI();

    return result;
  }

  @Benchmark
  public long removeLinkedList() {
    long result = 0;

    for (int i = 0; i < BUFFER_SIZE; i++)
      result += linkedListForRemove.remove().getI();

    return result;
  }

  @Benchmark
  public long removeArrayList() {
    long result = 0;

    for (int i = BUFFER_SIZE - 1; i >= 0; i--)
      result += arrayListForRemove.remove(i).getI();

    return result;
  }

  @Benchmark
  public long removeExtendedList() {
    long result = 0;

    for (int i = 0; i < BUFFER_SIZE; i++)
      result += extendedListForRemove.remove(0).getI();

    return result;
  }

  public static void main(final String[] args) throws RunnerException {
    final Options opt = new OptionsBuilder().include(SingleThreadMicrobenchmark.class.getSimpleName()).warmupIterations(3)
        .measurementIterations(3).forks(1).build();

    new Runner(opt).run();
  }
}
