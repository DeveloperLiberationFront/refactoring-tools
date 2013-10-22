package edu.pdx.cs.multiview.smelldetector.detectors.featureEnvy;

import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.pdx.cs.multiview.jdt.util.MemberReference;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationOverlay;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationWindow;

public class FeatureEnvyExplanationWindow extends SmellExplanationWindow{
	
	private FeatureEnvyOverlay overlay;
	
	public FeatureEnvyExplanationWindow(EnvyInstance currentSmell,ISourceViewer sv) {		
		super(sv.getTextWidget());
		overlay = new FeatureEnvyOverlay(currentSmell,sv);
		fillMain(sv.getTextWidget());
	}

	@Override
	protected String getText() {
		return "Feature Envy";
	}

	@Override
	protected void fill(Composite parent) {		
		createMemberRefItem(parent);
	}
	
	private void createMemberRefItem(Composite parent) {
		
		ListComposite item = new ListComposite(parent,overlay.hasReadOnlyReferencedClasses());
		
		Label l;
		
		//add this refs		
		l = item.newClassPane("this",overlay.thisColor(),false);
		Set<MemberReference> thisRefs = overlay.uniqueThisReferences();
		if(thisRefs.isEmpty()){
			item.createParentLabel("<not referenced>");
		}else{
			addRefMouseListener(l,thisRefs.iterator().next(),true);
			for(MemberReference thisReference : thisRefs){
				l = item.createParentLabel(thisReference.toString());
				addRefMouseListener(l,thisReference,false);
			}
		}
		
		//add other refs
		for(Color c : overlay.memberColors()){
			SortedSet<MemberReferenceDuplicate> references = overlay.referencesForColor(c);
			MemberReference someRef = references.first();
			l = item.newClassPane(someRef.classToString(),c,someRef.refersToReadOnlyClass());
			addRefMouseListener(l,someRef,true);
			for(MemberReference ref : references){
				l = item.createParentLabel(ref.toString());
				addRefMouseListener(l,ref,false);
			}
		}
	}

	private void addRefMouseListener(final Label label, 
									final MemberReference ref,
									final boolean highlightAll){
		
		label.addMouseTrackListener(new MouseTrackListener(){

			public void mouseEnter(MouseEvent e) {
				label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				overlay.highlight(ref,highlightAll,true);
			}

			public void mouseExit(MouseEvent e) {
				label.setBackground(null);
				overlay.highlight(ref,highlightAll,false);
			}

			public void mouseHover(MouseEvent e) {}
			});
	}
	
	
	class ListComposite{
		
		private Composite modifiableClasses;
		private Composite readonlyClasses;
		
		private Composite classMembers;
		
		public ListComposite(Composite c, boolean hasReadOnlyClasses){				
			c.setLayout(gridLayout());			
			
			modifiableClasses = composite(c, 0);
			
			if(hasReadOnlyClasses){
				
				Label l = new Label(c,SWT.NONE);
				l.setAlignment(SWT.CENTER);
				l.setText("read-only classes");
				l.setForeground(new Color(null,150,150,150));
				
				readonlyClasses = composite(c, 0);
			}
		}		

		private GridLayout gridLayout() {
			GridLayout gl = new GridLayout(1,true);
			gl.verticalSpacing = 0;
			return gl;
		}
		
		private GridData gridData() {
			GridData gd = new GridData();
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			return gd;
		}
		
		public Label newClassPane(String className, Color color, boolean readOnly) {
			
			TranslucentComposite classContainer = 
				new TranslucentComposite(readOnly ? readonlyClasses : modifiableClasses);		
			classContainer.setColor(color);
			classContainer.composite.setLayoutData(gridData());
			
			GridLayout layout = gridLayout();
			layout.marginHeight = layout.marginWidth = 1;
			layout.verticalSpacing = 2;
			classContainer.composite.setLayout(layout);
			
			Label classLabel = createLabel(className,classContainer.composite);			
			
			classMembers = composite(classContainer.composite, 5);
			
			return classLabel;
		}
		
		public Label createParentLabel(String text) {			
			return createLabel(text, classMembers);
		}

		protected Composite composite(Composite parent, int indent){
			Composite composite = new Composite(parent,SWT.NONE);
			GridLayout layout = gridLayout();
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0 ;
			layout.marginLeft = indent;
			composite.setLayout(layout);
			composite.setLayoutData(gridData());
			return composite;
		}
		
		private Label createLabel(String text, Composite parent) {
			Label label = new Label(parent,SWT.NONE);
			label.setText(text);
			label.setLayoutData(gridData());
			return label;
		}
	}

	@Override
	protected SmellExplanationOverlay<?> getOverlay() {
		return overlay;
	}
}
