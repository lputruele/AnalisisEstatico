package staticAnalysis.graph;

/**
 * Entry Node
 * 
 * @author fmolina
 */
public class EntryNode extends GraphNode {

  private static EntryNode single_instance = null;

  private EntryNode() {
    id = "Entry";
  }

  public static EntryNode get() {
    if (single_instance == null)
      single_instance = new EntryNode();
    return single_instance;
  }

  @Override
  public String toString() {
    return "[" + id + "]";
  }

}
