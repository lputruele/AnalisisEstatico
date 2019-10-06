package staticAnalysis.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import miniJava.Conditional;
import miniJava.Iterative;
import miniJava.Program;
import miniJava.Statement;

/**
 * This class represents the Control Flow Graph
 * @author fmolina
 */
public class ControlFlowGraph {

	DefaultDirectedGraph<BasicBlock,DefaultEdge> g;
	
	/**
	 * Constructor of the Control Flow Graph from a program p
	 */
	public ControlFlowGraph(Program p) {
		List<BasicBlock> basicBlocks = getBasicBlocks(p);
		buildGraph(basicBlocks);
	}
	
	/**
	 * Get the basic blocks from a given program
	 */
	private List<BasicBlock> getBasicBlocks(Program p) {
		List<Statement> progStmts = p.getStatements();
		markLeaders(progStmts);
		// Construct the basic blocks considering the leaders
		List<BasicBlock> basicBlocks = computeBasicBlocks(null,progStmts);
		return basicBlocks;
	}
	
	/**
	 * Mark the leaders
	 */
	private void markLeaders(List<Statement> progStmts) {
		if (progStmts.size()>0) {
			// The first statement is a leader
			progStmts.get(progStmts.size()-1).setIsLeader(true);
			boolean nextIsLeader = false;
			for (int i=progStmts.size()-2;i>=0;i--) {
				Statement s = progStmts.get(i);
				// Determine if s is a leader
				if (s instanceof Conditional) {
					Conditional c = (Conditional)s;
					markLeaders(c.getThenBlock());
					markLeaders(c.getElseBlock());
					nextIsLeader = true;
				} else if (s instanceof Iterative) {
					markLeaders(((Iterative)s).getDoBlock());
					nextIsLeader = true;
				} else if (nextIsLeader){
					nextIsLeader = false;
					s.setIsLeader(true);
				}	
			}
		}
	}
	
	/**
	 * Compute the list of basic blocks from the consider the leader program statements
	 */
	private List<BasicBlock> computeBasicBlocks(BasicBlock currBasicBlock,List<Statement> progStmts) {
		List<BasicBlock> basicBlocks = new ArrayList<BasicBlock>();
		boolean addCurr = false;
		int j = progStmts.size()-1;
		while (j >= 0) {
			Statement stmt = progStmts.get(j);
			if (stmt.isLeader()) {
				if (currBasicBlock!=null) {
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
					currBasicBlock = null;
					addCurr = false;
					if (stmt instanceof Conditional) {
						Conditional c = (Conditional)stmt;
						basicBlocks.addAll(computeBasicBlocks(null,c.getThenBlock()));
						basicBlocks.addAll(computeBasicBlocks(null,c.getElseBlock()));
					} else if (stmt instanceof Iterative) {
						Iterative i = (Iterative)stmt;
						basicBlocks.addAll(computeBasicBlocks(null,i.getDoBlock()));
					} 
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
	private void buildGraph(List<BasicBlock> basicBlocks) {
		g = new DefaultDirectedGraph<BasicBlock, DefaultEdge>(DefaultEdge.class);
	}
}
