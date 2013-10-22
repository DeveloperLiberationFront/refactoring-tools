package edu.pdx.cs.multiview.statementHelper;

import java.util.Stack;

import junit.framework.TestCase;
import edu.pdx.cs.multiview.statementHelper.StatementVisitor.BackwardStack;

public class StackTest extends TestCase{
	
	/**
	 * Assures that the loop construct in java iterates
	 * through a stack top-to-bottom
	 */
	public void testBackwardStack(){
		
		String a = "a",
				b = "b";
		
		Stack<String> s = new BackwardStack<String>();
		s.push(a);
		s.push(b);
		
		
		boolean isFirst = true;
		for(String i : s)
			if(isFirst){
				assertEquals(i,b);
				isFirst = false;
			}
			else
				assertEquals(i,a);
	}

}
