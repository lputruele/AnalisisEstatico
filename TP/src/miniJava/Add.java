package miniJava;


public class Add implements Expression{

	Expression exp1;
	Expression exp2;


	public Add(Expression e1, Expression e2){
		this.exp1 = e1;
		this.exp2 = e2;
	}

	@Override
	public String toString(){
		return exp1.toString() + " + " + exp2.toString();
	}
}