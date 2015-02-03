package com.extended.list;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.extended.list.ExtendedList.WriteOperation;

public class ExtendedListUnitTest {

  @Test
  public void testGetBucket() {
    final ExtendedList<?> list = new ExtendedList<Object>();

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
    final ExtendedList<?> list = new ExtendedList<Object>();

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
    final ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(0, 5);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testIndexInMoreBucket() {
    final ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(2, 5);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testLessIndexInBucket() {
    final ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(1, 1);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testMoreIndexInBucket() {
    final ExtendedList<?> list = new ExtendedList<Object>();
    list.getIndexInBucket(1, 6);
  }

  @Test
  public void testAllocateBucket() {
    final ExtendedList<Integer> list = new ExtendedList<Integer>();

    // Create the first bucket for four elements
    list.add(0);
    list.add(1);

    Assert.assertEquals(list.get(0), Integer.valueOf(0));
    Assert.assertEquals(list.get(1), Integer.valueOf(1));

    try {
      list.get(2);
      Assert.fail();
    } catch (final IndexOutOfBoundsException e) {
      // Okay
    }

    // Create the second bucket for four elements
    list.add(2);
    Assert.assertEquals(list.get(2), Integer.valueOf(2));

    // Try create the second bucket one more time
    list.allocateBucket(1);

    // Value shouldn't be override
    Assert.assertEquals(list.get(2), Integer.valueOf(2));

    System.out.println(list);
  }

  @Test
  public void testCompleteWrite() {
    final ExtendedList<Integer> list = new ExtendedList<Integer>();

    // Create descriptor with the first bucket for two elements
    list.add(0);
    list.add(1);

    final WriteOperation<Integer> writeOperation = new WriteOperation<Integer>(1, 100);

    // Write operation should be pending by default
    Assert.assertTrue(writeOperation.pending);

    // Try to put 100 to the second (1) cell in array
    list.completeWrite(writeOperation);

    // Check added value
    Assert.assertEquals(list.get(1), Integer.valueOf(100));

    // Write operation should be finished
    Assert.assertFalse(writeOperation.pending);
  }

  @Test
  public void testAddGetSize() {
    final List<Integer> list = new ExtendedList<Integer>();

    for (int i = 0; i < 30; i++)
      list.add(i);

    Assert.assertEquals(list.size(), 30);
    Assert.assertEquals(list.get(0), Integer.valueOf(0));
    Assert.assertEquals(list.get(1), Integer.valueOf(1));
    Assert.assertEquals(list.get(2), Integer.valueOf(2));
    Assert.assertEquals(list.get(15), Integer.valueOf(15));
    Assert.assertEquals(list.get(29), Integer.valueOf(29));

    System.out.println(list);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testGetIndexOutOfBounds() {
    final List<Integer> list = new ExtendedList<Integer>();

    for (int i = 0; i < 30; i++)
      list.add(i);

    list.get(30);
  }

  @Test
  public void testAddFirstElement() {
    final List<Integer> list = new ExtendedList<Integer>();

    list.add(9);

    Assert.assertEquals(list.size(), 1);
    Assert.assertEquals(list.get(0), Integer.valueOf(9));

    System.out.println(list);
  }

  @Test
  public void testSet() {
    final List<Integer> list = new ExtendedList<Integer>();

    for (int i = 0; i < 30; i++)
      list.add(i);

    Assert.assertEquals(list.size(), 30);
    Assert.assertEquals(list.get(15), Integer.valueOf(15));

    final Integer oldValue = list.set(15, 100);

    Assert.assertEquals(list.size(), 30);
    Assert.assertEquals(oldValue, Integer.valueOf(15));
    Assert.assertEquals(list.get(15), Integer.valueOf(100));

    System.out.println(list);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testSetIndexOutOfBounds() {
    final List<Integer> list = new ExtendedList<Integer>();

    list.add(0);
    list.add(1);
    list.add(2);

    list.set(3, 3);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testSetToEmptyList() {
    final List<Integer> list = new ExtendedList<Integer>();
    list.set(0, 0);
  }
  
  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testRemoveNull() {
    final List<Integer> list = new ExtendedList<Integer>();
    list.remove(0);
  }

  @Test
  public void testRemove() {
    final List<Integer> list = new ExtendedList<Integer>();
    list.add(0);
    list.add(1);
    list.add(2);
    Assert.assertEquals(list.size(), 3);

    // Remove last element, index doesn't influence
    Assert.assertEquals(list.remove(0), Integer.valueOf(2));
    Assert.assertEquals(list.size(), 2);
    Assert.assertEquals(list.get(1), Integer.valueOf(1));
    Assert.assertEquals(list.get(0), Integer.valueOf(0));

    Assert.assertEquals(list.remove(0), Integer.valueOf(1));
    Assert.assertEquals(list.size(), 1);
    Assert.assertEquals(list.get(0), Integer.valueOf(0));

    list.add(4);
    list.add(4);
    Assert.assertEquals(list.size(), 3);

    Assert.assertEquals(list.remove(0), Integer.valueOf(4));
    Assert.assertEquals(list.size(), 2);
    Assert.assertEquals(list.get(1), Integer.valueOf(4));
    Assert.assertEquals(list.get(0), Integer.valueOf(0));

    Assert.assertEquals(list.remove(0), Integer.valueOf(4));
    Assert.assertEquals(list.size(), 1);
    Assert.assertEquals(list.get(0), Integer.valueOf(0));

    Assert.assertEquals(list.remove(0), Integer.valueOf(0));
    Assert.assertEquals(list.size(), 0);

    list.add(5);
    Assert.assertEquals(list.size(), 1);
  }
  
  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testRemoveWhenSizeZerro() {
    final List<Integer> list = new ExtendedList<Integer>();
    list.add(0);
    list.add(1);
    list.add(2);
    list.remove(0);
    list.remove(0);
    list.remove(0);
    list.remove(0);
  }
}
