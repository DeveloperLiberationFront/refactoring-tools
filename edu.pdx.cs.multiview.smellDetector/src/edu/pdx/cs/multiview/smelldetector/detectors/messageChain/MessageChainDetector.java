package edu.pdx.cs.multiview.smelldetector.detectors.messageChain;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.pdx.cs.multiview.jdt.util.ASTPool;
import edu.pdx.cs.multiview.jdt.util.JavaElementFinder;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellDetector;
import edu.pdx.cs.multiview.smelldetector.detectors.SmellInstance;
import edu.pdx.cs.multiview.smelldetector.ui.Flower;

public class MessageChainDetector extends SmellDetector<MessageChainInstance>{
	
	public MessageChainDetector(Flower f) {	super(f);	}
	
	@Override
	public double obviousness() {	return 0.89;		}

	@Override
	public MessageChainInstance calculateComplexity(List<IMethod> visibleMethods) {
		
		final List<MethodInvocation> stmts = new ArrayList<MethodInvocation>();
		for (IMethod m : visibleMethods){
			
			MethodDeclaration node = getNode(m);
			node.accept(new ASTVisitor(){
				public boolean visit(MethodInvocation inv){
					if(!(inv.getParent() instanceof MethodInvocation))
						stmts.add(inv);
					return true;
				}
			});
		}

		return new MessageChainInstance(stmts);
	}
	
	

	@Override
	public String getName() {
		return "Message Chain";
	}

	@Override
	public void showDetails() {
		//new FeatureEnvyExplanationWindow(currentSmell(),sourceViewer());
	}
	
	private static ASTPool<ICompilationUnit> astPool = ASTPool.getDefaultCU();
	
	public MethodDeclaration getNode(IMethod m) {		
		CompilationUnit icu = astPool.getAST(m.getCompilationUnit());
		return JavaElementFinder.findMethodDeclaration(m,icu);
	}
}


class MessageChainInstance implements SmellInstance{

	private List<MethodInvocation> invocations;

	public MessageChainInstance(List<MethodInvocation> invs){
		this.invocations = invs;
	}
	
	public double magnitude() {
		
		double severity = 0;
		for(MethodInvocation inv : invocations){
			double chainSize = sizeOf(inv);
			severity += chainSize < 2 ? 0 : chainSize;
		}
		
		return Math.log(severity) / (8 * SmellDetector.LOG2);
	}

	private double sizeOf(MethodInvocation inv) {
	
		if(inv.getExpression() instanceof MethodInvocation){
			return 1 + sizeOf((MethodInvocation)inv.getExpression());
		}
		
		return 1;
	}
}