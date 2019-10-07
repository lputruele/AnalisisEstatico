package staticAnalysis.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import miniJava.Conditional;
import miniJava.Iterative;
import miniJava.Program;
import miniJava.Statement;

/**
 * This class represents the Control Flow Graph
 * 
 * @author fmolina
 */
public class ControlFlowGraph {

  DefaultDirectedGraph<ControlFlowGraphNode, DefaultEdge> g;

  /**
   * Constructor of the Control Flow Graph from a program p
   */
  public ControlFlowGraph(Program p) {
    List<BasicBlock> basicBlocks = getBasicBlocks(p);
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
    g = new DefaultDirectedGraph<ControlFlowGraphNode, DefaultEdge>(DefaultEdge.class);
    g.addVertex(EntryNode.getEntry());
    g.addVertex(ExitNode.getExit());
    // Add node Entry -> B1
    ControlFlowGraphNode entryBlock = basicBlocks.get(0);
    g.addVertex(entryBlock);
    g.addEdge(EntryNode.getEntry(), entryBlock);
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
          g.addEdge(blockI, blockJ);
        }
      }
      if (!atLeastOneFollower) {
        // BlockI must go to the exit, i.e., Bi -> Exit
        g.addEdge(blockI, ExitNode.getExit());
      }
    }
    System.out.println("Vertexs: " + g.vertexSet().size());
    System.out.println("Edges: " + g.edgeSet().size());
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
   * Export the graph as dot file
   */
  public void export() {
    try {
      VertexNameProvider<ControlFlowGraphNode> vertexIdProvider = new VertexNameProvider<ControlFlowGraphNode>() {
        public String getVertexName(ControlFlowGraphNode cfgn) {
          return cfgn.getId();
        }
      };
      VertexNameProvider<ControlFlowGraphNode> vertexLabelProvider = new VertexNameProvider<ControlFlowGraphNode>() {
        public String getVertexName(ControlFlowGraphNode cfgn) {
          return cfgn.toString();
        }
      };
      DOTExporter<ControlFlowGraphNode, DefaultEdge> exporter = new DOTExporter<ControlFlowGraphNode, DefaultEdge>(
          vertexIdProvider, vertexLabelProvider, null);
      exporter.export(new FileWriter("cfg.dot"), g);
    } catch (IOException e) {
      System.out.println("Unable to compute graph");
    }
  }
}
