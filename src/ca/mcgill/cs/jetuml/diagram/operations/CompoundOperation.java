package ca.mcgill.cs.jetuml.diagram.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundOperation implements DiagramOperation
{
	private List<DiagramOperation> aOperations = new ArrayList<>();
	
	public void add(DiagramOperation pOperation)
	{
		aOperations.add(pOperation);
	}

	@Override
	public void execute()
	{
		for( DiagramOperation operation : aOperations)
		{
			operation.execute();
		}
	}

	@Override
	public void undo()
	{
		ArrayList<DiagramOperation> reverse = new ArrayList<>(aOperations);
		Collections.reverse(reverse);
		for( DiagramOperation operation : reverse)
		{
			operation.undo();
		}
	}
}
