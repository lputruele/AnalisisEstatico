package miniJava;

import java.util.LinkedList;
import java.util.List;


public class Iterative implements Statement{

	Expression cond;
	LinkedList<Statement> doBlock;


	public Iterative(Expression exp, LinkedList<Statement> doB){
		this.cond = exp;
		this.doBlock = doB;
	}
	
	/**
	 * Get do block
	 */
	public List<Statement> getDoBlock() {
		return doBlock;
	}
	
}