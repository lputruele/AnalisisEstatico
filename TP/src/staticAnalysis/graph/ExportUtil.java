package staticAnalysis.graph;

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

  public static void dotExport(Graph<GraphNode, LabeledEdge> g, String filename) {
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

}
