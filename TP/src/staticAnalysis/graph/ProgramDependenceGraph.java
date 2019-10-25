package staticAnalysis.graph;

import java.util.LinkedList;

import org.jgrapht.graph.DirectedMultigraph;

/**
 * This class represents the Program Dependence Graph
 * 
 * @author lputruele
 */
public class ProgramDependenceGraph {

  DirectedMultigraph<GraphNode, LabeledEdge> g;

  /**
   * Constructor of the Program Dependence Graph from a Cpntrol Dependence Graph and a Data
   * Dependence Graph
   */
  public ProgramDependenceGraph(DataDependenceGraph ddg, ControlDependenceGraph cdg) {
    assert (ddg != null && cdg != null);
    g = new DirectedMultigraph<GraphNode, LabeledEdge>(LabeledEdge.class);
    for (GraphNode node : cdg.g.vertexSet()) {
      g.addVertex(node);
    }
    for (LabeledEdge edge : cdg.g.edgeSet()) {
      g.addEdge(cdg.g.getEdgeSource(edge), cdg.g.getEdgeTarget(edge), edge);
    }
    for (LabeledEdge edge : ddg.g.edgeSet()) {
      g.addEdge(ddg.g.getEdgeSource(edge), ddg.g.getEdgeTarget(edge), edge);
    }
    System.out.println("    Vertexs: " + g.vertexSet().size());
    System.out.println("    Edges: " + g.edgeSet().size());
  }

  /**
   * Computes slice of this Program Dependence Graph, i.e. set of backward reachable nodes from a
   * node s
   */
  public LinkedList<GraphNode> getBackwardStaticSlice(GraphNode p) {
    LinkedList<GraphNode> worklist = new LinkedList<GraphNode>();
    LinkedList<GraphNode> slice = new LinkedList<GraphNode>();
    //LinkedList<GraphNode> visited = new LinkedList<GraphNode>();
    worklist.addFirst(p);
    while (!worklist.isEmpty()) {
      GraphNode curr = worklist.removeFirst();
      //visited.add(curr);
      for (LabeledEdge edge : g.edgeSet()) {
        GraphNode source = g.getEdgeSource(edge);
        GraphNode target = g.getEdgeTarget(edge);
        if (target.equals(curr) && !slice.contains(source)) {
          worklist.addFirst(source);
          slice.addFirst(source);
        }
      }
    }
    return slice;
  }

  /**
   * Test for slices
   */
  public void printAllSlices() {
    for (GraphNode n : g.vertexSet()) {
      System.out.println("\n  Slice for " + n + ":");
      for (GraphNode n_ : getBackwardStaticSlice(n)) {
        System.out.println("    " + n_);
      }
    }
  }

  /**
   * Export as DOT
   */
  public void export() {
    ExportUtil.dotExport(g, "pdg.dot");
    System.out.println("    Exported to file output/pdg.dot");
  }
}
