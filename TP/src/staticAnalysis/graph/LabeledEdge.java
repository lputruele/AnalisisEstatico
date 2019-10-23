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
  EdgeType type;

  /**
   * Default constructor
   */
  public LabeledEdge(EdgeType et) {
    label = "";
    type = et;
  }

  /**
   * Constructor with label
   */
  public LabeledEdge(EdgeType et, String l) {
    label = l;
    type = et;
  }

  /**
   * Gets the label associated with this edge.
   *
   * @return edge label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Gets the type associated with this edge.
   *
   * @return edge type
   */
  public EdgeType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "(" + getSource() + " : " + getTarget() + " : " + label + ")";
  }

}
