package com.extended.list;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

public class ExtendedListMultiThreadMixedTest {

  private ExtendedList<A> list;
  private CountDownLatch  countDownLatch;

  private class Setter implements Runnable {
    @Override
    public void run() {
      for (int i = 0; i < 1000000; i++)
        try {
          // Check old value
          Assert.assertNotNull(list.set(i, new A(i)));
        } catch (final IndexOutOfBoundsException e) {
          // In case exception try to set one more time
          i--;
        }

      countDownLatch.countDown();
    }
  }

  private class Adder implements Runnable {
    @Override
    public void run() {
      for (int i = 0; i < 1000000; i++)
        list.add(new A(i));

      countDownLatch.countDown();
    }
  }

  private class Remover implements Runnable {
    @Override
    public void run() {
      for (int i = 0; i < 1000000; i++)
        try {
          // Check removed value. We put not-null and remove not-null
          Assert.assertNotNull(list.remove(0));
        } catch (final IndexOutOfBoundsException e) {
          // In case exception try to remove one more time
          i--;
        }
      countDownLatch.countDown();
    }
  }

  @BeforeClass
  public void setUp() {
    list = new ExtendedList<A>();
    countDownLatch = new CountDownLatch(16);
  }

  @Test
  public void test10Add3Remove3Set() throws InterruptedException {
    //
    final List<Runnable> workers = Lists.newArrayList(new Adder(), new Setter(), new Remover(), new Adder(), new Adder(),
        new Adder(), new Remover(), new Setter(), new Adder(), new Adder(), new Remover(), new Adder(), new Adder(), new Adder(),
        new Adder(), new Setter());

    for (final Runnable worker : workers)
      (new Thread(worker)).start();

    // Wait finishing a work
    countDownLatch.await();

    Assert.assertEquals(list.size(), 7000000);
    for (int i = 0; i < list.size(); i++)
      Assert.assertNotNull(list.get(i));
  }
}
