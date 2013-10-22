package edu.pdx.cs.multiview.smelldetector;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorManager {

	static int[] splitInto(int size, int upperBound) {
		
		int[] is = new int [size];
	
		if(size>2)		
			for(int i = 0; i < size; i++)
				is[i] = upperBound*i/(size-1);
		
		return is;
	}

	/**
	 * @param size
	 * @return		returns a gradient between two colors.  Clients are
	 * 				responsible for disposing these colors
	 */
	public static ColorIterator gradient(int size){
		return new ColorIterator(size,false);			
	}
	
	public static ColorIterator colorRange(int size){
		return new ColorIterator(size,true);
	}

	public static class ColorIterator implements Iterator<Color>, Iterable<Color>{

		int index;
		ArrayList<Color> colors;
		
		public ColorIterator(int size, boolean fullRange){
			colors = new ArrayList<Color>(size);
			index = 0;
			if(fullRange)
				fillFullRange(size);
			else
				gradient(size);
		}
		
		private void fillFullRange(int size) {
			
			int[] intervals = splitInto(size+1,360);
			//use all intervals but the last one (it wraps around in HSV)
			for(int i = 0; i < size; i++){
				RGB rgb = new RGB(intervals[i],1f,1f);				
				colors.add(i,new Color(null,rgb));
			}
		}

		private void gradient(double size) {
			
			Color color1 = new Color(null,0,51,204);
			Color color2 = new Color(null,255,133,0);
			
			double p,r,g,b;
			for(double i = 0; i<size; i++){
				p = i/(size-1);
				r = color1.getRed()*p + color2.getRed()*(1-p);
				g = color1.getGreen()*p + color2.getGreen()*(1-p);
				b = color1.getBlue()*p + color2.getBlue()*(1-p);
				colors.add((int)i,new Color(null,(int)r,(int)g,(int)b));
			}	
			
			color1.dispose();
			color2.dispose();
		}

		public boolean hasNext() {
			return index<colors.size();
		}

		public Color next() {
			return colors.get(index++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Iterator<Color> iterator() {
			return this;
		}

	}
		
}
