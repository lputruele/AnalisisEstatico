package miniJava;


public class Return extends Statement{

	Expression exp;

	public Return(Expression exp){
		this.exp = exp;
	}


	@Override
	public boolean isControlTransferStatement() {
		return false;
	}

	@Override
	public String toString(){
		return "return " + exp.toString();
	}
	
}