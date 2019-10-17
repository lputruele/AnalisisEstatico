package staticAnalysis;

import miniJava.Program;

/**
 * This class represents the compiler.
 */
public class MainClass {

  public static void main(String[] args) {
    ProgramParser prog = new ProgramParser();

    if (args.length < 1) {
      System.out.println("Usage: ./TPExe <nominal model path>");
    } else {
      Program p = prog.parseAux(args[args.length - 1]);
      StaticAnalyser sa = new StaticAnalyser(p);
    }
  }
}