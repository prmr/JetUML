package ca.mcgill.cs.jetuml.viewers.edges;

import java.util.IdentityHashMap;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;

/**
 * Keeps track of the association between and edge type and the viewer
 * that needs to be used to view it.
 */
public final class EdgeViewerRegistry
{	
	public static final EdgeViewerRegistry EDGE_VIEWER_REGISTRY = new EdgeViewerRegistry();
	
	private IdentityHashMap<Class<? extends Edge>, EdgeViewer> aRegistry = 
			new IdentityHashMap<>();
	
	private EdgeViewerRegistry() 
	{
		aRegistry.put(NoteEdge.class, new NoteEdgeViewer());
		aRegistry.put(UseCaseAssociationEdge.class, new UseCaseAssociationEdgeViewer());
		aRegistry.put(UseCaseGeneralizationEdge.class, new UseCaseGeneralizationEdgeViewer());
		aRegistry.put(UseCaseDependencyEdge.class, new UseCaseDependencyEdgeViewer());
		aRegistry.put(ObjectReferenceEdge.class, new ObjectReferenceEdgeViewer());
		aRegistry.put(ObjectCollaborationEdge.class, new ObjectCollaborationEdgeViewer());
		aRegistry.put(StateTransitionEdge.class, new StateTransitionEdgeViewer());
		aRegistry.put(ReturnEdge.class, new ReturnEdgeViewer());
		aRegistry.put(CallEdge.class, new CallEdgeViewer());
		aRegistry.put(DependencyEdge.class, new DependencyEdgeViewer());
		aRegistry.put(AssociationEdge.class,  new AssociationEdgeViewer());
		aRegistry.put(GeneralizationEdge.class, new GeneralizationEdgeViewer());
		aRegistry.put(AggregationEdge.class, new AggregationEdgeViewer());
	}
	
	/**
	 * @param pEdge The edge to view.
	 * @return A viewer for pEdge
	 * @pre pEdge != null;
	 */
	public EdgeViewer viewerFor(Edge pEdge)
	{
		assert pEdge != null && aRegistry.containsKey(pEdge.getClass());
		return aRegistry.get(pEdge.getClass());
	}
}
