package edu.pdx.cs.multiview.refactoring.piemenu;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.dom.Selection;
import org.eclipse.jdt.internal.corext.dom.SelectionAnalyzer;
import org.eclipse.jdt.internal.corext.refactoring.code.ConvertAnonymousToNestedRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractTempRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineTempRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.IntroduceFactoryRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.IntroduceIndirectionRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.PromoteTempToFieldRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.sef.SelfEncapsulateFieldRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInnerToTopRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.structure.PullUpRefactoringProcessor;
import org.eclipse.jdt.internal.corext.refactoring.structure.PushDownRefactoringProcessor;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;

import edu.pdx.cs.multiview.jdt.util.JDTUtils;
import edu.pdx.cs.multiview.jface.text.LinkModeManager;
import edu.pdx.cs.multiview.jface.text.RefactoringBundle;
import edu.pdx.cs.multiview.jface.text.WrappedEditor;
import edu.pdx.cs.multiview.refactoring.piemenu.customRefactorings.ChangeVisibilityRefactoring;
import edu.pdx.cs.multiview.refactoring.piemenu.customRefactorings.ConvertNestedToAnonymousRefactoring;
import edu.pdx.cs.multiview.swt.pieMenu.IPieMenuBuilder;
import edu.pdx.cs.multiview.swt.pieMenu.PieMenu;
import edu.pdx.cs.multiview.swt.pieMenu.SelectionListener;
import edu.pdx.cs.multiview.ui.ProgressMonitorUtil;

@SuppressWarnings("restriction")
public class PieMenuBuilder implements IPieMenuBuilder {

	//TODO: should have functionality to take in-file class
	//		and put it into a new file (not a current refactoring
	//		methinks
	
	protected JavaEditor editor;
	protected PieMenu menu;
	protected ICompilationUnit icu;
	protected CompilationUnit cu;

	public void setEditor(JavaEditor editor) {
		
		if(this.editor!=editor){
			this.editor = editor;
			icu = JDTUtils.getCompilationUnit(editor);
		}
	}

	public void build(PieMenu menu) {
		
		this.menu = menu;
		cu = ASTProvider.getASTProvider().getAST(icu, SharedASTProvider.WAIT_YES, null);
		
		try {
			initPieMenu(getNode());	
		} catch (JavaModelException e) {
			e.printStackTrace();
		}	
	}
	
	private ASTNode[] getNode() throws JavaModelException {
		
		ITextSelection selection = (ITextSelection)editor.getSelectionProvider().getSelection();
		
		SelectionAnalyzer analyzer = new SelectionAnalyzer(
				Selection.createFromStartLength(selection.getOffset(),
						selection.getLength()),true);
		cu.accept(analyzer);
		
		return analyzer.getSelectedNodes();
	}
	
	private void initPieMenu(ASTNode selectedNodes[]){		
		fillPieMenu(selectedNodes);
	}
	
	private void fillPieMenu(ASTNode nodes[]) {
		
		if(nodes.length == 0){
			System.out.println("No nodes selected");
		}else if(nodes.length==1){
			fillForOneNode(nodes[0]);
		}else{
			fillForStatement(nodes);
		}
	}

	private void fillForOneNode(ASTNode node) {
		if(node instanceof SimpleName){
			SimpleName name = (SimpleName)node;
			IBinding b = name.resolveBinding();
			if(b instanceof IVariableBinding){
				if(((IVariableBinding)b).isField()){
					fillFieldMenu(name,(IVariableBinding)b);
				}else{
					fillTempMenu((VariableDeclaration) node.getParent());
				}
			}else if(b instanceof IMethodBinding){
				fillMethodMenu(node,(IMethodBinding)b);
			}else if(b instanceof ITypeBinding){				
				ITypeBinding binding = (ITypeBinding)b;				
				if(binding.isNested()){
					fillNestedClassMenu((TypeDeclaration)node.getParent(), binding);
				}
			}
		}else if(node instanceof Statement){
			fillForStatement(new ASTNode[] {node});
		}else if(node instanceof Modifier){
			Modifier modifier = (Modifier)node;
			if(modifier.isPublic() || modifier.isPrivate() || modifier.isProtected())
				fillForModifier(modifier);
		}else if(node instanceof Expression){
			fillForExpression((Expression)node);
		}else if(node instanceof VariableDeclaration){
			VariableDeclaration decl = (VariableDeclaration)node;
			fillFieldMenu(decl.getName(),decl.resolveBinding());
		}else if(node instanceof SimpleType){
			ClassInstanceCreation t = (ClassInstanceCreation)((SimpleType)node).getParent();		
			if(isAnonymousDeclaration(t)){
				fillAnonymousClassMenu(t);
			}
		}
	}

