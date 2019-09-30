package parserMiniJava;

import java_cup.runtime.*;
%%

%public
%cup
%line
%column
%char
%full

%eofval{
return  new Symbol(symMiniJava.EOF,yyline,yycolumn,"");
%eofval}

%class scannerMiniJava
%{
	public scannerMiniJava(java.io.InputStream r, SymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	private SymbolFactory sf;
%}



digit=[0-9]
char=[a-zA-Z]
id = {char}({char}|{digit})*	
integer = {digit}{digit}*	
comment=("//"(.)*)|"/*"(((([^*/])*"*"[^/])*)|((([^*])*"/"[^*/])*)|([^*/])*)*"*/"
/* To ignore */
end_of_line   = \r|\n|\r\n
white_space     = {end_of_line} | [ \t\f]

%%


 
{comment} {}

"+" {   //System.out.println("SUMA:"+ yytext()); 
        return new Symbol(symMiniJava.PLUS,yyline,yycolumn,yytext());
    }
    
"(" {   //System.out.println("LPARENT:"+ yytext());
        return new Symbol(symMiniJava.LPARENT,yyline,yycolumn,yytext());
    }
    
")" {   //System.out.println("RPARENT:"+ yytext());
        return new Symbol(symMiniJava.RPARENT,yyline,yycolumn,yytext());
    }
    
";" {  //System.out.println("PUNTOYCOMA: "+ yytext());
        return new Symbol(symMiniJava.SEMICOLON,yyline,yycolumn,yytext());
    }
    
"=" {    //System.out.println(" ASIG:"+ yytext());
        return new Symbol(symMiniJava.EQUAL,yyline,yycolumn,yytext());
    }
    
"{" {   //System.out.println(" LLAVEABRE:"+ yytext());
        return new Symbol(symMiniJava.LBRACE,yyline,yycolumn,yytext());
    }

"}" {    //System.out.println(" LLAVECIERRA:"+ yytext());
        return new Symbol(symMiniJava.RBRACE,yyline,yycolumn,yytext());
    }
        
"integer" {  //System.out.println(" INT:"+ yytext());
         return new Symbol(symMiniJava.INT,yyline,yycolumn,yytext());
      }

"return" {  //System.out.println(" RETURN:"+ yytext());
         return new Symbol(symMiniJava.RETURN,yyline,yycolumn,yytext());
      }

"if" {  //System.out.println(" IF:"+ yytext());
         return new Symbol(symMiniJava.IF,yyline,yycolumn,yytext());
      }

"else" {  //System.out.println(" ELSE:"+ yytext());
         return new Symbol(symMiniJava.ELSE,yyline,yycolumn,yytext());
      }

"while" {  //System.out.println(" WHILE:"+ yytext());
         return new Symbol(symMiniJava.WHILE,yyline,yycolumn,yytext());
      }
       
          
{id}  {  //System.out.println("ID :  "+ yytext());
         return new Symbol(symMiniJava.ID,yyline,yycolumn,yytext());
      }	
 
       
{integer} {  //System.out.println(" VALORINT:"+ yytext());
            Integer value = new Integer(yytext());
            return new Symbol(symMiniJava.INTEGER,yyline,yycolumn,value);
          }
          
{white_space} {/* Ignore */} 

.     {   //return new Symbol(symMiniJava.LEXICAL_ERROR,yyline,yycolumn,yytext());
       }