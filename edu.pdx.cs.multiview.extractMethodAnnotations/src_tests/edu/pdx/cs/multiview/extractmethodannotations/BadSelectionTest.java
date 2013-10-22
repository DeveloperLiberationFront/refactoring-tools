package edu.pdx.cs.multiview.extractmethodannotations;

import edu.pdx.cs.multiview.extractmethodannotations.annotations.BadSelectionAnnotation;
import junit.framework.TestCase;

public class BadSelectionTest extends TestCase{

	public void testBreakStrings(){
		String[] strings = {
				"",
				"a",
				"a b",
				"a b c",
				"a c  d",
				" a d  ",
				"the quick brown fox jumped over the lazy dog"
		};
		
		for(String s : strings)
			assertEquals(s.trim(), BadSelectionAnnotation.breakUp(s).replace("\r   ", ""));
	}
}
