package com.extended.list;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ExtendedListSingleThreadTest {

  @Test
  public void testGetBucket() {
    ExtendedList<?> list = new ExtendedList<Object>();

    Assert.assertEquals(0, list.getIndexOfBucket(0));
    Assert.assertEquals(0, list.getIndexOfBucket(1));

    Assert.assertEquals(1, list.getIndexOfBucket(2));
    Assert.assertEquals(1, list.getIndexOfBucket(3));
    Assert.assertEquals(1, list.getIndexOfBucket(4));
    Assert.assertEquals(1, list.getIndexOfBucket(5));

    Assert.assertEquals(2, list.getIndexOfBucket(8));

    Assert.assertEquals(3, list.getIndexOfBucket(29));
  }

  @Test
  public void testGetIndexInBucket() {
    ExtendedList<?> list = new ExtendedList<Object>();

    Assert.assertEquals(0, list.getIndexInBucket(0, 0));
    Assert.assertEquals(0, list.getIndexInBucket(0, 0));
    Assert.assertEquals(1, list.getIndexInBucket(0, 1));

    Assert.assertEquals(0, list.getIndexInBucket(1, 2));
    Assert.assertEquals(1, list.getIndexInBucket(1, 3));
    Assert.assertEquals(2, list.getIndexInBucket(1, 4));
    Assert.assertEquals(3, list.getIndexInBucket(1, 5));

    Assert.assertEquals(2, list.getIndexInBucket(2, 8));

    Assert.assertEquals(0, list.getIndexInBucket(3, 14));
    Assert.assertEquals(15, list.getIndexInBucket(3, 29));
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testIndexInLessBucket() {
    ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(0, 5);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testIndexInMoreBucket() {
    ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(2, 5);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testLessIndexInBucket() {
    ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(1, 1);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testMoreIndexInBucket() {
    ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(1, 6);
  }

  @Test
  public void testAddGetSize() {
    ExtendedList<Integer> list = new ExtendedList<Integer>();

    for (int i = 0; i < 30; i++) {
      list.add(i);
    }

    System.out.println(list);

    Assert.assertEquals(list.size(), 30);
    Assert.assertEquals(list.get(0), Integer.valueOf(0));
    Assert.assertEquals(list.get(1), Integer.valueOf(1));
    Assert.assertEquals(list.get(2), Integer.valueOf(2));
    Assert.assertEquals(list.get(15), Integer.valueOf(15));
    Assert.assertEquals(list.get(29), Integer.valueOf(29));
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testGetIndexOutOfBounds() {
    ExtendedList<Integer> list = new ExtendedList<Integer>();

    for (int i = 0; i < 30; i++) {
      list.add(i);
    }

    list.get(30);
  }
}
