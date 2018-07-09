package ca.mcgill.cs.jetuml.diagram.operations;

public interface DiagramOperation
{
	void execute();
	
	void undo();
}
