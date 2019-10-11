package staticAnalysis.graph;

import miniJava.Statement;

/**
 * This class represents a Definition, i.e., a statement in which a particular variable gets a value
 * 
 * @author fmolina
 */
public class Definition {

  private Statement stmt;

  /**
   * Constructor with a given statement
   */
  public Definition(Statement stmt) {
    assert stmt != null;
    this.stmt = stmt;
  }

  /**
   * Get the statement
   */
  public Statement getStmt() {
    return stmt;
  }

}
