package staticAnalysis;

import miniJava.Program;
import staticAnalysis.graph.*;
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
    cfg.getDdg().export();
    ProgramDependenceGraph pdg = cfg.getPdg();
    pdg.export();
    pdg.printAllSlices();
  }

}