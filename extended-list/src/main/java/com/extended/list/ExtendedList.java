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
    descriptor = new AtomicReference<Descriptor>(null);
  }

  /**
   * Calculates bucked id by formula: USED_BITS(elementIndex + 2) - 2
   * 
   * @param index
   *          an index of element
   * @return counts of bits for the index of element
   */
  protected int getIndexOfBucket(final int index) {
    // Get counts of used bits
    final int countOfUsedBits = 64 - Long.numberOfLeadingZeros(index + 2);

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
  protected void allocateBucket(final int version, final int bucket) {
    // Try to increase version of expanding array
    if (expandVersion.compareAndSet(version, version + 1))
      array[bucket] = (T[]) new Object[2 << bucket];
  }

  protected void completeWrite(final WriteOperation<T> writeOperation, final int expandVersion) {
    if (writeOperation.pending) {
      // Try to find a bucket to put element
      final int bucket = getIndexOfBucket(writeOperation.indexOfElement);

      // Add new bucket if it's needed
      while (array[bucket] == null)
        allocateBucket(expandVersion, bucket);

      final int indexInBucket = getIndexInBucket(bucket, writeOperation.indexOfElement);

      // Add element
      array[bucket][indexInBucket] = writeOperation.element;

      // Complete write
      writeOperation.pending = false;
    }
  }

  @Override
  public boolean add(final T element) {
    // Initialize the first operation
    while (descriptor.get() == null)
      descriptor.compareAndSet(null, new Descriptor(0, new WriteOperation<T>(0, element)));

    Descriptor currentDescriptor;
    Descriptor operationDescriptor;
    final int currentExpandVersion = expandVersion.get();

    do {
      currentDescriptor = descriptor.get();

      // Try to complete previous write operation
      completeWrite(currentDescriptor.writeOperation, currentExpandVersion);

      operationDescriptor = new Descriptor(currentDescriptor.size + 1, new WriteOperation<T>(currentDescriptor.size, element));

    } while (!descriptor.compareAndSet(currentDescriptor, operationDescriptor));

    // Complete current operation
    completeWrite(operationDescriptor.writeOperation, currentExpandVersion);

    return true;
  }

  @Override
  public T set(final int index, final T element) {
    boundsValidation(index);

    final int indexOfBucket = getIndexOfBucket(index);
    final int indexInBucket = getIndexInBucket(indexOfBucket, index);

    final T oldValue = array[indexOfBucket][indexInBucket];

    array[indexOfBucket][indexInBucket] = element;

    return oldValue;
  }

  @Override
  public T get(final int index) {
    boundsValidation(index);

    final int indexOfBucket = getIndexOfBucket(index);
    final int indexInBucket = getIndexInBucket(indexOfBucket, index);

    return array[indexOfBucket][indexInBucket];
  }

  private void boundsValidation(final int index) {
    if (descriptor.get() == null)
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");

    if (index >= size())
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
  }

  @Override
  public int size() {
    final Descriptor currentDescriptor = descriptor.get();

    if (currentDescriptor.writeOperation.pending)
      return currentDescriptor.size - 1;

    return currentDescriptor.size;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ROOT_SIZE; i++) {
      if (array[i] == null)
        continue;

      sb.append(Arrays.asList(array[i]));
      sb.append("\n");
    }

    if (sb.length() > 0)
      sb.deleteCharAt(sb.length() - 1);

    return sb.toString();
  }

  private class Descriptor {
    public final int               size;
    public final WriteOperation<T> writeOperation;

    public Descriptor(final int size, final WriteOperation<T> writeOperation) {
      this.size = size;
      this.writeOperation = writeOperation;
    }

    @Override
    public String toString() {
      return "[Descriptor " + hashCode() + ", size: " + size + ", writeOperation: " + writeOperation + "]";
    }
  }

  protected static class WriteOperation<T> {
    public final int indexOfElement;
    public final T   element;
    public boolean   pending;

    /**
     * Creates write operation for index and element<br>
     * This operation is pending by default
     */
    public WriteOperation(final int indexOfElement, final T element) {
      this.indexOfElement = indexOfElement;
      this.element = element;
      this.pending = true;
    }

    @Override
    public String toString() {
      return "[WriteOperation " + hashCode() + ", indexOfElement: " + indexOfElement + ", element: " + element + ", pending: "
          + pending + "]";
    }
  }

}
