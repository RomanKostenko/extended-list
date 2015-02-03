package com.extended.list;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

public class ExtendedListMultiThreadAddRemoveTest {

  private ExtendedList<A> list;
  private CountDownLatch  countDownLatch;

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
    countDownLatch = new CountDownLatch(8);
  }

  @Test
  public void test6Add2Remove() throws InterruptedException {
    final List<Runnable> workers = Lists.newArrayList(new Adder(), new Remover(), new Adder(), new Adder(), new Adder(),
        new Remover(), new Adder(), new Adder());

    for (final Runnable worker : workers)
      (new Thread(worker)).start();

    // Wait finishing a work
    countDownLatch.await();

    Assert.assertEquals(list.size(), 4000000);
    for (int i = 0; i < list.size(); i++)
      Assert.assertNotNull(list.get(i));
  }
}
