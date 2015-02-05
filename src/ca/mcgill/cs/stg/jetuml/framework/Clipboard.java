package ca.mcgill.cs.stg.jetuml.framework;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.ObjectReferenceEdge;
import ca.mcgill.cs.stg.jetuml.graph.ReturnEdge;
import ca.mcgill.cs.stg.jetuml.graph.StateTransitionEdge;


/**
 * @author JoelChev
 * 
 * A class that will be used to store the current graph for cutting/copying and then pasting.
 *
 */
public final class Clipboard 
{
	private Object aContents;
	
	/**
	 * A constructor for a Clipboard object.
	 */
	public Clipboard() 
	{
	}
	
	/**
	 * @param pContents Sets the contents of the clipboard.
	 */
	public void setContents(Object pContents) 
	{
		aContents = pContents;
	}

	/**
	 * @return Gets the contents of the clipboard.
	 */
	public Object getContents() 
	{
		return aContents;
	}
	
	
		
}
