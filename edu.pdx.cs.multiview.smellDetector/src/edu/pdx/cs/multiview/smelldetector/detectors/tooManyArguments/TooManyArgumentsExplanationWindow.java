package edu.pdx.cs.multiview.smelldetector.detectors.tooManyArguments;

import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationOverlay;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellExplanationWindow;

public class TooManyArgumentsExplanationWindow extends SmellExplanationWindow {

	private TooManyArgumentsOverlay overlay;

	public TooManyArgumentsExplanationWindow(TooManyArgumentsClassInstance inst, ISourceViewer sv) {
		super(sv.getTextWidget());
		overlay = new TooManyArgumentsOverlay(inst, sv);
		fillMain(sv.getTextWidget());
	}

	protected void fill(Composite parent) {
		parent.setLayout(new GridLayout(1, true));

		for (Map.Entry<IMethod, Integer> pair : overlay.getInstance().getMethodToNumberOfArguments().entrySet()) {

			Label label = new Label(parent, SWT.NONE);
			final IMethod method = pair.getKey();
			label.setText(method.getElementName() + " -> " + pair.getValue());
			label.setBackground(overlay.colorFor(method));
			label.addMouseListener(new MouseListener() {
				public void mouseDoubleClick(MouseEvent e) {
				}

				public void mouseDown(MouseEvent e) {
				}

				public void mouseUp(MouseEvent e) {
					JDTUtils.openElementInEditor(method);
				}
			});
		}

	}

	@Override
	protected String getText() {
		return TooManyArgumentsDetector.TOO_MANY_ARGUMENTS_LABEL_TEXT;
	}

	@Override
	protected SmellExplanationOverlay<?> getOverlay() {
		return overlay;
	}
}
