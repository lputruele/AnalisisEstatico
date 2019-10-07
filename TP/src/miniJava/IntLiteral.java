package miniJava;


public class IntLiteral implements Expression{

	Integer value;


	public IntLiteral(Integer i){
		this.value = i;
	}

	@Override
	public String toString(){
		return value.toString();
	}
}