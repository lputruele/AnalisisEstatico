package staticAnalysis.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

/**
 * Export util class
 * 
 * @author fmolina
 *
 */

public class ExportUtil {

  public static void dotExport2(Graph<GraphNode, LabeledEdge> g, String filename) {
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
      exporter.export(new FileWriter(filename), g);
    } catch (IOException e) {
      System.out.println("Unable to export graph");
    }
  }

  public static String dotExport(Graph<GraphNode, LabeledEdge> g, String filename) {
    String res = "digraph model {\n\n";
    for (GraphNode vertex : g.vertexSet()) {
      res += "STATE" + vertex.getId() + " [label=\"" + vertex.toString() + "\"]" + "\n";
    }
    for (LabeledEdge edge : g.edgeSet()) {
      switch (edge.getType()) {
      case CFG:
        res += "STATE" + g.getEdgeSource(edge).getId() + " -> STATE" + g.getEdgeTarget(edge).getId()
            + " [color=\"red\",label = \"" + edge.getLabel() + "\"]" + ";\n";
        break;
      case DDG:
        res += "STATE" + g.getEdgeSource(edge).getId() + " -> STATE" + g.getEdgeTarget(edge).getId()
            + " [color=\"green\",label = \"" + edge.getLabel() + "\"]" + ";\n";
        break;
      default:
        res += "STATE" + g.getEdgeSource(edge).getId() + " -> STATE" + g.getEdgeTarget(edge).getId()
            + " [color=\"blue\",label = \"" + edge.getLabel() + "\"]" + ";\n";
        break;
      }
    }
    res += "\n}";
    try {
      String path = "output/" + filename;
      File file = new File(path);
      file.createNewFile();
      FileWriter fw = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(res);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return res;
  }

}
