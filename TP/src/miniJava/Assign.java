package miniJava;


public class Assign extends Statement{

	String var;
	Expression exp;


	public Assign(String id, Expression exp){
		this.var = id;
		this.exp = exp;
	}

	@Override
	public boolean isControlTransferStatement() {
		return false;
	}

	@Override
	public String toString(){
		return var + " = " + exp.toString();
	}
	
}