	private void fillNestedClassMenu(final TypeDeclaration typeNode,
			final ITypeBinding binding) {
		addNull();
		menu.add("Convert to\nAnonymous").addSelectionListener(new PieMenuRefactoring(){

			@Override
			protected RefactoringBundle getRefactoring() throws Exception {
				
				IType type = (IType)binding.getJavaElement();
				ConvertNestedToAnonymousRefactoring r = 
					new ConvertNestedToAnonymousRefactoring(typeNode,type);
				
				return new RefactoringBundle(r);
			}
			
		});
		addNull();
		menu.add("Convert to\nTop Level").addSelectionListener(new PieMenuRefactoring(){

			@Override
			protected RefactoringBundle getRefactoring() throws Exception {
				
				IType type = (IType)binding.getJavaElement();
				CodeGenerationSettings settings = JavaPreferencesSettings.
							getCodeGenerationSettings(type.getJavaProject());
				
				MoveInnerToTopRefactoring r = new MoveInnerToTopRefactoring(
						type,settings);
				
				return new RefactoringBundle(r);
			}
			
		});
	}

	private void fillAnonymousClassMenu(final ClassInstanceCreation t) {
		addNull();
		addNull();
		addNull();
		menu.add("Convert to\nNested").addSelectionListener(new PieMenuRefactoring(){

			@Override
			protected RefactoringBundle getRefactoring()
							throws Exception {
				
				ConvertAnonymousToNestedRefactoring r = 
					new ConvertAnonymousToNestedRefactoring(icu,
							t.getStartPosition(),t.getLength());
				
				RefactoringBundle b = new RefactoringBundle(r);
				String name = b.generateClassName(icu.getSource());
				r.setClassName(name);						
				return b;
			}
			
		});
	}

	private boolean isAnonymousDeclaration(ClassInstanceCreation instCreator) {
		return instCreator.getAnonymousClassDeclaration()!=null;
	}

	protected void fillForExpression(final Expression node) {
		addNull();
		addNull();
		addNull();
		menu.add("Extract Local\nVariable").addSelectionListener(new ExtractLocalVariable(node));
	}

	protected void fillForModifier(final Modifier node) {
		
		menu.addNull();
		menu.add("Reduce\nVisibility").addSelectionListener(new VisibilityChanger(node){
			@Override
			public ModifierKeyword getNewModifier() {
				return node.isPublic() ? 
						ModifierKeyword.PROTECTED_KEYWORD :
						ModifierKeyword.PRIVATE_KEYWORD;
			}
		});
		
		menu.addNull();
		menu.add("Increase\nVisibility").addSelectionListener(new VisibilityChanger(node){
			@Override
			public ModifierKeyword getNewModifier() {
				return node.isPrivate() ?
						ModifierKeyword.PROTECTED_KEYWORD :
						ModifierKeyword.PUBLIC_KEYWORD;
			}
		});
	}
	
	protected void fillFieldMenu(SimpleName name, IVariableBinding binding) {
		addPullUp(binding);
		addNull();
		addPushDown(binding);
		addEncapsulate(binding);
	}

	protected void fillTempMenu(VariableDeclaration parent) {

		addNull();
		addInlineLocal(parent);
		addNull();	
		addToInstance(parent);
	}

	protected void fillMethodMenu(final ASTNode node, final IMethodBinding binding) {
		if(binding.isConstructor()){
			addNull();
			addNull();			
			addNull();
			addFactory(node,binding);
		}else{
			addPullUp(binding);		
			addInlineMethod(node);
			addPushDown(binding);		
			addIndirection(binding);
		}
	}

	protected void fillForStatement(final ASTNode[] nodes) {

		addNull();
		addNull();
		addNull();
		menu.add("Extract\nMethod").addSelectionListener(new ExtractMethod(nodes));
	}
	

	protected void addNull() {
		menu.addNull();
	}

	protected void addPullUp(IMethodBinding binding) {
		addPullUp("Method",binding);
	}
	
	protected void addPullUp(IVariableBinding binding) {
		addPullUp("Field",binding);
	}
	
	protected void addPullUp(String end, IBinding binding) {
		menu.add("Pull Up\n" + end).addSelectionListener(new PullUp(binding));
	}
	
	protected void addPushDown(IMethodBinding binding) {
		addPushDown("Method",binding);
	}
	
	protected void addPushDown(IVariableBinding binding) {
		addPushDown("Field",binding);
	}

