/* *** This file is given as part of the programming assignment. *** */
//import static java.lang.System.out;
import java.util.*;
public class Parser 
{
  // tok is global to all these parsing methods;
  // scan just calls the scanner's scan method and saves the result in tok.
  private Token tok; // the current token
  private ArrayList< Variable > curList;
  private ArrayList< ArrayList< Variable > > LList;
  private int listCnt;
  
  private void scan() 
  {
	    tok = scanner.scan();
  }

  private Scan scanner;
  Parser(Scan scanner) 
  {
    this.listCnt = -1;
    //this.list = new ArrayList< Variable >();
    this.LList = new ArrayList< ArrayList< Variable > >();
	  this.scanner = scanner;
    scan();
    program();

	  if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
  }
    
  private boolean isInList(int listID)
  {
    if (this.LList.get( listCnt ).isEmpty() | listID < 0)
      return false;

    for ( int i = 0; i < this.LList.get(listCnt).size(); i++) 
    {
      // System.out.println(this.list.get(i).name + this.tok.string + (this.list.get(i).name.equals( this.tok.string)));
      if (this.LList.get(listID).get(i).name.equals (this.tok.string) )
      {
        //System.out.println(this.LList.get(listID).get(i).name );
        return true;
      }
    }
    return false;
  }

  private boolean isInLists()
  {  
    //if (this.LList.isEmpty() & this.LList.get(listCnt).isEmpty())
    //  return false;

    for ( int lCnt = 0; lCnt < this.LList.size(); lCnt++) 
    {
      for ( int vCnt = 0; vCnt < this.LList.get(lCnt).size(); vCnt++) 
      {
        // System.out.println(lCnt + " " + vCnt + " " + this.LList.get(lCnt).get(vCnt).name);
        // System.out.println(this.LList.get(lCnt).get(vCnt).name + this.tok.string + (this.LList.get(lCnt).get(vCnt).name.equals( this.tok.string)));
        // System.out.println(this.LList.get(lCnt).get(vCnt).name + this.tok.string + (this.LList.get(lCnt).get(vCnt).name.equals( this.tok.string)));
        if (this.LList.get(lCnt).get(vCnt).name.equals(this.tok.string) ) 
        {
          System.out.println(this.LList.get(lCnt).get(vCnt).ID);
          return true;
        }
          
      }
    }
      
    //System.out.println(this.LList.size() + " " + this.listCnt);
    return false;
  }

  private boolean hasDeclared()
  {
    // check if a variable is declared, 
    // false then declare
    if (tok.kind != TK.ID)
      System.err.println("ERROR: trying declaring non-ID.");

    if ( !isInList(this.listCnt)) 
    {
      //System.out.println(this.tok.toString() );
      this.LList.get(listCnt).add( new Variable(this.tok.string, this.listCnt));
      return false;       
    }
    else 
    {
      System.err.println("redeclaration of variable " + this.tok.string);
      return true;
    }
  }

  private void program() 
  {
    System.out.println("#include<stdio.h>");
    System.out.println("int main()");
    block();
  }

  private void block() 
  {
    // if (listCnt == -1)
    LList.add( new ArrayList< Variable >() );

    listCnt++;
      
    System.out.println("{");
	  declaration_list();
	  statement_list();
    System.out.println("}");
      
    LList.remove( listCnt );
    listCnt--;
  }

  private void declaration_list() 
  {
	  // below checks whether tok is in first set of declaration.
	  // here, that's easy since there's only one token kind in the set.
	  // in other places, though, there might be more.
	  // so, you might want to write a general function to handle that.
	  while( is(TK.DECLARE) ) 
	  {
	    declaration();
	  } 
  }

  private void declaration() 
  {
    boolean beenDeclared = false;

	  mustbe(TK.DECLARE);
    if (is(TK.ID))
    {
      beenDeclared = hasDeclared(); 
      if( !beenDeclared )
        System.out.println("int " + tok.string + listCnt + ";");
    }
	  mustbe(TK.ID);

    while( is(TK.COMMA) ) 
    {
	    scan();
      if (is(TK.ID))
        beenDeclared = hasDeclared(); 
      if( !beenDeclared )
        System.out.println("int " + tok.string + listCnt + ";");
	    mustbe(TK.ID);
	  }  
    //if (beenDeclared)
      //System.exit(1);
  }

  private void statement_list() 
  {
    // System.out.println("I am here testing" );
 
    while ( is(TK.ID) | is(TK.TILDE) // assignment  
           | is(TK.PRINT) | is(TK.DO) | is(TK.IF)
           | is(TK.FOR) )
      statement();
  }

  private void statement() 
  {
    // statement ::= assignment | print | do | if
    // System.out.println(tok);
      
    if (is(TK.ID) | is(TK.TILDE))
    {
      //  System.out.println("Say something");
      assignment();
      System.out.println(";");
    }
    else if (is(TK.PRINT))
      print();
    else if (is(TK.DO))
      DO();
    else if (is(TK.FOR))
      FOR();
    else if (is(TK.IF))
      IF();
    else
    {
      System.err.println("It is not a statement, liar!");
      System.exit(1);
    }      
    // scan();
  }
   
  private void assignment() 
  {
    // assignment ::= ref_id '=' expr
    mustRef_ID( );
    mustbe(TK.ASSIGN);
    System.out.println("=");
    mustExpr();
    // System.out.println(";");
  }
  
