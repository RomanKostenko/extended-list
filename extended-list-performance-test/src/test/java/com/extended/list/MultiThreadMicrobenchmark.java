package com.extended.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
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
public class MultiThreadMicrobenchmark {
  private static final int      BUFFER_SIZE                = 10000;
  private static final A[]      BUFFER                     = new A[BUFFER_SIZE];

  private final Vector<A>       vectorForGet               = new Vector<A>();
  private final Vector<A>       vectorForSet               = new Vector<A>();

  private final List<A>         synchronizedListForGet     = Collections.synchronizedList(new ArrayList<A>());
  private final List<A>         synchronizedListForSet     = Collections.synchronizedList(new ArrayList<A>());

  private final List<A>         copyOnWriteArrayListForGet = new CopyOnWriteArrayList<A>();
  private final List<A>         copyOnWriteArrayListForSet = new CopyOnWriteArrayList<A>();

  private final ExtendedList<A> extendedListForGet         = new ExtendedList<A>();
  private final ExtendedList<A> extendedListForSet         = new ExtendedList<A>();

  @Setup
  public void setUp() {
    final Random random = new Random();
    for (int i = 0; i < BUFFER_SIZE; i++)
      BUFFER[i] = new A(random.nextInt());

    for (int i = 0; i < BUFFER_SIZE; i++) {
      vectorForGet.add(BUFFER[i]);
      vectorForSet.add(BUFFER[i]);

      extendedListForGet.add(BUFFER[i]);
      extendedListForSet.add(BUFFER[i]);

      synchronizedListForGet.add(BUFFER[i]);
      synchronizedListForSet.add(BUFFER[i]);

      copyOnWriteArrayListForGet.add(BUFFER[i]);
      copyOnWriteArrayListForSet.add(BUFFER[i]);
    }
  }

  @Benchmark
  public Vector<A> addVector() {
    final Vector<A> vectorForAdd = new Vector<A>();

    for (int i = 0; i < BUFFER_SIZE; i++)
      vectorForAdd.add(BUFFER[i]);

    return vectorForAdd;
  }

  @Benchmark
  public List<A> addSynchronizedList() {
    final List<A> synchronizedList = Collections.synchronizedList(new ArrayList<A>());

    for (int i = 0; i < BUFFER_SIZE; i++)
      synchronizedList.add(BUFFER[i]);

    return synchronizedList;
  }

  @Benchmark
  public List<A> addCopyOnWriteArrayList() {
    final List<A> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    for (int i = 0; i < BUFFER_SIZE; i++)
      copyOnWriteArrayList.add(BUFFER[i]);

    return copyOnWriteArrayList;
  }

  @Benchmark
  public List<A> addExtendedList() {
    final ExtendedList<A> extendedList = new ExtendedList<>();

    for (int i = 0; i < BUFFER_SIZE; i++)
      extendedList.add(BUFFER[i]);

    return extendedList;
  }

  @Benchmark
  public Vector<A> setVector() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      vectorForSet.set(i, BUFFER[j]);

    return vectorForSet;
  }

  @Benchmark
  public List<A> setSynchronizedList() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      synchronizedListForSet.set(i, BUFFER[j]);

    return synchronizedListForSet;
  }

  @Benchmark
  public List<A> setCopyOnWriteArrayList() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      copyOnWriteArrayListForSet.set(i, BUFFER[j]);

    return copyOnWriteArrayListForSet;
  }

  @Benchmark
  public List<A> setExtendedList() {
    for (int i = 0, j = BUFFER_SIZE - 1; i < BUFFER_SIZE; i++, j--)
      extendedListForSet.set(i, BUFFER[j]);

    return extendedListForSet;
  }

  @Benchmark
  public long getVector() {
    long result = 0;

    final int sizeOfList = vectorForGet.size();

    for (int i = 0; i < sizeOfList; i++)
      result += vectorForGet.get(i).getI();

    return result;
  }

  @Benchmark
  public long getSynchronizedList() {
    long result = 0;

    final int sizeOfList = synchronizedListForGet.size();

    for (int i = 0; i < sizeOfList; i++)
      result += synchronizedListForGet.get(i).getI();

    return result;
  }

  @Benchmark
  public long getCopyOnWriteArrayList() {
    long result = 0;

    final int sizeOfList = copyOnWriteArrayListForGet.size();

    for (int i = 0; i < sizeOfList; i++)
      result += copyOnWriteArrayListForGet.get(i).getI();

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

  public static void main(final String[] args) throws RunnerException {
    final Options opt = new OptionsBuilder().include(MultiThreadMicrobenchmark.class.getSimpleName()).warmupIterations(3)
        .measurementIterations(3).threads(8).forks(1).build();

    new Runner(opt).run();
  }

}