	protected void addPushDown(String end, IBinding binding) {
		menu.add("Push Down\n"+end).addSelectionListener(new PushDown(binding));
	}

	protected void addToInstance(VariableDeclaration parent) {
		menu.add("Convert to\nField").addSelectionListener(new ConvertToInstance(parent));
	}

	protected void addEncapsulate(IVariableBinding binding) {
		menu.add("Encapsulate\nField").addSelectionListener(new EncapsulateField(binding));
	}

	protected void addInlineLocal(VariableDeclaration parent) {
		menu.add("Inline\nLocal").addSelectionListener(new InlineLocal(parent));
	}
	
	protected void addInlineMethod(ASTNode node) {
		menu.add("Inline\nMethod").addSelectionListener(new InlineMethod(node));
	}

	protected void addIndirection(IMethodBinding binding) {
		menu.add("Introduce\nIndirection").addSelectionListener(new IntroduceIndirection(binding));
	}

	protected void addFactory(final ASTNode node, IMethodBinding binding){
		menu.add("Introduce\nFactory").addSelectionListener(
				new PieMenuRefactoring(){

					@Override
					protected RefactoringBundle getRefactoring()
							throws Exception {
						
						IntroduceFactoryRefactoring r = 
							new IntroduceFactoryRefactoring(icu,node.getStartPosition(),node.getLength());
						
						r.checkInitialConditions(new NullProgressMonitor());
						//TODO: this shouldn't be necessary, should it
						r.setFactoryClass(icu.findPrimaryType().getFullyQualifiedName());
						
						//TODO: this is a global change and so needs the global rename
						RefactoringBundle b = new RefactoringBundle(r);
						String name = b.generateIdName(icu.getSource());
						r.setNewMethodName(name);						
						return b;
					}

		});
	}
	

	public abstract class PieMenuRefactoring implements SelectionListener{	
		private void doRefactoring(Refactoring r) {
			
			IStatusLineManager manager  = ProgressMonitorUtil.getStatusLineManager(editor);
			IProgressMonitor pm = manager.getProgressMonitor(); 
				
			pm.beginTask("Executing Refactoring", 3);
			try {
				
				RefactoringStatus conditions = r.checkAllConditions(inc(pm));
				
				if(!conditions.isOK())
					throw new Exception(conditions.getEntries()[0].getMessage());
				
				r.createChange(inc(pm)).perform(inc(pm));
			} catch (Exception e) {
				manager.setErrorMessage(e.getMessage());
			} finally {
				pm.done(); 
			}		
		}

		private SubProgressMonitor inc(IProgressMonitor pm) {
			return new SubProgressMonitor(pm,1);
		}
		
