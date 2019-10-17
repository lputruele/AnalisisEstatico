package staticAnalysis.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import miniJava.Statement;

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
  public List<Statement> getStatements() {
    return new LinkedList<Statement>();
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
