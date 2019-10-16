package staticAnalysis.graph;

import java.util.HashSet;
import java.util.List;

import miniJava.Statement;

/**
 * Start Node
 * 
 * @author lputruele
 */
public class StartNode extends GraphNode {

  private static StartNode single_instance = null;

  private StartNode() {
    id = "Start";
  }

  public static StartNode get() {
    if (single_instance == null)
      single_instance = new StartNode();
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
