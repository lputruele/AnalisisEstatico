package staticAnalysis.graph;

import java.util.Set;

import org.jgrapht.graph.DirectedMultigraph;

/**
 * This class represents the Control Flow Graph
 * 
 * @author fmolina
 */
public class DataDependenceGraph {

  DirectedMultigraph<GraphNode, LabeledEdge> g;

  /**
   * Constructor of the Data Dependence Graph from a Control Flow Graph
   */
  public DataDependenceGraph(ControlFlowGraph cfg) {
    assert cfg != null;
    System.out.println("    Computing DefUse Pairs");
    Set<DUPair> dupairs = cfg.computeDefUsePairs();
    System.out.println("    DefUse Pairs: " + dupairs.size());
    // Initialize the graph as the CFG
    g = new DirectedMultigraph<GraphNode, LabeledEdge>(LabeledEdge.class);
    for (GraphNode node : cfg.g.vertexSet()) {
      g.addVertex(node);
    }
    for (LabeledEdge edge : cfg.g.edgeSet()) {
      g.addEdge(cfg.g.getEdgeSource(edge), cfg.g.getEdgeTarget(edge),
          new LabeledEdge(EdgeType.DDG));
    }
    // Add an special labeled edge from D to U for each Definition Use Pair
    for (DUPair dupair : dupairs) {
      LabeledEdge duPairLabeledEdge = new LabeledEdge(EdgeType.DDG, dupair.toString());
      g.addEdge(dupair.getDefinitionGraphNode(), dupair.getUseGraphNode(), duPairLabeledEdge);
    }
    System.out.println("    Vertexs: " + g.vertexSet().size());
    System.out.println("    Edges: " + g.edgeSet().size());
  }

  /**
   * Export as DOT
   */
  public void export() {
    ExportUtil.dotExport(g, "ddg.dot");
    System.out.println("    Exported to file output/ddg.dot");
  }
}
