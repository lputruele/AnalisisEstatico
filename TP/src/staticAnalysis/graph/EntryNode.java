package staticAnalysis.graph;

import java.util.HashSet;
import java.util.List;

import miniJava.Statement;

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

  @Override
  public void computeGen() {
    gen = new HashSet<Definition>();
  }

  @Override
  public void computeKill(List<Statement> progStmt) {
    kill = new HashSet<Definition>();
  }
}
