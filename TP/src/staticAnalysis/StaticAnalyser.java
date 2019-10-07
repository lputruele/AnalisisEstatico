package staticAnalysis;

import miniJava.Program;
import staticAnalysis.graph.ControlFlowGraph;

/**
 * This class represents the compiler.
 */
public class StaticAnalyser {

  private ControlFlowGraph cfg;

  public StaticAnalyser(Program p) {
    if (p == null)
      throw new IllegalArgumentException("The program can't be null");
    cfg = new ControlFlowGraph(p);
    cfg.export();
  }

  public void doSomething() {

  }

}