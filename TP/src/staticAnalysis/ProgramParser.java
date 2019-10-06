package staticAnalysis;

import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;
import miniJava.*;
import parserMiniJava.*;

/**
 * This class represents the compiler.
 */
public class ProgramParser {
	
	private parserMiniJava parser;	// Parser
    private static FileReader programFile;
    
    
    public ProgramParser(){
    	this.programFile=null;
    }
    
    

    public Program parseAux(String nameFile){
        try {
            programFile = new FileReader(nameFile);
        
            // Read file
            parser = new parserMiniJava(new scannerMiniJava(programFile));
            Program program = (Program)parser.parse().value;
            return program;

        } catch (Exception e) {         
            System.out.println("Program Error." + e.getMessage());
            e.printStackTrace(System.out);
        }
        
        return null;
        
    }

    
 	

 	
}