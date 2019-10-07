package staticAnalysis.graph;

import java.util.ArrayList;
import java.util.List;

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
    markLeaders(progStmts);
    // Construct the basic blocks considering the leaders
    List<BasicBlock> basicBlocks = computeBasicBlocks(null, progStmts);
    return basicBlocks;
  }

  /**
   * Mark the leaders
   */
  private void markLeaders(List<Statement> progStmts) {
    if (progStmts.size() > 0) {
      // The first statement is a leader
      progStmts.get(progStmts.size() - 1).setIsLeader(true);
      boolean nextIsLeader = false;
      for (int i = progStmts.size() - 2; i >= 0; i--) {
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
    int j = progStmts.size() - 1;
    BasicBlock currBasicBlock = block;
    while (j >= 0) {
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
      j--;
    }
    if (addCurr)
      basicBlocks.add(currBasicBlock);
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
      g.addVertex(blockI);
      boolean atLeastOneFollower = false;
      Statement lastStmtBlockI = blockI.getStatements().get(blockI.getStatements().size() - 1);
      for (int j = i + 1; j < basicBlocks.size(); j++) {
        BasicBlock blockJ = basicBlocks.get(j);
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
    Statement next = getNext(stmt1, p.getStatements());
    if (next != null) {
      if (next.equals(stmt2)) {
        // stmt2 immediately follows stmt1 in p
        return true;
      } else if (next instanceof Conditional) {
        Conditional c = (Conditional) next;
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
      } else if (next instanceof Iterative) {
        Iterative it = (Iterative) next;
        Statement fstDoBlock = it.getDoBlock().size() > 0 ? it.getDoBlock().get(0) : null;
        if (stmt2.equals(fstDoBlock)) {
          // stmt2 follows stmt1 through the do branch of an iterative statement
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Given a statement and a list of statements of a program, return the next statement if exists
   */
  private Statement getNext(Statement stmt, List<Statement> stmts) {
    int i = stmts.size() - 1;
    while (i >= 0) {
      if (stmts.get(i).equals(stmt)) {
        // We are in stmt
        if (i - 1 >= 0) {
          // Get the next statement
          return stmts.get(i - 1);
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
          if (i - 1 >= 0) {
            // Get the statement that follows the conditional
            return stmts.get(i - 1);
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
        if (stmt.equals(doBlock.get(doBlock.size() - 1))) {
          if (i - 1 >= 0) {
            // Get the next statement that follows the iterative
            return stmts.get(i - 1);
          }
        }
      }
      i--;
    }
    return null;
  }
}
