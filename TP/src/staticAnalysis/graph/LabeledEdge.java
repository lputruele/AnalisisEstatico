package staticAnalysis.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * This class represents a labeled edge
 * 
 * @author fmolina
 *
 */
public class LabeledEdge extends DefaultEdge {

  String label;

  /**
   * Default constructor
   */
  public LabeledEdge() {
    label = "";
  }

  /**
   * Constructor with label
   */
  public LabeledEdge(String l) {
    label = l;
  }

  /**
   * Gets the label associated with this edge.
   *
   * @return edge label
   */
  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return "(" + getSource() + " : " + getTarget() + " : " + label + ")";
  }

}
