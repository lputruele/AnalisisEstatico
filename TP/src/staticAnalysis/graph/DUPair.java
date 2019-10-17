package staticAnalysis.graph;

import miniJava.Assign;
import miniJava.Conditional;
import miniJava.Iterative;
import miniJava.Return;
import miniJava.Statement;

/**
 * This class represents a Definition-Use pair, i.e., an ordered pair (D,U) where D is a statement
 * that contains a definition of a variable v, U is a statement that contains a use of the variable
 * v and there is a subpath in the CFG from D to U along which D is not killed.
 * 
 * @author fmolina
 *
 */
public class DUPair {

  private String var; // Variable v
  private Statement definition; // Definition statement
  private Statement use; // Use statement

  /**
   * Constructor
   * 
   * @param d
   *          is the definition statement
   * @param u
   *          is the use statement
   */
  public DUPair(Statement d, Statement u) {
    assert d != null && d instanceof Assign && u != null;
    var = ((Assign) d).getVar();
    definition = d;
    use = u;
  }

  /**
   * Get variable
   */
  public String getVar() {
    return var;
  }

  /**
   * Get the definition statement
   */
  public Statement getDefinition() {
    return definition;
  }

  /**
   * Get the use statement
   */
  public Statement getUse() {
    return use;
  }

  @Override
  public String toString() {
    return "var: " + var + " pair: (" + definition.toString() + ", " + useString() + ")";
  }

  private String useString() {
    if (use instanceof Assign) {
      return use.toString();
    } else if (use instanceof Conditional) {
      return "if " + ((Conditional) use).getCondition().toString();
    } else if (use instanceof Iterative) {
      return "while " + ((Iterative) use).getCondition().toString();
    } else if (use instanceof Return) {
      return use.toString();
    }
    return "";
  }
}
