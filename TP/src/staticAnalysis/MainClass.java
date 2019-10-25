package staticAnalysis;

import miniJava.Program;

/**
 * This the main class
 */
public class MainClass {

  public static void main(String[] args) {
    ProgramParser prog = new ProgramParser();

    if (args.length < 1) {
      System.out.println("Usage: ./TPExe <test example>");
    } else {
      System.out.println("Starting analysis of program " + args[args.length - 1]);
      System.out.println();
      System.out.println("Parsing..");
      Program p = prog.parseAux(args[args.length - 1]);
      StaticAnalyser sa = new StaticAnalyser(p);
    }
  }
}