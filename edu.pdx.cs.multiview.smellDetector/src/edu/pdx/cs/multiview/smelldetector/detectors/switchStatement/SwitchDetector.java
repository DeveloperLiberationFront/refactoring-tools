package edu.pdx.cs.multiview.smelldetector.detectors.switchStatement;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchStatement;

import edu.pdx.cs.multiview.jdt.util.ASTPool;
import edu.pdx.cs.multiview.jdt.util.JavaElementFinder;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class SwitchDetector extends SmellDetector<SwitchInstance>{

	public SwitchDetector(Flower f) {	super(f);	}
	
	@Override
	public double obviousness() {	return 0.93;		}

	@Override
	public SwitchInstance calculateComplexity(List<IMethod> visibleMethods) {
		
		final List<SwitchStatement> stmts = new ArrayList<SwitchStatement>();
		for (IMethod m : visibleMethods){
			
			MethodDeclaration node = getNode(m);
			node.accept(new ASTVisitor(){
				public boolean visit(SwitchStatement switchStmt){
					stmts.add(switchStmt);
					return true;
				}
			});
		}

		return new SwitchInstance(stmts);
	}


	@Override
	public String getName() {
		return "Switch Statement";
	}
	
	private static ASTPool<ICompilationUnit> astPool = ASTPool.getDefaultCU();
	
	public MethodDeclaration getNode(IMethod m) {		
		CompilationUnit icu = astPool.getAST(m.getCompilationUnit());
		return JavaElementFinder.findMethodDeclaration(m,icu);
	}

	@Override
	public void showDetails() {
		// TODO Auto-generated method stub
	}
}

class SwitchInstance implements SmellInstance{

	private List<SwitchStatement> switches;

	public SwitchInstance(List<SwitchStatement> stmts){
		this.switches = stmts;
	}
	
	public double magnitude() {
		
		double severity = 0;
		for(SwitchStatement stmt : switches){
			severity += stmt.statements().size();
		}
		
		return Math.log(severity) / (8 * SmellDetector.LOG2);
	}
	
}