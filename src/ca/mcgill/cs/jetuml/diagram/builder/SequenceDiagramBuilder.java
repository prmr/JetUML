/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2019 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.diagram.builder;

import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.ControlFlow;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.SequenceDiagramEdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.ImplicitParameterNodeViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * A builder for sequence diagrams.
 */
public class SequenceDiagramBuilder extends DiagramBuilder
{
	private static final int CALL_NODE_YGAP = 5;
	private static final ImplicitParameterNodeViewer IMPLICIT_PARAMETER_NODE_VIEWER = new ImplicitParameterNodeViewer();
	
	/**
	 * Creates a new builder for sequence diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	public SequenceDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram.getType() == DiagramType.SEQUENCE;
	}
	
	@Override
	protected ConstraintSet getAdditionalEdgeConstraints(Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint)
	{
		ConstraintSet constraintSet = new ConstraintSet(
				EdgeConstraints.maxEdges(pEdge, pStart, pEnd, aDiagram, 1),
				SequenceDiagramEdgeConstraints.noEdgesFromParameterTop(pStart, pStartPoint),
				SequenceDiagramEdgeConstraints.returnEdge(pEdge, pStart, pEnd, aDiagram),
				SequenceDiagramEdgeConstraints.singleEntryPoint(pEdge, pStart, aDiagram),
				SequenceDiagramEdgeConstraints.constructorCall(pEdge, pEnd, aDiagram)
			);
		if( !canCreateConstructorCall(pStart, pEnd, pEndPoint) )
		{
			constraintSet.merge( new ConstraintSet(SequenceDiagramEdgeConstraints.callEdgeEnd(pEdge, pEnd, pEndPoint)) );
		}
		return constraintSet;
	}
	
	private void addEdgeEndIfItHasNoCallees(List<DiagramElement> pElements, DiagramElement pTarget)
	{
		ControlFlow flow = new ControlFlow(aDiagram);
		if( pTarget instanceof CallEdge && flow.hasNoCallees(((Edge)pTarget).getEnd()))
		{
			Node end = ((Edge)pTarget).getEnd();
			pElements.add(end);
			// If the target node is an implicit parameter node in a constructor call, remove its children
			if(flow.isConstructorCall(end))
			{
				addConstructorCallElements(pElements, end);
			}
		}
	}

	private void addConstructorCallElements(List<DiagramElement> pElements, Node pTarget)
	{
		ControlFlow flow = new ControlFlow(aDiagram);
		List<Node> children = ((ImplicitParameterNode)pTarget).getChildren();
		pElements.addAll(children);
		for(Node child: children)
		{
			Optional<Node> contructorNode = flow.getConstructorCall(child);
			if(contructorNode.isPresent())
			{
				addConstructorCallElements(pElements, contructorNode.get());
			}
			pElements.addAll(getCoRemovals(child));
		}
	}
	
	private void addEdgeStartIfItHasNoOtherFlow(List<DiagramElement> pElements, DiagramElement pTarget)
	{
		ControlFlow flow = new ControlFlow(aDiagram);
		if( pTarget instanceof CallEdge && flow.onlyConnectedToOneCall(((Edge)pTarget).getStart(), (CallEdge) pTarget))
		{
			Node start = ((Edge)pTarget).getStart();
			// Remove the start node only if the node is not in the constructor call 
			if(!flow.isInconstructorCall(start))
			{
				pElements.add(start);				
			}
		}
	}
	
	private void addNodeCallerIfItHasNoOtherFlow(List<DiagramElement> pElements, DiagramElement pElement)
	{
		ControlFlow flow = new ControlFlow(aDiagram);
		if( pElement instanceof CallNode )
		{
			Optional<CallNode> caller = flow.getCaller((Node)pElement);
			if( caller.isPresent() )
			{
				List<CallEdge> calls = flow.getCalls(caller.get());
				if( !flow.getCaller(caller.get()).isPresent() && calls.size() == 1 && calls.get(0).getEnd() == pElement )
				{
					pElements.add(caller.get());
				}
			}
		}
	}
	
	private void addNodeCalleesIfItHasNoOtherFlow(List<DiagramElement> pElements, DiagramElement pElement)
	{
		ControlFlow flow = new ControlFlow(aDiagram);
		if( pElement instanceof CallNode )
		{
			for( Node callee : flow.getCallees((Node)pElement))
			{
				if( flow.hasNoCallees(callee))
				{
					pElements.add(callee);
					if( flow.isConstructorCall(callee) )
					{
						addConstructorCallElements(pElements, callee);
					}
				}
			}
		}
	}
	
	private void addCorrespondingReturnEdge(List<DiagramElement> pElements, DiagramElement pElement)
	{
		if( pElement instanceof CallEdge )
		{
			Edge returnEdge = null;
			Edge input = (CallEdge) pElement;
			for( Edge edge : aDiagram.edges() )
			{
				if( edge instanceof ReturnEdge && edge.getStart() == input.getEnd() && edge.getEnd() == input.getStart())
				{
					returnEdge = edge;
					break;
				}
			}
			if( returnEdge != null )
			{
				final Edge target = returnEdge;
				pElements.add(target);
			}
		}
	}
	
	@Override
	protected List<DiagramElement> getCoRemovals(DiagramElement pElement)
	{
		List<DiagramElement> result = super.getCoRemovals(pElement);
		addEdgeEndIfItHasNoCallees(result, pElement);
		addEdgeStartIfItHasNoOtherFlow(result, pElement);
		addNodeCallerIfItHasNoOtherFlow(result, pElement);
		addCorrespondingReturnEdge(result, pElement);
		addNodeCalleesIfItHasNoOtherFlow(result, pElement);
		return result;
	}
	
	@Override
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		boolean result = true;
		if(pNode instanceof CallNode && insideTargetArea(pRequestedPosition) == null)
		{
			result = false;
		}
		return result;
	}
	
	@Override
	protected void completeEdgeAdditionOperation( CompoundOperation pOperation, Edge pEdge, Node pStartNode, Node pEndNode,
			Point pStartPoint, Point pEndPoint )
	{
		if( pEdge.getClass() != CallEdge.class )
		{
			super.completeEdgeAdditionOperation(pOperation, pEdge, pStartNode, pEndNode, pStartPoint, pEndPoint);
			return;
		}
		if( canCreateConstructorCall(pStartNode, pEndNode, pEndPoint) )
		{
			completeConstructorCallEdgeAddition(pOperation, pEdge, pStartNode, pEndNode, pStartPoint, pEndPoint);
		}
		else 
		{
			completeMethodCallEdgeAddition(pOperation, pEdge, pStartNode, pEndNode, pStartPoint, pEndPoint);
		}
	}
	
	private boolean canCreateConstructorCall(Node pStartNode, Node pEndNode, Point pEndPoint)
	{
		return pEndNode instanceof ImplicitParameterNode && IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(pEndNode).contains(pEndPoint)
				&& ((ImplicitParameterNode)pEndNode).getChildren().size()==0;
	}
	
	private void completeConstructorCallEdgeAddition(CompoundOperation pOperation, Edge pEdge, Node pStartNode, Node pEndNode,
			Point pStartPoint, Point pEndPoint)
	{
		assert canCreateConstructorCall(pStartNode, pEndNode, pEndPoint);
		Node startNode = pStartNode;
		if (pStartNode instanceof ImplicitParameterNode)
		{
			ImplicitParameterNode parent = (ImplicitParameterNode) pStartNode;
			CallNode newCallNode = new CallNode();
			pOperation.add(new SimpleOperation(() -> 
			{
				newCallNode.attach(aDiagram);
				parent.addChild(newCallNode);
			}, () -> 
			{
				newCallNode.detach();
				parent.removeChild(newCallNode);
			}));
			startNode = newCallNode;
		}
		final CallNode start = (CallNode)startNode;
		int insertionIndex = computeInsertionIndex(start, pStartPoint.getY());	
		CallNode endChild = new CallNode();
		ImplicitParameterNode end = (ImplicitParameterNode) pEndNode;
		pOperation.add(new SimpleOperation(() -> 
		{
			end.addChild(endChild);
			endChild.attach(aDiagram);
		}, ()-> 
		{
			end.removeChild(endChild);
			endChild.detach();
		}));
		pEdge.connect(start, end, aDiagram);
		((CallEdge)pEdge).setMiddleLabel("\u00ABcreate\u00BB");
		pOperation.add(new SimpleOperation(()-> aDiagram.addEdge(insertionIndex, pEdge),
				()-> aDiagram.removeEdge(pEdge)));
	}
	
	private void completeMethodCallEdgeAddition(CompoundOperation pOperation, Edge pEdge, Node pStartNode, Node pEndNode,
			Point pStartPoint, Point pEndPoint)
	{
		Node start = pStartNode;
		if( start.getClass() == ImplicitParameterNode.class )
		{
			CallNode newCallNode = new CallNode();
			ImplicitParameterNode parent = (ImplicitParameterNode) pStartNode;
			pOperation.add(new SimpleOperation(() -> 
			{
				newCallNode.attach(aDiagram);
				parent.addChild(newCallNode);
			}, () -> 
			{
				newCallNode.detach();
				parent.removeChild(newCallNode);
			}));
			start = newCallNode;
		}
		
		Node end = new CallNode();
		ImplicitParameterNode endParent = null;
		if (pEndNode.getClass() == ImplicitParameterNode.class)
		{
			endParent = (ImplicitParameterNode) pEndNode;
		}
		else 
		{
			assert pEndNode.getClass() == CallNode.class;
			endParent = (ImplicitParameterNode)((CallNode)pEndNode).getParent();
		}
		final ImplicitParameterNode parent = endParent;
		pOperation.add(new SimpleOperation(()-> 
		{
			end.attach(aDiagram);
			parent.addChild(end);
		},
		()-> 
		{
			end.detach();
			parent.removeChild(end);
		}
		));
		int insertionIndex = computeInsertionIndex(start, pStartPoint.getY());
		pEdge.connect(start, end, aDiagram);
		pOperation.add(new SimpleOperation(()-> aDiagram.addEdge(insertionIndex, pEdge),
				()-> aDiagram.removeEdge(pEdge)));
	}
	
	private int computeInsertionIndex( Node pCaller, int pY)
	{
		for( CallEdge callee : new ControlFlow(aDiagram).getCalls(pCaller))
		{
			if( EdgeViewerRegistry.getConnectionPoints(callee).getY1() > pY )
			{
				return aDiagram.indexOf(callee);
			}
		}
		return aDiagram.edges().size();
	}
	
	@Override
	public DiagramOperation createAddNodeOperation(Node pNode, Point pRequestedPosition)
	{
		DiagramOperation result = null;
		if(pNode instanceof CallNode) 
		{
			ImplicitParameterNode target = insideTargetArea(pRequestedPosition);
			if( target != null )
			{
				result = new SimpleOperation(()-> 
				{ 
					pNode.attach(aDiagram);
					target.addChild(pNode); 
				},
				()-> 
				{
					pNode.detach();
					target.removeChild(pNode);
				});
			}
		}
		if( result == null )
		{
			result = super.createAddNodeOperation(pNode, pRequestedPosition);
		}
		return result;
	}
	
	/*
	 * If pPoint is inside an ImplicitParameterNode but below its top
	 * rectangle, returns that node. Otherwise, returns null.
	 */
	private ImplicitParameterNode insideTargetArea(Point pPoint)
	{
		for( Node node : aDiagram.rootNodes() )
		{
			if(node instanceof ImplicitParameterNode && NodeViewerRegistry.contains(node, pPoint))
			{
				if( !(pPoint.getY() < IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(node).getMaxY() + CALL_NODE_YGAP))
				{
					return (ImplicitParameterNode) node;
				}
			}
		}
		return null;
	}
}
