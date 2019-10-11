package staticAnalysis.graph;

import java.util.HashSet;
import java.util.Set;

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

  /**
   * Get id
   */
  public String getId() {
    return id;
  }

  /**
   * Add gen definition
   */
  public void addGen(Definition def) {
    if (gen == null)
      gen = new HashSet<Definition>();
    gen.add(def);
  }

  /**
   * Add kill definition
   */
  public void addKill(Definition def) {
    if (kill == null)
      kill = new HashSet<Definition>();
    kill.add(def);
  }

  /**
   * Get gen set
   */
  public Set<Definition> getGen() {
    return gen != null ? gen : new HashSet<Definition>();
  }

  /**
   * Get kill set
   */
  public Set<Definition> getKill() {
    return kill != null ? kill : new HashSet<Definition>();
  }

}
