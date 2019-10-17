package staticAnalysis;

import java.util.Set;

import miniJava.Program;
import staticAnalysis.graph.ControlFlowGraph;
import staticAnalysis.graph.DUPair;
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
    cfg.getCdg().export();
    cfg.iterativeDataFlowAnalysis();
    Set<DUPair> dupairs = cfg.computeDefUsePairs();
    printDUPairs(dupairs);
  }

  private void printDUPairs(Set<DUPair> dupairs) {
    System.out.println("DUPairs");
    for (DUPair du : dupairs) {
      System.out.println(du.toString());
    }
  }

}