package com.extended.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class ExtendedListPerformanceTest {

  private static final int      NUMBERS          = 100000;
  private static final int      THREADS          = 8;
  private static final int      INVOCATION_COUNT = 4;

  private final ExecutorService executorService  = Executors.newCachedThreadPool();
  private final List<Integer>   list             = new ExtendedList<Integer>();

  private long                  agregator        = 0;

  @Test(invocationCount = INVOCATION_COUNT)
  public void runTest() throws Exception {
    long start = System.currentTimeMillis();
    List<Future<Void>> futures = new ArrayList<Future<Void>>();

    for (int i = 0; i < THREADS; i++)
      futures.add(executorService.submit(new ListFiller()));

    for (Future<Void> future : futures)
      future.get();

    agregator = agregator + (System.currentTimeMillis() - start);

  }

  @AfterClass
  public void validation() {
    System.out.println("Avg time: " + (agregator / INVOCATION_COUNT));
    Assert.assertEquals(INVOCATION_COUNT * THREADS * NUMBERS, list.size());
  }

  private final class ListFiller implements Callable<Void> {

    public Void call() throws Exception {
      for (int i = 0; i < NUMBERS; i++)
        list.add(i);

      return null;
    }

  }
}