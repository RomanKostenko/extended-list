package com.extended.list;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ExtendedList<T> extends AbstractList<T> implements List<T> {

  /**
   * The size of root array
   */
  private static final int                  ROOT_SIZE = 64;

  /**
   * It serves for indicating the first undefined operation<br>
   * It's used instead of <code>null</code>. In other case you can't put null in to array
   */
  @SuppressWarnings("unchecked")
  private final T                           UNDEFINED = (T) new Object();

  /**
   * The data array.
   */
  private final T[][]                       array;

  /**
   * The counter uses for CAS expanding array
   */
  private final AtomicInteger               expandVersion;

  /**
   * Represents current pointer of array
   */
  private final AtomicReference<Descriptor> descriptor;

  @SuppressWarnings("unchecked")
  public ExtendedList() {
    array = (T[][]) new Object[ROOT_SIZE][];
    expandVersion = new AtomicInteger();
    descriptor = new AtomicReference<Descriptor>(new Descriptor(0, UNDEFINED));
  }

  /**
   * Calculates bucked id by formula: USED_BITS(elementIndex + 2) - 2
   * 
   * @param index
   *          an index of element
   * @return counts of bits for the index of element
   */
  protected int getIndexOfBucket(int index) {
    index = index + 2;

    // Get counts of used bits
    int countOfUsedBits = 64 - Long.numberOfLeadingZeros(index);

    // Get index of bucket
    return countOfUsedBits - 2;
  }

  /**
   * Calculates index in bucket for element
   * 
   * @param indexOfBucket
   *          id of bucket
   * @param indexOfElement
   *          index of element
   * @return index in bucket for element
   */
  protected int getIndexInBucket(final int indexOfBucket, final int indexOfElement) {
    // The index of first element in bucket
    final int indexOfFirstElement = (2 << indexOfBucket) - 2;

    // Element should be in right bucket
    assert indexOfElement >= indexOfFirstElement;

    // Max index for element in bucket: [(2 ^ NEXT_BUCKET) - 3]
    final int maxIndexOfBucket = (2 << (indexOfBucket + 1)) - 3;

    // Element should be in right bucket
    assert indexOfElement <= maxIndexOfBucket;

    // Element index in the bucket
    return indexOfElement - indexOfFirstElement;
  }

  @SuppressWarnings("unchecked")
  protected void expandArray(int version, int bucket) {
    // Try to increase version of expanding array
    if (expandVersion.compareAndSet(version, version + 1))
      array[bucket] = (T[]) new Object[2 << bucket];
  }

  protected void completeWrite(int indexOfElement, int expandVersion, Descriptor descriptor) {
    // Try to find a bucket to put element
    final int bucket = getIndexOfBucket(indexOfElement);
    
    // TODO if desc.pending == false == return

    // Add new bucket if it's needed
    // TODO can be improved use park
    while (array[bucket] == null)
      expandArray(expandVersion, bucket);

    final int indexInBucket = getIndexInBucket(bucket, indexOfElement);

    // Add element
    array[bucket][indexInBucket] = descriptor.element.get();

    // Complete write
    descriptor.pending = false;
  }

  @Override
  public boolean add(T element) {
    Descriptor currentDescriptor;
    Descriptor nextDescriptor;
    int indexOfElement;
    int version;

    do {
      // Get current version of expanding array TODO
      version = expandVersion.get();

      currentDescriptor = descriptor.get();

      // Set up the first element TODO optimize it
      if (currentDescriptor.element == UNDEFINED)
        currentDescriptor.element.compareAndSet(UNDEFINED, element);

      final int currentSize = currentDescriptor.size;

      // Index for element
      indexOfElement = currentSize;

      // Try to complete current write operation
      if (currentDescriptor.pending)
        completeWrite(indexOfElement - 1, version, currentDescriptor);

      nextDescriptor = new Descriptor(currentSize + 1, element, true);
    } while (!descriptor.compareAndSet(currentDescriptor, nextDescriptor));

    completeWrite(indexOfElement, version, nextDescriptor);

    return true;
  }

  // @Override
  // public void add(int indexOfElement, T element) {
  // Descriptor currentDescriptor;
  // Descriptor nextDescriptor;
  // int version;
  //
  // do {
  // // Get current version of expanding array TODO
  // version = expandVersion.get();
  //
  // currentDescriptor = descriptor.get();
  //
  // // Set up the first element TODO optimize it
  // if (currentDescriptor.element == UNDEFINED)
  // currentDescriptor.element.compareAndSet(UNDEFINED, element);
  //
  // final int currentSize = currentDescriptor.size;
  //
  // // // Try to complete current write operation
  // // if (currentDescriptor.pending)
  // // completeWrite(indexOfElement - 1, version, currentDescriptor);
  //
  // nextDescriptor = new Descriptor(currentSize + 1, element, true);
  // } while (!descriptor.compareAndSet(currentDescriptor, nextDescriptor));
  //
  // completeWrite(indexOfElement, version, nextDescriptor);
  // }

  @Override
  public T get(int index) {
    // Try to find a bucket to get element
    int bucket = getIndexOfBucket(index);
    int indexInBucket = getIndexInBucket(bucket, index);

    if (array[bucket] == null) {
      throw new IndexOutOfBoundsException();
    }

    return array[bucket][indexInBucket];
  }

  @Override
  public int size() {
    final Descriptor currentDescriptor = descriptor.get();

    if (currentDescriptor.pending)
      return currentDescriptor.size - 1;

    return currentDescriptor.size;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ROOT_SIZE; i++) {
      if (array[i] == null)
        break;

      sb.append(Arrays.asList(array[i]));
      sb.append("\n");
    }

    if (sb.length() > 0)
      sb.deleteCharAt(sb.length() - 1);

    return sb.toString();
  }

  private class Descriptor {
    public final int          size;
    public AtomicReference<T> element;
    public boolean            pending;

    public Descriptor(int size, T element) {
      this(size, element, false);
    }

    public Descriptor(int size, T element, boolean pending) {
      this.size = size;
      this.element = new AtomicReference<T>(element);
      this.pending = pending;
    }

    @Override
    public String toString() {
      return "[Descriptor " + hashCode() + ", size: " + size + ", pending: " + pending + ", element: " + element + "]";
    }
  }

  private class WriteOperation {
    public final int indexOfElement;
    public T         element;

    public WriteOperation(int indexOfElement) {
      this.indexOfElement = indexOfElement;
    }

  }

}
