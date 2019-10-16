package staticAnalysis.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import miniJava.Assign;
import miniJava.Conditional;
import miniJava.Iterative;
import miniJava.Statement;

/**
 * This class represents a Basic Block, i.e., a sequence of program statements in which flow of
 * control enters at the beginning and leaves at the end without halt or possibility of branching
 * except at the end.
 * 
 * @author fmolina
 */
public class BasicBlock extends GraphNode {

  private List<Statement> statements; // Program statements that are part of the block

  /**
   * Default constructor
   */
  public BasicBlock() {
    statements = new ArrayList<Statement>();
  }

  /**
   * Add an statement to the building block
   */
  public void addStatement(Statement stmt) {
    statements.add(stmt);
  }

  /**
   * Get statements
   */
  public List<Statement> getStatements() {
    return statements;
  }

  /**
   * Set id
   */
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    String str = "[ ";
    for (Statement stmt : statements) {
      if (stmt instanceof Conditional) {
        Conditional c = (Conditional) stmt;
        str += c.getCondition().toString() + ", ";
      } else if (stmt instanceof Iterative) {
        Iterative it = (Iterative) stmt;
        str += it.getCondition().toString() + ", ";
      } else {
        str += stmt.toString() + ", ";
      }
    }
    str = str.substring(0, str.length() - 2);
    str += " ]";
    return str;
  }

  @Override
  public void computeGen() {
    gen = new HashSet<Definition>();
    assert statements.size() == 1;
    Statement stmt = statements.get(0);
    if (stmt instanceof Assign) {
      gen.add(new Definition(stmt));
    }
  }

  @Override
  public void computeKill(List<Statement> progStmt) {
    kill = new HashSet<Definition>();
    assert statements.size() == 1;
    Statement stmt = statements.get(0);
    if (stmt instanceof Assign) {
      kill.add(new Definition(stmt));
      computeKillRecursivelly(kill, (Assign) stmt, progStmt);
    }
  }

  /**
   * Compute kill recursivelly
   */
  private boolean computeKillRecursivelly(Set<Definition> killSet, Assign target,
      List<Statement> progStmt) {
    boolean continueComputation = true;
    for (Statement stmt : progStmt) {
      if (stmt instanceof Assign) {
        if (stmt.equals(target)) {
          continueComputation = false;
        } else {
          if (((Assign) stmt).getVar().equals(target.getVar())) {
            killSet.add(new Definition(stmt));
          }
        }
      }
      if (stmt instanceof Conditional) {
        continueComputation = computeKillRecursivelly(killSet, target,
            ((Conditional) stmt).getThenBlock());
        if (continueComputation)
          continueComputation = computeKillRecursivelly(killSet, target,
              ((Conditional) stmt).getElseBlock());
      }
      if (stmt instanceof Iterative) {
        continueComputation = computeKillRecursivelly(killSet, target,
            ((Iterative) stmt).getDoBlock());
      }
      if (!continueComputation)
        break;
    }
    return continueComputation;
  }
}