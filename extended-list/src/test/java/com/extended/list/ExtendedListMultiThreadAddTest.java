package com.extended.list;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExtendedListMultiThreadAddTest {

  private ExtendedList<A> list;

  @BeforeClass
  public void givenAdd() {
    list = new ExtendedList<A>();
  }

  @Test(invocationCount = 8, threadPoolSize = 8)
  public void whenAdd() {
    for (int i = 0; i < 1000000; i++)
      list.add(new A(i));
  }

  @AfterClass
  public void thenAdd() {
    // Validate result size
    Assert.assertEquals(list.size(), 8000000);

    // Validate the first element
    Assert.assertEquals(list.get(0).i, 0);

    // Validate the last element
    Assert.assertEquals(list.get(list.size() - 1).i, 999999);

    // Validate CAS. The list mustn't contain null elements because you put not-null new A()
    for (int i = 0; i < list.size(); i++)
      Assert.assertNotNull(list.get(i));
  }

}
