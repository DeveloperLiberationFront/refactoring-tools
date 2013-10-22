package edu.pdx.cs.multiview.extractmethodannotations.util;

import java.util.Enumeration;

import org.eclipse.swt.graphics.Color;

/**
 * I produce a set of colors spaced evenly
 * around the color wheel
 * 
 * @author emerson
 */
public class ColorManager {

	//http://www.visibone.com/color/faq.html
	private static Color[] hues = new Color[]
	                                   {new Color(null, 255,0,0),
										new Color(null,0,255,0),
										new Color(null,51,153,255),
										new Color(null,255,153,51),
										new Color(null,51,255,153),
										new Color(null,255,0,255),
										new Color(null,0,0,255),
										new Color(null,0,255,255),
										new Color(null,153,51,255),
										new Color(null,153,255,51),
										new Color(null,255,51,153)};
	
	public static Enumeration<Color> getColors(){
		ColorIterator i = new ColorIterator();
		i.colors = hues;
		return i;
	}

	private static class ColorIterator implements Enumeration<Color>{

		private int index = 0;
		private Color[] colors;
		
		public boolean hasMoreElements() {
			return true;
		}

		public Color nextElement() {
			return colors[index++ % colors.length];
		}		
	}
}
