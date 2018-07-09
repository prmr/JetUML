package ca.mcgill.cs.jetuml.diagram.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundOperation implements DiagramOperation
{
	private List<SimpleOperation> aOperations = new ArrayList<>();
	
	public void add(SimpleOperation pOperation)
	{
		aOperations.add(pOperation);
	}

	@Override
	public void execute()
	{
		for( SimpleOperation operation : aOperations)
		{
			operation.execute();
		}
	}

	@Override
	public void undo()
	{
		ArrayList<SimpleOperation> reverse = new ArrayList<>(aOperations);
		Collections.reverse(reverse);
		for( SimpleOperation operation : reverse)
		{
			operation.undo();
		}
	}

}
