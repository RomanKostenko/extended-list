package com.extended.list;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

public class ExtendedListMultiThreadRemoveTest {

  private ExtendedList<A> list;
  private CountDownLatch  countDownLatch;

  private class Remover implements Runnable {
    @Override
    public void run() {
      for (int i = 0; i < 1000000; i++)
        // Check removed value. We put not-null and remove not-null
        Assert.assertNotNull(list.remove(0));
      countDownLatch.countDown();
    }
  }

  @BeforeClass
  public void setUp() {
    list = new ExtendedList<A>();
    for (int i = 0; i < 8000000; i++)
      list.add(new A(i));
    countDownLatch = new CountDownLatch(8);
  }

  @Test
  public void test8Remove() throws InterruptedException {
    final List<Remover> workers = Lists.newArrayList(new Remover(), new Remover(), new Remover(), new Remover(), new Remover(),
        new Remover(), new Remover(), new Remover());

    for (final Runnable worker : workers)
      (new Thread(worker)).start();

    // Wait finishing jobs
    countDownLatch.await();

    Assert.assertEquals(list.size(), 0);
  }
}
