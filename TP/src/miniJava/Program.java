package miniJava;

import java.util.LinkedList;
import java.util.List;



public class Program{

	Type type;
	String id;
	LinkedList<Statement> statements;


	public Program(Type t, String id, LinkedList<Statement> stmts){
		this.type = t;
		this.id = id;
		this.statements = stmts;
	}
	
	/**
	 * Get the program statements
	 */
	public List<Statement> getStatements() {
		return statements;
	}
	
}