package edu.pdx.cs.multiview.refactoringCues.refactorings;

import org.apache.commons.collections15.CollectionUtils;


public class RefactoringActionManager {

	private static final RefactoringAction<?>[] refactoringActions;
	
	static {
		refactoringActions = 
			new RefactoringAction[]{
				new RenameAction(),
				new MoveAction(),	
				new ChangeMethodSignatureAction(),	
				new ExtractMethodFromStatementsAction(),
				new ExtractMethodFromExpressionAction(),
				new ExtractTempAction(),
				new ExtractConstantAction(),
				new InlineTempAction(),
				new InlineMethodAction(),
				new ConvertAnonymousToNestedAction(),
				new ConvertMemberToTopLevelAction(),
				new ConvertTempToInstanceAction(),
				new ExtractSuperclassAction(),
				new ExtractInterfaceAction(),
				new UserSupertypeAction(),	
				new PushDownAction(),
				new PullUpAction(),
				new IntroduceIndirectionAction(),
				new IntroduceFactoryAction(),
				new IntroduceParameterObjectAction(),
				new IntroduceParameterAction(),				
				new EncapsulateFieldAction(),
				new GeneralizeTypeAction(),
				new InferGenericArgumentsAction()
		};
		CollectionUtils.reverseArray(refactoringActions);
	}

	public static RefactoringAction<?>[] getActions(){
		return refactoringActions;
	}

	public static RefactoringAction<?> executeOutstandingActions(){
		
		for(RefactoringAction<?> action : getActions()){
			if(action.isOutstanding()){
				action.executeRefactoring();
				action.deActivateSelectionCue();
				return action;
			}
		}
		
		return null;
	}

}
