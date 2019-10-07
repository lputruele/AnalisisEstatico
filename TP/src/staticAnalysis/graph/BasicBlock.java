package staticAnalysis.graph;

import java.util.ArrayList;
import java.util.List;

import miniJava.Statement;

/**
 * This class represents a Basic Block, i.e., a sequence of program statements in which 
 * flow of control enters at the beginning and leaves at the end without halt or possibility 
 * of branching except at the end.
 * 
 * @author fmolina
 */
public class BasicBlock extends ControlFlowGraphNode {

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
		if (stmt==null||stmt.isControlTransferStatement())
			throw new IllegalArgumentException("Invalid statement");
		statements.add(stmt);
	}
	
	/**
	 * Get statements
	 */
	public List<Statement> getStatements() {
		return statements;
	}
	
}