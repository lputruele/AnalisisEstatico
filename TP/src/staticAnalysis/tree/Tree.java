package staticAnalysis.tree;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Generic class that implements a Tree
 * 
 * @author fmolina
 */
public class Tree<E> {

  private Node<E> root;
  private DefaultDirectedGraph<Node<E>, DefaultEdge> t;
  private int size = 0;
  private List<E> markedNodes;

  /**
   * Constructor
   */
  public Tree() {
    root = null;
    t = new DefaultDirectedGraph<Node<E>, DefaultEdge>(DefaultEdge.class);
    markedNodes = new LinkedList<E>();
  }

  /**
   * Constructor with root
   */
  public Tree(E root) {
    this.root = new Node<E>(root);
    this.root.setId(size);
    size++;
    t = new DefaultDirectedGraph<Node<E>, DefaultEdge>(DefaultEdge.class);
    t.addVertex(this.root);
    markedNodes = new LinkedList<E>();
  }

  public boolean contains(E value) {
    for (Node<E> node : t.vertexSet()) {
      if (node.value.equals(value))
        return true;
    }
    return false;
  }

  /**
   * Get the node with the given value
   */
  private Node<E> getNode(E value) {
    for (Node<E> node : t.vertexSet()) {
      if (node.value.equals(value))
        return node;
    }
    return null;
  }

  /**
   * Adds the node with value v2 as child of the node with value v1
   */
  public void addChild(E v1, E v2) {
    Node<E> node1 = getNode(v1);
    Node<E> node2 = getNode(v2);
    if (node1 == null) {
      node1 = new Node<E>(v1);
      node1.setId(size);
      t.addVertex(node1);
      size++;
    }
    if (node2 == null) {
      node2 = new Node<E>(v2);
      node2.setId(size);
      t.addVertex(node2);
      size++;
    }
    node2.setParent(node1);
    t.addEdge(node1, node2);
  }

  /**
   * Get the size
   */
  public int size() {
    return size;
  }

  /**
   * Returns true is s is an ancestor of t
   */
  public boolean isAncestorOf(E s, E t) {
    Node<E> curr = getNode(t);
    while (curr != null) {
      if (curr.value.equals(s))
        return true;
      curr = curr.getParent();
    }
    return false;
  }

  /**
   * Finds least common ancestor of s and t
   */
  public E leastCommonAncestor(E s, E t) {
    Node<E> curr = getNode(t);
    while (curr != null) {
      if (isAncestorOf(curr.getValue(), s))
        return curr.getValue();
      curr = curr.getParent();
    }
    return null;
  }

  /**
   * Marks a node s as visited
   */
  public void mark(E s) {
    if (getNode(s) != null) {
      // getNode(s).setVisited(true);
      markedNodes.add(s);
    }
  }

  /**
   * Get all marked nodes
   */
  public List<E> getMarkedNodes() {
    return markedNodes;
  }

  public void clearMarked() {
    markedNodes.clear();
  }

  /**
   * Marks back path from b to l
   */
  public void markBackPath(E b, E l) {
    Node<E> curr = getNode(b);
    while (curr != null && !curr.value.equals(l)) {
      mark(curr.getValue());
      curr = curr.getParent();
    }
  }

  /**
   * Export to a dot file
   */
  public void export() {
    try {
      VertexNameProvider<Node<E>> vertexIdProvider = new VertexNameProvider<Node<E>>() {
        public String getVertexName(Node<E> n) {
          return n.getId();
        }
      };
      VertexNameProvider<Node<E>> vertexLabelProvider = new VertexNameProvider<Node<E>>() {
        public String getVertexName(Node<E> n) {
          return n.toString();
        }
      };
      DOTExporter<Node<E>, DefaultEdge> exporter = new DOTExporter<Node<E>, DefaultEdge>(
          vertexIdProvider, vertexLabelProvider, null);
      exporter.export(new FileWriter("output/tree.dot"), t);
      System.out.println("    Exported to file output/tree.dot");
    } catch (IOException e) {
      System.out.println("Unable to export tree");
    }
  }

  private class Node<T> {
    String id;
    private T value;
    private Node<T> parent;
    private boolean visited;

    public Node(T value) {
      this.value = value;
    }

    public void setId(int id) {
      this.id = String.valueOf(id);
    }

    public void setParent(Node<T> p) {
      this.parent = p;
    }

    public void setVisited(boolean b) {
      this.visited = b;
    }

    /**
     * Get id
     */
    public String getId() {
      return id;
    }

    /**
     * Get parent in tree
     */
    public Node<T> getParent() {
      return parent;
    }

    /**
     * Get value
     */
    public T getValue() {
      return value;
    }

    /**
     * Get visited
     */
    public boolean getVisited() {
      return visited;
    }

    @Override
    public String toString() {
      return value.toString();
    }
  }

}
