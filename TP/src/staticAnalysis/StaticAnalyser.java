package staticAnalysis;

import miniJava.*;
import staticAnalysis.graph.ControlFlowGraph;

/**
 * This class represents the compiler.
 */
public class StaticAnalyser {
	   
  private ControlFlowGraph cfg;
  
  public StaticAnalyser(Program p){
   if (p==null)
      throw new IllegalArgumentException("The program can't be null");
   System.out.println(p);
   cfg = new ControlFlowGraph(p);
  }
 
  public void doSomething(){
	   
  }
  
}