package staticAnalysis.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;

import miniJava.Assign;
import miniJava.Conditional;
import miniJava.Iterative;
import miniJava.Program;
import miniJava.Return;
import miniJava.Statement;
import miniJava.Var;
import staticAnalysis.tree.Tree;

/**
 * This class represents the Control Flow Graph
 * 
 * @author fmolina
 */
public class ControlFlowGraph {

  Program p;
  DefaultDirectedGraph<GraphNode, LabeledEdge> g;
  GraphNode initial;
  private static final String TRUE_LABEL = "T";
  private static final String FALSE_LABEL = "F";

  /**
   * Constructor of the Control Flow Graph
   */
  public ControlFlowGraph() {
    g = new DefaultDirectedGraph<GraphNode, LabeledEdge>(LabeledEdge.class);
  }

  /**
   * Constructor of the Control Flow Graph from a program p
   */
  public ControlFlowGraph(Program p) {
    this.p = p;
    g = new DefaultDirectedGraph<GraphNode, LabeledEdge>(LabeledEdge.class);
    List<BasicBlock> basicBlocks = getBasicBlocks(p);
    System.out.println("    Basic blocks: " + basicBlocks.size());
    buildGraph(basicBlocks, p);

  }

  /**
   * Get the basic blocks from a given program
   */
  private List<BasicBlock> getBasicBlocks(Program p) {
    List<Statement> progStmts = p.getStatements();
    // markLeaders(progStmts);
    // Construct the basic blocks considering the leaders
    // List<BasicBlock> basicBlocks = computeBasicBlocks(null, progStmts);
    List<BasicBlock> basicBlocks = computeBasicBlocksAllAreLeaders(null, progStmts);
    return basicBlocks;
  }

  /**
   * Mark the leaders
   */
  private void markLeaders(List<Statement> progStmts) {
    if (progStmts.size() > 0) {
      // The first statement is a leader
      progStmts.get(0).setIsLeader(true);
      boolean nextIsLeader = false;
      for (int i = 1; i < progStmts.size(); i++) {
        Statement s = progStmts.get(i);
        // Determine if s is a leader
        if (s instanceof Conditional) {
          Conditional c = (Conditional) s;
          markLeaders(c.getThenBlock());
          markLeaders(c.getElseBlock());
          nextIsLeader = true;
        } else if (s instanceof Iterative) {
          markLeaders(((Iterative) s).getDoBlock());
          nextIsLeader = true;
        } else if (nextIsLeader) {
          nextIsLeader = false;
          s.setIsLeader(true);
        }
      }
    }
  }

  /**
   * Compute the list of basic blocks from the consider the leader program statements
   */
  private List<BasicBlock> computeBasicBlocks(BasicBlock block, List<Statement> progStmts) {
    List<BasicBlock> basicBlocks = new ArrayList<BasicBlock>();
    boolean addCurr = false;
    int j = 0;
    BasicBlock currBasicBlock = block;
    while (j < progStmts.size()) {
      Statement stmt = progStmts.get(j);
      if (stmt.isLeader()) {
        if (currBasicBlock != null) {
          basicBlocks.add(currBasicBlock);
        }
        currBasicBlock = new BasicBlock();
        currBasicBlock.addStatement(stmt);
        addCurr = true;
      } else {
        if (!stmt.isControlTransferStatement()) {
          currBasicBlock.addStatement(stmt);
          addCurr = true;
        } else {
          // Is a control transfer statement
          basicBlocks.add(currBasicBlock);
          addCurr = false;
          if (stmt instanceof Conditional) {
            Conditional c = (Conditional) stmt;
            basicBlocks.addAll(computeBasicBlocks(null, c.getThenBlock()));
            basicBlocks.addAll(computeBasicBlocks(null, c.getElseBlock()));
          } else if (stmt instanceof Iterative) {
            Iterative i = (Iterative) stmt;
            basicBlocks.addAll(computeBasicBlocks(null, i.getDoBlock()));
          }
          currBasicBlock = null;
        }
      }
      j++;
    }
    if (addCurr)
      basicBlocks.add(currBasicBlock);
    return basicBlocks;
  }

