package miniJava;


public class Assign implements Statement{

	String var;
	Expression exp;


	public Assign(String id, Expression exp){
		this.var = id;
		this.exp = exp;
	}
}