  private void print() 
  {
    // print ::= '!' expr
    scan();
    System.out.println("printf(\"%d\\n\", ");
      
    mustExpr();
    System.out.println(");");
  }

  private void DO() 
  {
    // do ::= '<' guarded_command '>'
    mustbe(TK.DO);
    System.out.println("while(");
    mustGC();
    mustbe(TK.ENDDO);
  }

  private void FOR()
  {
    // for ::= '&' assignment expression assignment block
    mustbe(TK.FOR);
    System.out.println("for(");
    assignment();
    System.out.println(";");
    
    mustExpr();
    System.out.println("<= 0;");
    
    assignment();
    System.out.println(")");
    block();
  }

  private void IF()
  {
    // if ::= '[' guarded_command { '|' guarded_command } [ '%' block ] ']'
    mustbe(TK.IF);
    System.out.println("if(");
    mustGC();
    //System.out.println(")");
    while (is(TK.ELSEIF))
    {
      System.out.println("else if(");
      scan();
      mustGC();
      //System.out.println(")");
    }
    
    if (is(TK.ELSE))
    {
      System.out.println("else");
      scan();
      block();
    }
    mustbe(TK.ENDIF);
  }

  private void mustGC()
  {
    // guarded_command ::= expr ':' block 
    mustExpr();
    System.out.println("<=0");
    System.out.println(")");
    mustbe(TK.THEN);
    block();
  }

  private void mustExpr()
  {
    // expr ::= term { addop term }
    mustTerm();
    while( isAddop() )
    {
      System.out.println(tok.string);
      scan();
      mustTerm();
    }
  }
    
  private void mustTerm()
  {
    // term ::= factor { multop factor } 
    mustFactor();
    while( isMultop())
    {
      System.out.println(tok.string);
      scan();
      mustFactor();
    } 
  }
 
  private void mustFactor()
  {
    // factor ::= '(' expr ')' | ref_id | number
    if (is(TK.LPAREN))
    {
      System.out.println("(");
      scan();
      mustExpr();
      mustbe(TK.RPAREN);
      System.out.println(")");
    } 
    else if ( is(TK.TILDE) | is(TK.ID) )
      mustRef_ID( );
    else if ( is(TK.NUM)) {
      System.out.println(tok.string);
      mustbe( TK.NUM );
    }
    else
    {
      System.err.println ("It is not a factor" );
      System.exit(1);
    }
  }
    
  private void printVarID (int listID) 
  {
    if (!isInList(listID)) 
    {
      System.out.println("(printVarID)ERROR: try printing non-existed ID");
      System.exit(1);
    }
    
    for (int varCnt = 0; varCnt < LList.get(listID).size(); varCnt++) 
    {
      if (LList.get(listID).get(varCnt).name.equals(this.tok.string))
      {
        System.out.println(LList.get(listID).get(varCnt).ID);
        break;
      } // if current token is equal to variable in list
    } // iterates through variables
  }
  
  private void mustRef_ID()
  {
    // ref_id ::= [ '~' [ number ] ] id
    boolean hasTilde = false;
    String num = null;
    int tNum = -1;
    if (is(TK.TILDE))
    {
      hasTilde = true;
      mustbe(TK.TILDE);
     
      if (!is(TK.ID))
      {
        num = tok.string;
        tNum = Integer.parseInt(num);
        mustbe(TK.NUM);
      }
      
      //System.out.println(tNum);
      if (tNum == -1) // -1 means no number input 
      {
        if (!isInList(0)) // it cant find it in global, it is wrong
        {
          System.err.println("no such variable " + "~" + this.tok.string + " on line " + this.tok.lineNumber);
          System.exit(1);
        }
        else 
          printVarID(0);
      } 
      else if ( isInList(this.listCnt - tNum) )
        printVarID (this.listCnt - tNum);
      
      else {
        System.err.println("no such variable " + "~" + num + this.tok.string + " on line " + this.tok.lineNumber);
        System.exit(1);
      }
        
    }
    else
    { 
      if ( isInList(this.listCnt) )
        printVarID(this.listCnt);
      else if (!isInLists())
      { 
        if(!isInLists())
        {
          System.err.println( this.tok.string + " is an undeclared variable on line " + this.tok.lineNumber);
          System.exit(1);
        }
      }
    }
    mustbe(TK.ID);
  }

  private boolean isAddop()
  {
    if ( tok.kind == TK.PLUS | tok.kind == TK.MINUS )
      return true;
    else
      return false;
  }

  private boolean isMultop()
  {
    if ( tok.kind == TK.TIMES | tok.kind == TK.DIVIDE )
      return true;
    else
      return false;
  }
  
  // is current token what we want?
  private boolean is(TK tk)
  {
    return tk == tok.kind;
  }

  // ensure current token is tk and skip over it.
  private void mustbe(TK tk)
  {
	  if( tok.kind != tk )
	  {
	    System.err.println( "mustbe: want " + tk + ", got " +
				                  tok);
	    parse_error( "missing token (mustbe)" );
	  }
	  scan();
  }

  private void parse_error(String msg)
  {
	  System.err.println( "can't parse: line "
		                    + tok.lineNumber + " " + msg );
	  System.exit(1);
  }
  
} // class Parser

class Variable
{
  public String name;
  public String ID;
  public Variable(String name, int listCnt)
  {
    this.name = name;
    this.ID  = name + Integer.toString(listCnt);
  } // Variable constructor
} // class Variable