  /**
   * Compute the list of basic blocks from the consider the leader program statements
   */
  private List<BasicBlock> computeBasicBlocksAllAreLeaders(BasicBlock block,
      List<Statement> progStmts) {
    List<BasicBlock> basicBlocks = new ArrayList<BasicBlock>();
    int j = 0;
    BasicBlock currBasicBlock = block;
    while (j < progStmts.size()) {
      Statement stmt = progStmts.get(j);
      currBasicBlock = new BasicBlock();
      currBasicBlock.addStatement(stmt);
      basicBlocks.add(currBasicBlock);
      if (stmt instanceof Conditional) {
        Conditional c = (Conditional) stmt;
        basicBlocks.addAll(computeBasicBlocksAllAreLeaders(null, c.getThenBlock()));
        basicBlocks.addAll(computeBasicBlocksAllAreLeaders(null, c.getElseBlock()));
      } else if (stmt instanceof Iterative) {
        Iterative i = (Iterative) stmt;
        basicBlocks.addAll(computeBasicBlocksAllAreLeaders(null, i.getDoBlock()));
      }
      j++;
    }
    return basicBlocks;
  }

  /**
   * Build the Control Flow Graph from the program basic blocks
   */
  private void buildGraph(List<BasicBlock> basicBlocks, Program p) {
    initial = EntryNode.get();
    g.addVertex(EntryNode.get());
    g.addVertex(ExitNode.get());
    // Add node Entry -> B1
    GraphNode entryBlock = basicBlocks.get(0);
    g.addVertex(entryBlock);
    g.addEdge(EntryNode.get(), entryBlock, new LabeledEdge(EdgeType.CFG));
    // Add nodes Bi -> Bj if and only if Bj can immediately follow Bi in some execution
    for (int i = 0; i < basicBlocks.size(); i++) {
      BasicBlock blockI = basicBlocks.get(i);
      blockI.setId(String.valueOf(i));
      g.addVertex(blockI);
      boolean atLeastOneFollower = false;
      Statement lastStmtBlockI = blockI.getStatements().get(blockI.getStatements().size() - 1);
      for (int j = 0; j < basicBlocks.size(); j++) {
        if (i == j)
          continue;
        BasicBlock blockJ = basicBlocks.get(j);
        blockJ.setId(String.valueOf(j));
        g.addVertex(blockJ);
        Statement fstStmtBlockJ = blockJ.getStatements().get(0);
        if (followsImmediately(lastStmtBlockI, fstStmtBlockJ, p)) {
          atLeastOneFollower = true;
          LabeledEdge edge = new LabeledEdge(EdgeType.CFG);
          if (lastStmtBlockI.isControlTransferStatement())
            edge = new LabeledEdge(EdgeType.CFG, getLabel(lastStmtBlockI, fstStmtBlockJ));
          g.addEdge(blockI, blockJ, edge);
        }
      }
      if (!atLeastOneFollower) {
        // BlockI must go to the exit, i.e., Bi -> Exit
        g.addEdge(blockI, ExitNode.get(), new LabeledEdge(EdgeType.CFG));
      }
    }
    System.out.println("    Vertexs: " + g.vertexSet().size());
    System.out.println("    Edges: " + g.edgeSet().size());
  }

  /**
   * Determine the label to be used in the graph
   */
  private String getLabel(Statement controlStmt, Statement stmt) {
    if (controlStmt instanceof Conditional) {
      Conditional c = (Conditional) controlStmt;
      return c.getThenBlock().contains(stmt) ? TRUE_LABEL : FALSE_LABEL;
    } else if (controlStmt instanceof Iterative) {
      Iterative it = (Iterative) controlStmt;
      return it.getDoBlock().contains(stmt) ? TRUE_LABEL : FALSE_LABEL;
    }
    return null;
  }

  /**
   * Returns true iff stmt2 follows immediately stmt1 in some path
   */
  private boolean followsImmediately(Statement stmt1, Statement stmt2, Program p) {
    if (stmt1 instanceof Conditional) {
      Conditional c = (Conditional) stmt1;
      Statement fstThen = c.getThenBlock().size() > 0 ? c.getThenBlock().get(0) : null;
      if (stmt2.equals(fstThen)) {
        // stmt2 follows stmt1 through the then branch of a conditional statement
        return true;
      }
      Statement fstElse = c.getElseBlock().size() > 0 ? c.getElseBlock().get(0) : null;
      if (stmt2.equals(fstElse)) {
        // stmt2 follows stmt1 through the else branch of a conditional statement
        return true;
      }
    } else if (stmt1 instanceof Iterative) {
      Iterative it = (Iterative) stmt1;
      Statement fstDoBlock = it.getDoBlock().size() > 0 ? it.getDoBlock().get(0) : null;
      if (stmt2.equals(fstDoBlock)) {
        // stmt2 follows stmt1 through the do branch of an iterative statement
        return true;
      }
      // Maybe the next statement of the iterative one, is the stmt2 (thus, it follows stmt1 when
      // the iterative condition is false)
      Statement nextIt = getNext(it, p.getStatements());
      if (stmt2.equals(nextIt))
        return true;
    } else {
      Statement next = getNext(stmt1, p.getStatements());
      if (stmt2.equals(next))
        return true;
    }
    if (stmt2 instanceof Iterative) {
      // In case that stmt1 is the one of the last sentences of the iterative stmt2, stmt2 can
      // follow stmt1
      List<Statement> lastStatements = ((Iterative) stmt2).getLastStatements();
      for (Statement last : lastStatements) {
        if (stmt1.equals(last))
          return true;
      }
    }
    return false;
  }

