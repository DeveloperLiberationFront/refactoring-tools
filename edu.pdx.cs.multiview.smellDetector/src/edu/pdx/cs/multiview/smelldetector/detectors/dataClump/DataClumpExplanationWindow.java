package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationOverlay;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationWindow;
import edu.pdx.cs.multiview.smelldetector.detectors.featureEnvy.TranslucentLabel;
import edu.pdx.cs.multiview.smelldetector.ui.TranslucentComponent;

public class DataClumpExplanationWindow extends SmellExplanationWindow{
	
	private DataClumpOverlay overlay;

	public DataClumpExplanationWindow(ClumpSpider inst, ISourceViewer sv) {
		super(sv.getTextWidget());
		overlay = new DataClumpOverlay(inst,sv);
		fillMain(sv.getTextWidget());
	}

	protected void fill(Composite parent) {

		parent.setLayout(new GridLayout(1,true));
		
		for(Map.Entry<ClumpGroup, Color> e : overlay.clumps().entrySet()){
			
			TranslucentComposite coloredParent = new TranslucentComposite(parent);
			coloredParent.setColor(e.getValue());
			
			TranslucentLabel l = new TranslucentLabel(coloredParent.composite());
			l.setColor(e.getValue());
			l.setText(e.getKey().toString());
			
			final TranslucentCombo list = new TranslucentCombo(coloredParent.composite());
			
			for(IMethod m : e.getKey().methods()){
				String name = m.getElementName();
				list.add(name);
				list.setData(name, m);
			}
			
			list.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					list.openMethod();
				}}
			);
		}
		
		
	}
	@Override
	protected String getText() {
		return "Data Clumps";
	}

	@Override
	protected SmellExplanationOverlay<?> getOverlay() {
		return overlay;
	}
	
	class TranslucentComposite extends TranslucentComponent{
		
		private Composite composite;
		
		public TranslucentComposite(Composite parent){
			composite = new Composite(parent,SWT.NONE);
			composite.setLayout(new GridLayout(1,true));
		}
		
		public Composite composite() {
			return composite;
		}

		public void setColor(Color color){
			Image icon = createIcon(composite,color);
			composite.setBackgroundImage(icon);
		}
	}
	
	class TranslucentCombo extends TranslucentComponent{
		
		private Combo combo;
		
		public TranslucentCombo(Composite parent){
			combo = new Combo(parent,SWT.MULTI | SWT.READ_ONLY);
		}
		
		public void openMethod() {
			IMethod m = (IMethod)combo.getData(combo.getText());
			if(m!=null)
				JDTUtils.openElementInEditor(m);
		}

		public String getText() {
			return combo.getText();
		}

		public void addSelectionListener(SelectionListener selectionListener) {
			combo.addSelectionListener(selectionListener);
		}

		public void setData(String name, IMethod m) {
			combo.setData(name,m);
		}

		public void add(String name) {
			combo.add(name);
		}

		public Composite composite() {
			return combo;
		}

		public void setColor(Color color){
			Image icon = createIcon(combo,color);
			combo.setBackgroundImage(icon);
		}
	}	
}