		public final void itemSelected() {
			try {
				RefactoringBundle rb = getRefactoring();
				doRefactoring(rb.getRefactoring());
				 
				LinkModeManager links = new LinkModeManager(new WrappedEditor(editor));
				links.add(rb);
				links.activateLinks();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		protected abstract RefactoringBundle getRefactoring() throws Exception;
	}
	
	public final class ExtractLocalVariable extends PieMenuRefactoring{
		private final Expression node;

		private ExtractLocalVariable(Expression node) {
			this.node = node;
		}

		public RefactoringBundle getRefactoring() throws Exception{
			
			
			
			ExtractTempRefactoring r = new ExtractTempRefactoring(
												(CompilationUnit) node.getRoot(),
												node.getStartPosition(),
												node.getLength());
			RefactoringBundle b = new RefactoringBundle(r);
			String name = b.generateIdName(icu.getSource());			
			r.setTempName(name);
			return b;
		}
	}

	public final class ExtractMethod extends PieMenuRefactoring {
		private final ASTNode[] nodes;

		private ExtractMethod(ASTNode[] nodes) {
			this.nodes = nodes;
		}

		protected RefactoringBundle getRefactoring() throws Exception{
			int start = nodes[0].getStartPosition();
			ASTNode last = nodes[nodes.length-1];
			int length = (last.getStartPosition()+last.getLength()) - start;
			
			ExtractMethodRefactoring r = new ExtractMethodRefactoring(icu,start,length);
			RefactoringBundle b = new RefactoringBundle(r);
			String name = b.generateIdName(icu.getSource());			
			r.setMethodName(name);
			return b;
			
		}
	}

	public final class ConvertToInstance extends PieMenuRefactoring {
		private final VariableDeclaration parent;
	
		public ConvertToInstance(VariableDeclaration parent) {
			this.parent = parent;
		}
	
		public RefactoringBundle getRefactoring() throws Exception{
			PromoteTempToFieldRefactoring r = new PromoteTempToFieldRefactoring(parent);
			
			RefactoringBundle b = new RefactoringBundle(r);
			//TODO: name should be same as old temp name
			String name = b.generateIdName(icu.getSource());			
			r.setFieldName(name);
			return b;
		}
	}

	public final class InlineLocal extends PieMenuRefactoring {
		private final VariableDeclaration parent;
	
		public InlineLocal(VariableDeclaration parent) {
			this.parent = parent;
		}
	
		public RefactoringBundle getRefactoring() {
			return new RefactoringBundle(new InlineTempRefactoring(parent));
		}
	}

	public abstract class VisibilityChanger extends PieMenuRefactoring {
		
		private final Modifier node;
		private VisibilityChanger(Modifier node) {
			this.node = node;
		}
	
		public RefactoringBundle getRefactoring() {
			return new RefactoringBundle(new ChangeVisibilityRefactoring(
					icu,node,getNewModifier()));
		}
	
		public abstract ModifierKeyword getNewModifier();
	}

	public final class IntroduceIndirection extends PieMenuRefactoring {
		private final IMethodBinding binding;
	
		public IntroduceIndirection(IMethodBinding binding) {
			this.binding = binding;
		}
	
		public RefactoringBundle getRefactoring() throws JavaModelException {
			//TODO: I believe you can do this without calling internal classes:
			//			see Calling Refactorings Programmatically on jdt newsgroup
			IntroduceIndirectionRefactoring r = new IntroduceIndirectionRefactoring(
					((IMethod)binding.getJavaElement()));

			RefactoringBundle b = new RefactoringBundle(r);
			String name = b.generateIdName(icu.getSource());			
			r.setIntermediaryMethodName(name);
			return b;
		}
	}

	public final class PushDown extends PieMenuRefactoring {
		private final IBinding binding;
	
		public PushDown(IBinding binding) {
			this.binding = binding;
		}
	
		public RefactoringBundle getRefactoring() {
			IMember[] members = new IMember[] {((IMember)binding.getJavaElement())};
			PushDownRefactoringProcessor proc = new PushDownRefactoringProcessor(members);
			Refactoring r = new ProcessorBasedRefactoring(proc);			
			return new RefactoringBundle(r);
		}
	}

	public final class InlineMethod extends PieMenuRefactoring {
		private final ASTNode node;
	
		public InlineMethod(ASTNode node) {
			this.node = node;
		}
	
		public RefactoringBundle getRefactoring() {
			ITypeRoot typeRoot= SelectionConverter.getInput(editor);
			CompilationUnit root = (CompilationUnit) node.getRoot();
			InlineMethodRefactoring r = InlineMethodRefactoring.create(typeRoot,root,
					node.getStartPosition(),node.getLength());
			r.setDeleteSource(true);
			return new RefactoringBundle(r);
		}
	}

	public class PullUp extends PieMenuRefactoring {
		
		private final IBinding binding;
	
		public PullUp(IBinding binding) {
			this.binding = binding;
		}
	
		public RefactoringBundle getRefactoring() throws JavaModelException {
			IMember m = (IMember)binding.getJavaElement();
			CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(m.getJavaProject());
			IMember[] members = new IMember[] {m};
			
			PullUpRefactoringProcessor proc= new PullUpRefactoringProcessor(members, settings);
			Refactoring r = new ProcessorBasedRefactoring(proc);
			
			IType[] candidateTypes = proc.getCandidateTypes(new RefactoringStatus(), 
															new NullProgressMonitor());
			proc.setDestinationType(candidateTypes[candidateTypes.length-1]);
			if(m instanceof IMethod)
				proc.setDeletedMethods(new IMethod[] {(IMethod)m});
			return new RefactoringBundle(r);
		
		}
	}

	protected final class EncapsulateField extends PieMenuRefactoring {
		private final IVariableBinding binding;
	
		public EncapsulateField(IVariableBinding binding) {
			this.binding = binding;
		}
	
		@Override
		public RefactoringBundle getRefactoring() throws JavaModelException {
			IField field = (IField) binding.getJavaElement();
			SelfEncapsulateFieldRefactoring r = 
				new SelfEncapsulateFieldRefactoring(field);
			RefactoringBundle b = new RefactoringBundle(r);
			
			//TODO: it would be nice to have the refactoring choose a name
			//		I know it can, just haven't gotten it to work
			String name = b.generateIdName(icu.getSource());			
			r.setGetterName("get"+name);
			r.setSetterName("set"+name);
			
			return b;	
		}
	}

}
