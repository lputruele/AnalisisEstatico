package staticAnalysis;

import miniJava.Program;
import staticAnalysis.graph.ControlDependenceGraph;
import staticAnalysis.graph.ControlFlowGraph;
import staticAnalysis.graph.DataDependenceGraph;
import staticAnalysis.graph.GraphNode;
import staticAnalysis.graph.ProgramDependenceGraph;
import staticAnalysis.tree.Tree;

/**
 * This class represents the compiler.
 */
public class StaticAnalyser {

  private ControlFlowGraph cfg;

  public StaticAnalyser(Program p) {
    if (p == null)
      throw new IllegalArgumentException("The program can't be null");
    System.out.println();
    System.out.println("Starting static analysis..");
    System.out.println("* Control Flow Graph");
    cfg = new ControlFlowGraph(p);
    cfg.export();
    System.out.println();
    System.out.println("* PostDominators Tree");
    Tree<GraphNode> t = cfg.computePostDominatorsTree();
    System.out.println("    Size: " + t.size());
    t.export();
    System.out.println();
    System.out.println("* Control Dependence Graph");
    ControlDependenceGraph cdg = cfg.getCdg();
    cdg.export();
    System.out.println();
    System.out.println("* Data Dependence Graph");
    cfg.iterativeDataFlowAnalysis();
    DataDependenceGraph ddg = cfg.getDdg();
    ddg.export();
    System.out.println();
    System.out.println("* Program Dependence Graph");
    ProgramDependenceGraph pdg = cfg.getPdg(ddg, cdg);
    pdg.export();
    System.out.println();
    System.out.println("* Static Backward Slicing");
    pdg.printAllSlices();
    System.out.println();
    System.out.println("Done!");
  }

}