  /**
   * Given a statement and a list of statements of a program, return the next statement if exists
   */
  private Statement getNext(Statement stmt, List<Statement> stmts) {
    int i = 0;
    while (i < stmts.size()) {
      if (stmts.get(i).equals(stmt)) {
        // We are in stmt
        if (i + 1 < stmts.size()) {
          // Get the next statement
          return stmts.get(i + 1);
        }
      } else if (stmts.get(i) instanceof Conditional) {
        // Continue through then and else branches
        Conditional c = (Conditional) stmts.get(i);
        List<Statement> thenBlock = c.getThenBlock();
        List<Statement> elseBlock = c.getElseBlock();
        Statement next = getNext(stmt, thenBlock);
        if (next == null)
          next = getNext(stmt, elseBlock);
        if (next != null)
          return next;
        // If stmt is the last of the then block or the else block, then the
        // statement that follows the conditional, is the next
        if (stmt.equals(thenBlock.get(thenBlock.size() - 1))
            || stmt.equals(elseBlock.get(elseBlock.size() - 1))) {
          if (i + 1 < stmts.size()) {
            // Get the statement that follows the conditional
            return stmts.get(i + 1);
          }
        }
      } else if (stmts.get(i) instanceof Iterative) {
        // Continue through the do block
        Iterative it = (Iterative) stmts.get(i);
        List<Statement> doBlock = it.getDoBlock();
        Statement next = getNext(stmt, doBlock);
        if (next != null) {
          return next;
        }
      }
      i++;
    }
    return null;
  }

  /**
   * Compute the set of dominators of each node in the Control Flow Graph
   */
  public Map<GraphNode, Set<GraphNode>> computeDom() {
    Map<GraphNode, Set<GraphNode>> doms = new HashMap<GraphNode, Set<GraphNode>>();
    doms.put(initial, new HashSet<GraphNode>());
    // Initially, the dominators of the initial node is just the initial node
    doms.get(initial).add(initial);
    // Also, for each node that is not the initial one, the dominators are all the nodes
    for (GraphNode node : g.vertexSet()) {
      if (!node.equals(initial)) {
        Set<GraphNode> allNodes = new HashSet<GraphNode>(g.vertexSet());
        doms.put(node, allNodes);
      }
    }
    boolean changeOcurred = true;
    while (changeOcurred) {
      changeOcurred = false;
      for (GraphNode node : g.vertexSet()) {
        if (!node.equals(initial)) {
          // For each node that is not the initial
          Set<GraphNode> currentDoms = doms.get(node);
          Set<GraphNode> intersectPreds = intersectPredecessors(node, doms);
          intersectPreds.add(node);
          if (!currentDoms.equals(intersectPreds)) {
            changeOcurred = true;
            doms.put(node, intersectPreds);
          }
        }
      }
    }
    return doms;
  }

  /**
   * Intersects the current dominators of all the predecessors of the given node
   */
  private Set<GraphNode> intersectPredecessors(GraphNode node,
      Map<GraphNode, Set<GraphNode>> doms) {
    Set<GraphNode> intersection = new HashSet<GraphNode>();
    Set<GraphNode> predecessors = getPredecessors(node);
    if (predecessors.size() > 0) {
      intersection = new HashSet<GraphNode>(doms.get(predecessors.iterator().next()));
      for (GraphNode pred : predecessors) {
        intersection.retainAll(doms.get(pred));
      }
    }
    return intersection;
  }

