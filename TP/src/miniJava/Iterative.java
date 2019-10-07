package miniJava;

import java.util.LinkedList;
import java.util.List;

public class Iterative extends Statement {

  Expression cond;
  LinkedList<Statement> doBlock;

  public Iterative(Expression exp, LinkedList<Statement> doB) {
    this.cond = exp;
    this.doBlock = doB;
  }

  /**
   * Get do block
   */
  public List<Statement> getDoBlock() {
    return doBlock;
  }

  @Override
  public boolean isControlTransferStatement() {
    return true;
  }

  /**
   * Get the condition
   */
  public Expression getCondition() {
    return cond;
  }

  /**
   * Get the statements that can be executed in the last step of any path
   */
  public List<Statement> getLastStatements() {
    return getLastStatementsRecursively(doBlock);
  }

  /**
   * Get the last statements recursively
   */
  private List<Statement> getLastStatementsRecursively(List<Statement> stmts) {
    List<Statement> lasts = new LinkedList<Statement>();
    if (stmts.size() > 0) {
      Statement currLast = stmts.get(stmts.size() - 1);
      if (!currLast.isControlTransferStatement()) {
        lasts.add(currLast);
      } else {
        if (currLast instanceof Conditional) {
          lasts.addAll(getLastStatementsRecursively(((Conditional) currLast).getThenBlock()));
          lasts.addAll(getLastStatementsRecursively(((Conditional) currLast).getElseBlock()));
        } else if (currLast instanceof Iterative) {
          lasts.addAll(getLastStatementsRecursively(((Iterative) currLast).getDoBlock()));
        }
      }
    }
    return lasts;
  }

  @Override
  public String toString() {
    return "while (" + cond.toString() + ") {\n" + doBlock.toString() + "}\n";
  }

}