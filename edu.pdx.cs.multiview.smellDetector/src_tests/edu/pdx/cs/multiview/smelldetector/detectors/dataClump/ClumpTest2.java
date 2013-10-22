package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.List;

import junit.framework.TestCase;

public class ClumpTest2 extends TestCase {

	public void testEmpty(){
		List<ClumpSignature> sigs = ClumpSignature.from
						(new String[] {});
		assertEquals(0,sigs.size());
	}
	
	public void testOne(){
		List<ClumpSignature> sigs = ClumpSignature.from
						(new String[] {"one"});
		assertEquals(1,sigs.size());
	}
	
	public void testTwo(){
		List<ClumpSignature> sigs = ClumpSignature.from
						(new String[] {"one", "two"});
		assertEquals(1,sigs.size());
	}
	
	public void testThree(){
		List<ClumpSignature> sigs = ClumpSignature.from
						(new String[] {"one", "two", "three"});
		assertEquals(4,sigs.size());
	}
	
	public void testFour(){
		List<ClumpSignature> sigs = ClumpSignature.from
						(new String[] {"one", "two", "three","four"});
		assertEquals(11,sigs.size());
	}
}