  /**
   * Compute the predecessors of a given node
   */
  private Set<GraphNode> getPredecessors(GraphNode node) {
    Set<GraphNode> pred = new HashSet<GraphNode>();
    for (LabeledEdge edge : g.edgeSet()) {
      if (node.equals(g.getEdgeTarget(edge)))
        pred.add(g.getEdgeSource(edge));
    }
    return pred;
  }

  /**
   * Get the reversed Control Flow Graph
   */
  public ControlFlowGraph reverse() {
    ControlFlowGraph reversedCfg = new ControlFlowGraph();
    reversedCfg.initial = ExitNode.get();
    for (GraphNode v : g.vertexSet()) {
      reversedCfg.g.addVertex(v);
    }
    for (LabeledEdge edge : g.edgeSet()) {
      reversedCfg.g.addEdge(g.getEdgeTarget(edge), g.getEdgeSource(edge),
          new LabeledEdge(EdgeType.CFG));
    }
    return reversedCfg;
  }

  /**
   * Get the augmented Control Flow Graph
   */
  public ControlFlowGraph getAugmentedCfg() {
    ControlFlowGraph acfg = new ControlFlowGraph();
    acfg.g.addVertex(StartNode.get());
    acfg.initial = StartNode.get();
    for (GraphNode v : g.vertexSet()) {
      acfg.g.addVertex(v);
    }
    acfg.g.addEdge(StartNode.get(), EntryNode.get(), new LabeledEdge(EdgeType.CFG, TRUE_LABEL));
    acfg.g.addEdge(StartNode.get(), ExitNode.get(), new LabeledEdge(EdgeType.CFG, FALSE_LABEL));
    for (LabeledEdge edge : g.edgeSet()) {
      acfg.g.addEdge(g.getEdgeSource(edge), g.getEdgeTarget(edge), new LabeledEdge(EdgeType.CFG));
    }
    return acfg;
  }

  /**
   * Get the Control Dependence Graph
   */
  public ControlDependenceGraph getCdg() {
    ControlFlowGraph acfg = getAugmentedCfg();
    System.out.println("    Augmented CFG vertexs: " + acfg.g.vertexSet().size());
    System.out.println("    Augmented CFG edges: " + acfg.g.edgeSet().size());
    return new ControlDependenceGraph(acfg, acfg.computePostDominatorsTree());
  }

  /**
   * Get the Data Dependence Graph
   */
  public DataDependenceGraph getDdg() {
    return new DataDependenceGraph(this);
  }

  /**
   * Get the Program Dependence Graph
   */
  public ProgramDependenceGraph getPdg() {
    return new ProgramDependenceGraph(this.getDdg(), this.getCdg());
  }

  /**
   * Get the Program Dependence Graph
   */
  public ProgramDependenceGraph getPdg(DataDependenceGraph ddg, ControlDependenceGraph cdg) {
    return new ProgramDependenceGraph(ddg, cdg);
  }

  /**
   * Compute the set of postdominators
   */
  public Map<GraphNode, Set<GraphNode>> computePostDom() {
    ControlFlowGraph reverse = reverse();
    return reverse.computeDom();
  }

  /**
   * Compute the postdominators tree
   */
  public Tree<GraphNode> computePostDominatorsTree() {
    ControlFlowGraph reverse = reverse();
    Map<GraphNode, Set<GraphNode>> postDoms = reverse.computeDom();
    Tree<GraphNode> postDomTree = new Tree<GraphNode>(reverse.initial);
    LinkedList<GraphNode> queue = new LinkedList<GraphNode>();
    // Enqueue the initial node
    queue.addLast(reverse.initial);
    // Remove each node from the dominators of itself
    for (GraphNode node : reverse.g.vertexSet()) {
      Set<GraphNode> nodeDoms = postDoms.get(node);
      nodeDoms.remove(node);
      postDoms.put(node, nodeDoms);
    }
    while (!queue.isEmpty()) {
      GraphNode current = queue.removeFirst();
      for (GraphNode node : reverse.g.vertexSet()) {
        // For each node in such that the dominators set is not empty
        Set<GraphNode> nodeDoms = postDoms.get(node);
        if (!nodeDoms.isEmpty()) {
          if (nodeDoms.contains(current)) {
            nodeDoms.remove(current);
            if (nodeDoms.isEmpty()) {
              postDomTree.addChild(current, node);
              queue.addLast(node);
            }
          }
        }
      }
    }
    return postDomTree;
  }

