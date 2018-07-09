package ca.mcgill.cs.jetuml.diagram.operations;

public class SimpleOperation implements DiagramOperation
{
	private final Runnable aOperation;
	private final Runnable aReverse;
	
	public SimpleOperation(Runnable pOperation, Runnable pReverse)
	{
		assert pOperation != null && pReverse != null;
		aOperation = pOperation;
		aReverse = pReverse;
	}

	@Override
	public void execute()
	{
		aOperation.run();
	}

	@Override
	public void undo()
	{
		aReverse.run();
	}
}
