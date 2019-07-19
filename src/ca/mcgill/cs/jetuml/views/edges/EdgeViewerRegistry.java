package ca.mcgill.cs.jetuml.views.edges;

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
		aRegistry.put(NoteEdge.class, EdgeViewerFactory.createNoteEdgeViewer());
		aRegistry.put(UseCaseAssociationEdge.class, EdgeViewerFactory.createUseCaseAssociationEdgeViewer());
		aRegistry.put(UseCaseGeneralizationEdge.class, EdgeViewerFactory.createUseCaseGeneralizationEdgeViewer());
		aRegistry.put(UseCaseDependencyEdge.class, EdgeViewerFactory.createUseCaseDependencyEdgeViewer());
		aRegistry.put(ObjectReferenceEdge.class, EdgeViewerFactory.createObjectReferenceEdgeViewer());
		aRegistry.put(ObjectCollaborationEdge.class, EdgeViewerFactory.createObjectCollaborationEdgeViewer());
		aRegistry.put(StateTransitionEdge.class, EdgeViewerFactory.createStateTransitionEdgeViewer());
		aRegistry.put(ReturnEdge.class, EdgeViewerFactory.createReturnEdgeViewer());
		aRegistry.put(CallEdge.class, EdgeViewerFactory.createCallEdgeViewer());
		aRegistry.put(DependencyEdge.class, EdgeViewerFactory.createDependencyEdgeViewer());
		aRegistry.put(AssociationEdge.class, EdgeViewerFactory.createAssociationEdgeViewer());
		aRegistry.put(GeneralizationEdge.class, EdgeViewerFactory.createGeneralizationEdgeViewer());
		aRegistry.put(AggregationEdge.class, EdgeViewerFactory.createAggregationEdgeViewer());
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