  /**
   * Perform the iterative dataflow analysis which consists of the computation of four sets: KILL,
   * GEN, IN, OUT
   */
  public void iterativeDataFlowAnalysis() {
    System.out.println("    Computing Gen and Kill sets");
    computeGenAndKillSets();
    System.out.println("    Computing Reaching Defs");
    computeReachingDefs();
  }

  /**
   * Compute GEN and KILL sets
   */
  private void computeGenAndKillSets() {
    for (GraphNode node : g.vertexSet()) {
      node.computeGen();
      node.computeKill(p.getStatements());
    }
  }

  /**
   * Compute the Reaching Definitions assuming that computeGenAndKillSets method has been executed
   */
  private void computeReachingDefs() {
    // OUT[n] = GEN[n]
    for (GraphNode node : g.vertexSet()) {
      node.setOut(new HashSet<Definition>(node.getGen()));
    }
    boolean change = true;
    while (change) {
      change = false;
      for (GraphNode node : g.vertexSet()) {
        // IN[n] = U OUT[P] where P is an immediate predecessor of node
        Set<GraphNode> preds = getPredecessors(node);
        Set<Definition> in = new HashSet<Definition>();
        for (GraphNode pred : preds) {
          in.addAll(new HashSet<Definition>(pred.getOut()));
        }
        node.setIn(in);
        // OLDOUT = OUT[n]
        Set<Definition> oldOut = node.getOut();
        // OUT[n] = GEN[n] U (IN[n] - KILL[n])
        Set<Definition> newOut = new HashSet<Definition>(node.getIn());
        newOut.removeAll(node.getKill());
        newOut.addAll(node.getGen());
        node.setOut(newOut);
        if (!newOut.equals(oldOut))
          change = true;
      }
    }
  }

  /**
   * Compute the Definition-Use Pairs
   */
  public Set<DUPair> computeDefUsePairs() {
    HashSet<DUPair> dupairs = new HashSet<DUPair>();
    for (GraphNode node : g.vertexSet()) {
      Set<Statement> upwardsExposedUses = getUpwardsExposedUses(node);
      for (Statement use : upwardsExposedUses) {
        for (Definition def : node.getIn()) {
          if (defUseSameVariable(def, use))
            dupairs.add(new DUPair(def.getStmt(), def.getGraphNode(), use, node));
        }
      }
    }
    return dupairs;
  }

  /**
   * Return true iff the given definition and the given use are over the same variable
   */
  private boolean defUseSameVariable(Definition def, Statement use) {
    if (def.getStmt() instanceof Assign) {
      String var = ((Assign) def.getStmt()).getVar();
      if (use instanceof Assign) {
        return !((Assign) use).getVar().equals(var)
            && ((Assign) use).getExpression().toString().contains(var);
      } else if (use instanceof Conditional) {
        return ((Conditional) use).getCondition().toString().equals(var);
      } else if (use instanceof Iterative) {
        return ((Iterative) use).getCondition().toString().equals(var);
      } else if (use instanceof Return) {
        return ((Return) use).getExpression().toString().contains(var);
      }
    }
    return false;
  }

  /**
   * Get upwards exposed uses in the given node
   */
  private Set<Statement> getUpwardsExposedUses(GraphNode node) {
    HashSet<Statement> ueu = new HashSet<Statement>();
    Set<Definition> in = node.getIn();
    for (Definition d : in) {
      Assign stmt = (Assign) d.getStmt();
      for (Statement nodeStmt : node.getStatements()) {
        if (uses(nodeStmt, stmt.getVar()))
          ueu.add(nodeStmt);
      }
    }
    return ueu;
  }

  /**
   * Returns true iff the given statement uses the given variable and it is not a definition
   */
  private boolean uses(Statement stmt, String var) {
    if (stmt instanceof Assign) {
      return stmt.toString().contains(var) && !((Assign) stmt).getVar().contains(var);
    } else if (stmt instanceof Conditional) {
      Conditional cond = (Conditional) stmt;
      return cond.getCondition() instanceof Var && cond.getCondition().toString().equals(var);
    } else if (stmt instanceof Iterative) {
      Iterative it = (Iterative) stmt;
      return it.getCondition() instanceof Var && it.getCondition().toString().equals(var);
    } else if (stmt instanceof Return) {
      return stmt.toString().contains(var);
    }
    return false;
  }

  /**
   * Export the graph as dot file
   */
  public void export() {
    ExportUtil.dotExport(g, "cfg.dot");
    System.out.println("    Exported to file output/cfg.dot");
  }
}
