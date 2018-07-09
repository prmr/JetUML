package ca.mcgill.cs.jetuml.diagram.builder;

import java.util.ArrayList;
import java.util.List;

public class DiagramOperationProcessor
{
	private final List<DiagramOperation> aExecutedOperations = new ArrayList<>();
	private final List<DiagramOperation> aUndoneOperations = new ArrayList<>();
	
	public void executeNewOperation(DiagramOperation pOperation)
	{
		pOperation.execute();
		aExecutedOperations.add(pOperation);
	}
	
	public void undoLastExecutedOperation()
	{
		assert canUndo();
		DiagramOperation operation = aExecutedOperations.remove(aExecutedOperations.size() - 1);
		operation.undo();
		aUndoneOperations.add(operation);
	}
	
	public void redoLastUndoneOperation()
	{
		assert canRedo();
		DiagramOperation operation = aUndoneOperations.remove(aUndoneOperations.size() - 1);
		operation.execute();
		aExecutedOperations.add(operation);
	}

	public boolean canUndo()
	{
		return !aExecutedOperations.isEmpty();
	}
	
	public boolean canRedo()
	{
		return !aUndoneOperations.isEmpty();
	}
}
