package com.extended.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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
public class ExtendedListMicrobenchmark {
  private final int           bufferSize       = 1000;
  private final A[]           buffer           = new A[bufferSize];

  private final LinkedList<A> linkedListForSet = new LinkedList<A>();
  private final LinkedList<A> linkedListForGet = new LinkedList<A>();

  private final ArrayList<A>  arrayListForSet  = new ArrayList<A>();
  private final ArrayList<A>  arrayListForGet  = new ArrayList<A>();

  @Setup
  public void setUp() {
    final Random random = new Random();
    for (int i = 0; i < bufferSize; i++)
      buffer[i] = new A(random.nextInt());

    for (int i = 0; i < bufferSize; i++) {
      linkedListForSet.add(buffer[i]);
      linkedListForGet.add(buffer[i]);
      arrayListForSet.add(buffer[i]);
      arrayListForGet.add(buffer[i]);
    }
  }

  @Benchmark
  public List<A> addLinkedList() {
    final LinkedList<A> linkedList = new LinkedList<>();

    for (final A a : buffer)
      linkedList.add(a);

    return linkedList;
  }

  @Benchmark
  public List<A> addArrayList() {
    final ArrayList<A> arrayList = new ArrayList<>();

    for (final A a : buffer)
      arrayList.add(a);

    return arrayList;
  }

  @Benchmark
  public List<A> setLinkedList() {
    for (int i = 0, j = bufferSize - 1; i < bufferSize; i++, j--)
      linkedListForGet.set(i, buffer[j]);

    return linkedListForGet;
  }

  @Benchmark
  public List<A> setArrayList() {
    for (int i = 0, j = bufferSize - 1; i < bufferSize; i++, j--)
      arrayListForSet.set(i, buffer[j]);

    return arrayListForSet;
  }

  @Benchmark
  public long getLinkedList() {
    long result = 0;

    final int sizeOfList = linkedListForGet.size();

    for (int i = 0; i < sizeOfList; i++)
      result += linkedListForGet.get(i).getI();

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

  public static void main(final String[] args) throws RunnerException {
    final Options opt = new OptionsBuilder().include(".*" + ExtendedListMicrobenchmark.class.getSimpleName() + ".*")
        .warmupIterations(5).measurementIterations(5).forks(1).build();

    new Runner(opt).run();
  }

}
