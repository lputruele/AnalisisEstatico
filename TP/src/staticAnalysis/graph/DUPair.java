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
  private GraphNode defGraphNode; // Graph node in which the definition is present
  private Statement use; // Use statement
  private GraphNode useGraphNode; // Graph node in which the use is present

  /**
   * Constructor
   */
  public DUPair(Statement d, GraphNode dNode, Statement u, GraphNode uNode) {
    assert d != null && d instanceof Assign && u != null && dNode != null && uNode != null;
    var = ((Assign) d).getVar();
    definition = d;
    defGraphNode = dNode;
    use = u;
    useGraphNode = uNode;
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
   * Get definition graph node
   */
  public GraphNode getDefinitionGraphNode() {
    return defGraphNode;
  }

  /**
   * Get the use statement
   */
  public Statement getUse() {
    return use;
  }

  /**
   * Get use graph node
   */
  public GraphNode getUseGraphNode() {
    return useGraphNode;
  }

  @Override
  public String toString() {
    return "[var: " + var + " pair: (" + definition.toString() + ", " + useString() + ")]";
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
