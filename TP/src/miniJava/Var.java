package miniJava;


public class Var implements Expression{

	String label;


	public Var(String id){
		this.label = id;
	}

	@Override
	public String toString(){
		return label;
	}
}