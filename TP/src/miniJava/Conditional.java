package miniJava;

import java.util.LinkedList;
import java.util.List;


public class Conditional implements Statement{

	Expression cond;
	LinkedList<Statement> thenBlock;
	LinkedList<Statement> elseBlock;


	public Conditional(Expression exp, LinkedList<Statement> thenB, LinkedList<Statement> elseB){
		this.cond = exp;
		this.thenBlock = thenB;
		this.elseBlock = elseB;
	}
	
	/**
	 * Get then block
	 */
	public List<Statement> getThenBlock() {
		return thenBlock;
	}
	
	/**
	 * Get else block
	 */
	public List<Statement> getElseBlock() {
		return elseBlock;
	}
}