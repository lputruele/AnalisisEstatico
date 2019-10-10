package staticAnalysis.tree;

import java.io.FileWriter;
import java.io.IOException;

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

  /**
   * Constructor
   */
  public Tree() {
    root = null;
    t = new DefaultDirectedGraph<Node<E>, DefaultEdge>(DefaultEdge.class);
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
    t.addEdge(node1, node2);
  }

  /**
   * Get the size
   */
  public int size() {
    return size;
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
      exporter.export(new FileWriter("tree.dot"), t);
    } catch (IOException e) {
      System.out.println("Unable to export tree");
    }
  }

  private class Node<T> {
    String id;
    private T value;

    public Node(T value) {
      this.value = value;
    }

    public void setId(int id) {
      this.id = String.valueOf(id);
    }

    /**
     * Get id
     */
    public String getId() {
      return id;
    }

    @Override
    public String toString() {
      return value.toString();
    }
  }

}
