package com.extended.list;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExtendedListMultiThreadTest {

  private ExtendedList<A> list = new ExtendedList<A>();

  @BeforeClass
  public void given() {
    list = new ExtendedList<A>();
  }

  @Test(invocationCount = 8, threadPoolSize = 8)
  public void whenAdd() {
    for (int i = 0; i < 1000000; i++)
      list.add(new A(i));
  }

  @AfterClass
  public void then() {
    Assert.assertEquals(list.size(), 8000000);
    Assert.assertEquals(list.get(0).i, 0);
    Assert.assertEquals(list.get(list.size() - 1).i, 999999);
  }
}
