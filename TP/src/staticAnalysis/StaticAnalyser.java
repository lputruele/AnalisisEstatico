package staticAnalysis;

import miniJava.Program;
import staticAnalysis.graph.ControlFlowGraph;
import staticAnalysis.graph.GraphNode;
import staticAnalysis.tree.Tree;

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
    Tree<GraphNode> t = cfg.computePostDominatorsTree();
    t.export();
    cfg.iterativeDataFlowAnalysis();
  }

  public void doSomething() {

  }

}