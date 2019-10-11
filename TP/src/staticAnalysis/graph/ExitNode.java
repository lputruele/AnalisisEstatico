package staticAnalysis.graph;

/**
 * Exit Node
 * 
 * @author fmolina
 */
public class ExitNode extends GraphNode {

  private static ExitNode single_instance = null;

  private ExitNode() {
    id = "Exit";
  }

  public static ExitNode get() {
    if (single_instance == null)
      single_instance = new ExitNode();
    return single_instance;
  }

  @Override
  public String toString() {
    return "[" + id + "]";
  }
}
