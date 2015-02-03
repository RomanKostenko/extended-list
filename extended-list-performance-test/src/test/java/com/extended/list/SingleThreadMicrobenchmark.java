package com.extended.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SingleThreadMicrobenchmark {
  private final static int      bufferSize            = 10;
  private final static A[]      buffer                = new A[bufferSize];

  private final LinkedList<A>   linkedListForSet      = new LinkedList<A>();
  private final LinkedList<A>   linkedListForGet      = new LinkedList<A>();
  private final LinkedList<A>   linkedListForRemove   = new LinkedList<A>();

  private final ArrayList<A>    arrayListForSet       = new ArrayList<A>();
  private final ArrayList<A>    arrayListForGet       = new ArrayList<A>();
  private final ArrayList<A>    arrayListForRemove    = new ArrayList<A>();

  private final ExtendedList<A> extendedListForGet    = new ExtendedList<A>();
  private final ExtendedList<A> extendedListForSet    = new ExtendedList<A>();
  private final ExtendedList<A> extendedListForRemove = new ExtendedList<A>();

  @Setup
  public void setUp() {
    final Random random = new Random();
    for (int i = 0; i < bufferSize; i++)
      buffer[i] = new A(random.nextInt());

    for (int i = 0; i < bufferSize; i++) {
      linkedListForSet.add(buffer[i]);
      linkedListForGet.add(buffer[i]);
      linkedListForRemove.add(buffer[i]);

      arrayListForSet.add(buffer[i]);
      arrayListForGet.add(buffer[i]);
      arrayListForRemove.add(buffer[i]);

      extendedListForGet.add(buffer[i]);
      extendedListForSet.add(buffer[i]);
      extendedListForRemove.add(buffer[i]);
    }
  }

  // @Benchmark
  // public List<A> addLinkedList() {
  // final LinkedList<A> linkedList = new LinkedList<>();
  //
  // for (final A a : buffer)
  // linkedList.add(a);
  //
  // return linkedList;
  // }
  //
  // @Benchmark
  // public List<A> addArrayList() {
  // final ArrayList<A> arrayList = new ArrayList<>();
  //
  // for (final A a : buffer)
  // arrayList.add(a);
  //
  // return arrayList;
  // }
  //
  // @Benchmark
  // public List<A> addExtendedList() {
  // final ExtendedList<A> arrayList = new ExtendedList<>();
  //
  // for (final A a : buffer)
  // arrayList.add(a);
  //
  // return arrayList;
  // }
  //
  // @Benchmark
  // public List<A> setLinkedList() {
  // for (int i = 0, j = bufferSize - 1; i < bufferSize; i++, j--)
  // linkedListForSet.set(i, buffer[j]);
  //
  // return linkedListForSet;
  // }
  //
  // @Benchmark
  // public List<A> setArrayList() {
  // for (int i = 0, j = bufferSize - 1; i < bufferSize; i++, j--)
  // arrayListForSet.set(i, buffer[j]);
  //
  // return arrayListForSet;
  // }
  //
  // @Benchmark
  // public List<A> setExtendedList() {
  // for (int i = 0, j = bufferSize - 1; i < bufferSize; i++, j--)
  // extendedListForSet.set(i, buffer[j]);
  //
  // return extendedListForSet;
  // }
  //
  // @Benchmark
  // public long getLinkedListWithIterator() {
  // long result = 0;
  //
  // for (final A a : linkedListForGet)
  // result += a.getI();
  //
  // return result;
  // }
  //
  // @Benchmark
  // public long getArrayList() {
  // long result = 0;
  //
  // final int sizeOfList = arrayListForGet.size();
  //
  // for (int i = 0; i < sizeOfList; i++)
  // result += arrayListForGet.get(i).getI();
  //
  // return result;
  // }
  //
  // @Benchmark
  // public long getExtendedList() {
  // long result = 0;
  //
  // final int sizeOfList = extendedListForGet.size();
  //
  // for (int i = 0; i < sizeOfList; i++)
  // result += extendedListForGet.get(i).getI();
  //
  // return result;
  // }
  //
  // @Benchmark
  // public long removeLinkedList() {
  // long result = 0;
  //
  // for (int i = linkedListForRemove.size() - 1; i >= 0; i--)
  // result += linkedListForRemove.remove(i).getI();
  //
  // return result;
  // }
  //
  // @Benchmark
  // public long removeArrayList() {
  // long result = 0;
  //
  // for (int i = arrayListForRemove.size() - 1; i >= 0; i--)
  // result += arrayListForRemove.remove(i).getI();
  //
  // return result;
  // }

  public long removeExtendedList() {
    long result = 0;
    for (; extendedListForRemove.size() > 0;) {
      final A a = extendedListForRemove.remove(0);
      if (a != null)
        result += a.getI();
      else
        System.out.println("!!!!!!!!!!!!!!!!!!!! ");
    }
    return result;
  }

  public static void main(final String[] args) throws RunnerException {
    // final Options opt = new OptionsBuilder().include(SingleThreadMicrobenchmark.class.getSimpleName()).warmupIterations(0)
    // .measurementIterations(1).forks(1).build();
    //
    // new Runner(opt).run();
    final ExtendedList<A> extendedListForRemove = new ExtendedList<A>();
    final Random random = new Random();
    for (int i = 0; i < bufferSize; i++)
      buffer[i] = new A(random.nextInt());

    for (int i = 0; i < bufferSize; i++)
      extendedListForRemove.add(buffer[i]);

    long result = 0;
    for (; extendedListForRemove.size() > 0;) {
      System.out.println(extendedListForRemove.size());
      final A a = extendedListForRemove.remove(0);
      if (a != null)
        result += a.getI();
      else
        System.out.println("!!!!!!!!!!!!!!!!!!!! ");
    }
  }

}
