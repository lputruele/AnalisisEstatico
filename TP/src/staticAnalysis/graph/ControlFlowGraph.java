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
		List<Statement> leaders = getLeaders(progStmts);
		System.out.print("Total leaders: "+leaders.size());
		// Construct the basic blocks from the leaders
		List<BasicBlock> basicBlocks = new LinkedList<BasicBlock>();
		return basicBlocks;
	}
	
	/**
	 * Get the leaders
	 */
	private List<Statement> getLeaders(List<Statement> progStmts) {
		List<Statement> leaders = new ArrayList<Statement>();
		// The first statement is a leader
		leaders.add(progStmts.get(0));
		boolean nextIsLeader = false;
		for (int i=1;i<progStmts.size();i++) {
			Statement s = progStmts.get(i);
			// Determine if s is a leader
			if (s instanceof Conditional) {
				Conditional c = (Conditional)s;
				leaders.addAll(getLeaders(c.getThenBlock()));
				leaders.addAll(getLeaders(c.getElseBlock()));
				nextIsLeader = true;
			} else if (s instanceof Iterative) {
				leaders.addAll(((Iterative)s).getDoBlock());
				nextIsLeader = true;
			} else if (nextIsLeader){
				nextIsLeader = false;
				leaders.add(s);
			}		
		}
		return leaders;
	}
	
	/**
	 * Build the Control Flow Graph from the program basic blocks
	 */
	private void buildGraph(List<BasicBlock> basicBlocks) {
		g = new DefaultDirectedGraph<BasicBlock, DefaultEdge>(DefaultEdge.class);
	}
}
