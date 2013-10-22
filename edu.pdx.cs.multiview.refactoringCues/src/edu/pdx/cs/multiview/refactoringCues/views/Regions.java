package edu.pdx.cs.multiview.refactoringCues.views;

import org.eclipse.jface.text.ITextSelection;

public interface Regions extends Iterable<ASTRegion>{

	public abstract Regions withOnlyActive();

	public abstract void setSelectedIn(ITextSelection selection);

	public abstract ASTRegion first();

	public abstract ASTRegion last();

	public abstract boolean isEmpty();
	
}