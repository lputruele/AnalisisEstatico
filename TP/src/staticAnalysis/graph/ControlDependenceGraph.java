package staticAnalysis.graph;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;

import staticAnalysis.tree.Tree;

/**
 * This class represents the Control Dependence Graph
 * 
 * @author lputruele
 */
public class ControlDependenceGraph {

  DefaultDirectedGraph<GraphNode, LabeledEdge> g;
  GraphNode initial;
  private static final String TRUE_LABEL = "T";
  private static final String FALSE_LABEL = "F";

  /**
   * Constructor of the Control Dependence Graph
   */
  public ControlDependenceGraph(ControlFlowGraph acfg, Tree<GraphNode> pdt) {
    g = new DefaultDirectedGraph<GraphNode, LabeledEdge>(LabeledEdge.class);
    Set<LabeledEdge> s = new HashSet<LabeledEdge>();
    for (GraphNode n : acfg.g.vertexSet()) {
      g.addVertex(n);
    }
    for (LabeledEdge edge : acfg.g.edgeSet()) {
      if (!pdt.isAncestorOf(acfg.g.getEdgeTarget(edge), acfg.g.getEdgeSource(edge))) {
        s.add(edge);
      }
    }
    for (LabeledEdge edge : s) {
      GraphNode a = acfg.g.getEdgeSource(edge);
      GraphNode b = acfg.g.getEdgeTarget(edge);
      GraphNode l = pdt.leastCommonAncestor(a, b);
      pdt.markBackPath(b, l);
      if (a.equals(l)) {
        pdt.mark(a);
      }
      for (GraphNode n : pdt.getMarkedNodes()) {
        g.addEdge(a, n, new LabeledEdge(EdgeType.CDG, edge.getLabel()));
      }
      pdt.clearMarked();
    }
    System.out.println("    Vertexs: " + g.vertexSet().size());
    System.out.println("    Edges: " + g.edgeSet().size());
  }

  /**
   * Export the graph as dot file
   */
  public void export() {
    ExportUtil.dotExport(g, "cdg.dot");
    System.out.println("    Exported to file output/cdg.dot");
  }

}