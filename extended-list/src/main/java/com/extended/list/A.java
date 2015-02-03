package com.extended.list;

public class A {
  public final int i;

  public A(final int i) {
    this.i = i;
  }

  public int getI() {
    return i;
  }

  @Override
  public String toString() {
    return String.valueOf(i);
  }
}
