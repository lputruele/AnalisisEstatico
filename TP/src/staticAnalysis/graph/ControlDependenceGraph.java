package staticAnalysis.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;

import miniJava.Conditional;
import miniJava.Iterative;
import miniJava.Program;
import miniJava.Statement;
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
    for (GraphNode n : acfg.g.vertexSet()){
    	g.addVertex(n);
    }
    for (LabeledEdge edge : acfg.g.edgeSet()){
      if(!pdt.isAncestorOf(acfg.g.getEdgeTarget(edge), acfg.g.getEdgeSource(edge)));
        s.add(edge);
    }
    for (LabeledEdge edge : s){
    	GraphNode a = acfg.g.getEdgeSource(edge);
    	GraphNode b = acfg.g.getEdgeTarget(edge);
    	GraphNode l = pdt.leastCommonAncestor(a, b);
    	pdt.markBackPath(b, l);
    	if (a == l){
    		pdt.mark(a);
    	}
    	for (GraphNode n : pdt.getMarkedNodes()){
    		g.addEdge(a,n,edge);
    	}
    }
  }

  /**
   * Export the graph as dot file
   */
  public void export() {
    try {
      VertexNameProvider<GraphNode> vertexIdProvider = new VertexNameProvider<GraphNode>() {
        public String getVertexName(GraphNode cdgn) {
          return cdgn.getId();
        }
      };
      VertexNameProvider<GraphNode> vertexLabelProvider = new VertexNameProvider<GraphNode>() {
        public String getVertexName(GraphNode cdgn) {
          return cdgn.toString();
        }
      };
      EdgeNameProvider<LabeledEdge> edgeLabelProvider = new EdgeNameProvider<LabeledEdge>() {
        public String getEdgeName(LabeledEdge edge) {
          return edge.getLabel();
        }
      };
      DOTExporter<GraphNode, LabeledEdge> exporter = new DOTExporter<GraphNode, LabeledEdge>(
          vertexIdProvider, vertexLabelProvider, edgeLabelProvider);
      exporter.export(new FileWriter("cdg.dot"), g);
    } catch (IOException e) {
      System.out.println("Unable to export graph");
    }
  }

}