package com.extended.list;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class RunMicrobenchmarks {

  public static void main(final String[] args) throws RunnerException {
    final Options opt = new OptionsBuilder().include(SingleThreadMicrobenchmark.class.getSimpleName()).warmupIterations(3)
        .measurementIterations(3).forks(1).build();

    new Runner(opt).run();

    // final Options optMulti = new OptionsBuilder().include(MultiThreadMicrobenchmark.class.getSimpleName()).warmupIterations(3)
    // .measurementIterations(3).threads(8).forks(1).build();
    //
    // new Runner(optMulti).run();
  }

}
