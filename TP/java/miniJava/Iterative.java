package miniJava;

import java.util.LinkedList;


public class Iterative implements Statement{

	Expression cond;
	LinkedList<Statement> doBlock;


	public Iterative(Expression exp, LinkedList<Statement> doB){
		this.cond = exp;
		this.doBlock = doB;
	}
}