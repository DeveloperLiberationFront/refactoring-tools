package edu.pdx.cs.multiview.refactoring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class SampleStyledText {
  Display display = new Display();
  Shell shell = new Shell(display);
  
  StyledText styledText;

  public SampleStyledText() {
    init();
    
    shell.setLayout(new GridLayout());
    
    styledText = new StyledText(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    
    styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    Font font = new Font(shell.getDisplay(), "Courier New", 12, SWT.NORMAL);
    styledText.setFont(font);
    
    styledText.setText("123456789\r\nABCDEFGHIJKLM\nxxxxx");
    
    StyleRange styleRange1 = new StyleRange();
    styleRange1.start = 2;
    styleRange1.length = 16;
    styleRange1.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);  
    
    
    StyleRange styleRange2 = new StyleRange();
    styleRange2.start = 4;
    styleRange2.length = 14;
    styleRange2.background = shell.getDisplay().getSystemColor(SWT.COLOR_RED);
    
    styledText.setStyleRanges(new StyleRange[]{styleRange1, styleRange2});
    //styledText.setStyleRanges(new StyleRange[]{styleRange2, styleRange1});
    
    //styledText.setLineBackground(1, 1, shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
    
//    styledText.setSelection(4);
//    System.out.println(printStyleRanges(styledText.getStyleRanges()) );
//    styledText.insert("000");
    
    
//    styledText.setStyleRanges(new StyleRange[]{styleRange1});
//    System.out.println(printStyleRanges(styledText.getStyleRanges()) );
    
    //shell.pack();
    shell.setSize(300, 120);
    shell.open();
    //textUser.forceFocus();

    // Set up the event loop.
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        // If no more entries in event queue
        display.sleep();
      }
    }

    display.dispose();
  }
  
  private String printStyleRanges(StyleRange[] styleRanges) {
    
    if(styleRanges == null)
      return "null";
    else if(styleRanges.length == 0)
      return "[]";
    
    StringBuffer sb = new StringBuffer();
    for(int i=0; i<styleRanges.length; i++) {
      sb.append(styleRanges[i] + "\n");
    }
    
    return sb.toString();
  }

  private void init() {

  }

  public static void main(String[] args) {
    new SampleStyledText();
  }
}