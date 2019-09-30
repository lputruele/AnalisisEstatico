package miniJava;

import java.util.LinkedList;


public class Conditional implements Statement{

	Expression cond;
	LinkedList<Statement> thenBlock;
	LinkedList<Statement> elseBlock;


	public Conditional(Expression exp, LinkedList<Statement> thenB, LinkedList<Statement> elseB){
		this.cond = exp;
		this.thenBlock = thenB;
		this.elseBlock = elseB;
	}
}