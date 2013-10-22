package edu.pdx.cs.multiview.smelldetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.util.eclipse.EclipseHacks;

@SuppressWarnings("restriction")
final public class EditorViewportListener implements IViewportListener,
													 IPainter{
	
	/*
	 * If true, the context is considered all methods in the visible window.
	 * If false, the context is considered the method closest to the center of the window.
	 */
	public static final ContextType context = ContextType.CURSOR_OR_CENTER_SCREEN;
	
	public enum ContextType{
		ALL_ON_SCREEN,
		CENTER_SCREEN,
		CURSOR_OR_CENTER_SCREEN
	}
	
	private static EditorViewportListener listener = new EditorViewportListener();
	private JavaEditor editor;
	private Collection<SmellDetector<?>> detectors;
	
	public static void listenTo(JavaEditor newEditor, Collection<SmellDetector<?>> ds) {		
		
		removeListener();
		
		listener.editor = newEditor;
		listener.detectors = ds;
				
		newEditor.getViewer().addViewportListener(listener);
		EclipseHacks.getSourceViewer(newEditor).addPainter(listener);
		
		listener.fireRecompute();
	}

	public static void removeListener() {
		if(listener.editor!=null && listener.editor.getViewer()!=null){
			EclipseHacks.getSourceViewer(listener.editor).removePainter(listener);
			listener.editor.getViewer().removeViewportListener(listener);
		}
	}

	public void viewportChanged(int offset) {		
		fireRecompute();
	}

	private void fireRecompute() {
		boolean someDetectorChanged = false;
		int oldCursorPosition = cursorPosition;
		List<IMethod> methods = null;
		try {
			methods = getMethodsIn(editor);
		} catch (JavaModelException e) {
			e.printStackTrace();
			return;
		}
		for(SmellDetector<?> detector : detectors){
			someDetectorChanged |= detector.recompute(editor.getViewer(),methods);
		}

		boolean cursorEntered = oldCursorPosition==-1 && cursorPosition!=-1;
		boolean cursorExited = cursorPosition==-1 && oldCursorPosition!=-1;
		if(someDetectorChanged || cursorEntered || cursorExited){
			detectors.iterator().next().redrawUI();
		}
	}
	
	private List<IMethod> getMethodsIn(JavaEditor e) throws JavaModelException {
		
		IRegion r;
		if(context==ContextType.ALL_ON_SCREEN){
			r = getVisibleRegion(e);
		}else if(context==ContextType.ALL_ON_SCREEN){
			r = getCenterRegion(e);
		}else{
			r = getCursorOrCenterRegion(e);
		}
		
		//TODO: this takes a long time to compute; can't we assume 
		//			that the same editor has the same cu?
		ICompilationUnit cu = JDTUtils.getCompilationUnit(e);
		IType[] types = cu.getAllTypes();
		List<IMethod> methods = new ArrayList<IMethod>(10);
		
		for(IType t : types)
			for(IMethod m : t.getMethods()){
				ISourceRange range = m.getSourceRange();
				if(overlaps(range,r.getOffset(),r.getOffset()+r.getLength()))
					methods.add(m);
			}
		
		return methods;
	}
	
	//this whole context thing should be a class; I'm breaking 
	//encapsulation badly here
	public static int cursorPosition = -1;
	
	private IRegion getVisibleRegion(JavaEditor e) throws JavaModelException {
		
		StyledText widget = e.getViewer().getTextWidget();
		Rectangle bounds = widget.getClientArea();
		int start = widget.getOffsetAtLocation(new Point(bounds.x,bounds.y));
		int end;
		try {
			end = widget.getOffsetAtLocation(new Point(bounds.x,bounds.y+bounds.height));
		} catch (RuntimeException e1) {
			System.err.println(e1);
			end = widget.getText().length();
		}		
		
		return ((TextViewer)e.getViewer()).widgetRange2ModelRange(new Region(start,end-start));
	}
	
	private IRegion getCenterRegion(JavaEditor e) throws JavaModelException {
		
		StyledText widget = e.getViewer().getTextWidget();
		Rectangle bounds = widget.getClientArea();
		int end;
		try {
			end = widget.getOffsetAtLocation(new Point(bounds.x+widget.getLeftMargin(),bounds.y+bounds.height/2));
		} catch (RuntimeException e1) {
			//this is thrown when we are in single-element-only mode, and there's nothing
			//past the halfway point.  In that case, the last character is fine
			System.err.println(e1); 
			end = widget.getText().length();
		}		
		
		return ((TextViewer)e.getViewer()).widgetRange2ModelRange(new Region(end,1));
	}
	
	private IRegion getCursorOrCenterRegion(JavaEditor e) throws JavaModelException{
		StyledText widget = e.getViewer().getTextWidget();
		Rectangle bounds = widget.getClientArea();
		
		int startOnScreen = widget.getOffsetAtLine(widget.getTopIndex());
		int endOnScreen;
		try {
			endOnScreen = widget.getOffsetAtLocation(new Point(bounds.x+widget.getLeftMargin(),widget.getTopMargin()+bounds.y+bounds.height));
		} catch (Exception _) {
			//occurs when there's no vertical scroll
			endOnScreen = widget.getText().length();
		}
		
		int caretOffset = widget.getCaretOffset();
		
		if(startOnScreen <= caretOffset && caretOffset <= endOnScreen){
			cursorPosition = caretOffset;
			return ((TextViewer)e.getViewer()).widgetRange2ModelRange(new Region(caretOffset,1));
		}
		cursorPosition = -1;
		return getCenterRegion(e);
	}
	
	public static boolean overlaps(ISourceRange r1, int start, int end) {		
		return	!((r1.getOffset() + r1.getLength()) < start || 
					end < r1.getOffset());
	}

	public void paint(int reason) {
		
		switch(reason){
			case IPainter.KEY_STROKE:
			case IPainter.MOUSE_BUTTON:
			case IPainter.SELECTION:
			case IPainter.TEXT_CHANGE:
				fireRecompute();
		}
	}

	public void deactivate(boolean redraw) {}
	public void dispose() {}
	public void setPositionManager(IPaintPositionManager manager) {}
}