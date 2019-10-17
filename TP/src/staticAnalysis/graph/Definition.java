package staticAnalysis.graph;

import miniJava.Statement;

/**
 * This class represents a Definition, i.e., a statement in which a particular variable gets a value
 * 
 * @author fmolina
 */
public class Definition {

  private Statement stmt;
  private GraphNode node; // Node in which the statement is present

  /**
   * Constructor with a given statement
   */
  public Definition(Statement stmt, GraphNode stmtNode) {
    assert stmt != null && stmtNode != null;
    this.stmt = stmt;
    this.node = stmtNode;
  }

  /**
   * Get the statement
   */
  public Statement getStmt() {
    return stmt;
  }

  /**
   * Get the graph node in which the stmt is present
   */
  public GraphNode getGraphNode() {
    return node;
  }

  @Override
  public String toString() {
    return "Def: " + stmt.toString();
  }

}
