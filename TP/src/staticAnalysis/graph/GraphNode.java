package staticAnalysis.graph;

import java.util.List;
import java.util.Set;

import miniJava.Statement;

/**
 * This class represents a Control Flow Graph node
 * 
 * @author fmolina
 */
public abstract class GraphNode {

  protected String id;
  protected Set<Definition> gen; // set of definitions in the node that reach the point immediately
                                 // after the node
  protected Set<Definition> kill; // set of definitions in the program that are killed if they reach
                                  // the entry to the node
  protected Set<Definition> in; // set of definitions in the program that reach the point
                                // immediately before the node
  protected Set<Definition> out; // set of definitions in the program that reach the point
                                 // immediately following the node

  /**
   * Get id
   */
  public String getId() {
    return id;
  }

  /**
   * Compute the gen definitions of the node
   */
  public abstract void computeGen();

  /**
   * Compute the kill definitions of the node
   */
  public abstract void computeKill(List<Statement> progStmt);

  /**
   * Get gen set
   */
  public Set<Definition> getGen() {
    return gen;
  }

  /**
   * Get kill set
   */
  public Set<Definition> getKill() {
    return kill;
  }

  /**
   * Get in set
   */
  public Set<Definition> getIn() {
    return in;
  }

  /**
   * Set in set
   */
  public void setIn(Set<Definition> set) {
    in = set;
  }

  /**
   * Get out set
   */
  public Set<Definition> getOut() {
    return out;
  }

  /**
   * Set out set
   */
  public void setOut(Set<Definition> set) {
    out = set;
  }

  /**
   * Get definition set strings
   */
  public String getDefSetsStrings() {
    String str = "";
    if (gen != null) {
      str += "gen: " + getSetString(gen);
    }
    if (kill != null) {
      str += "kill: " + getSetString(kill);
    }
    if (in != null) {
      str += "in: " + getSetString(in);
    }
    if (out != null) {
      str += "out: " + getSetString(out);
    }
    return str;
  }

  private String getSetString(Set<Definition> set) {
    String str = "{";
    for (Definition def : set) {
      str += "\n  " + def.toString();
    }
    return str + "\n}\n";
  }
}
