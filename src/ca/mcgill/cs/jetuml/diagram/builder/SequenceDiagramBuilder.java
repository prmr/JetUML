/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.SequenceDiagramEdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * A builder for sequence diagrams.
 */
public class SequenceDiagramBuilder extends DiagramBuilder
{
	private static final int CALL_NODE_YGAP = 5;
	
	/**
	 * Creates a new builder for sequence diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	public SequenceDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram instanceof SequenceDiagram;
	}
	
	@Override
	protected ConstraintSet getAdditionalEdgeConstraints(Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint)
	{
		return new ConstraintSet(
			EdgeConstraints.maxEdges(pEdge, pStart, pEnd, aDiagram, 1),
			SequenceDiagramEdgeConstraints.noEdgesFromParameterTop(pStart, pStartPoint),
			SequenceDiagramEdgeConstraints.returnEdge(pEdge, pStart, pEnd, aDiagram),
			SequenceDiagramEdgeConstraints.callEdgeEnd(pEdge, pEnd, pEndPoint),
			SequenceDiagramEdgeConstraints.singleEntryPoint(pEdge, pStart, aDiagram)
		);
	}
	
	private void addEdgeEndIfItHasNoCallees(List<DiagramElement> pElements, DiagramElement pTarget)
	{
		ControlFlow flow = new ControlFlow((SequenceDiagram)aDiagram);
		if( pTarget instanceof CallEdge && flow.hasNoCallees((CallNode)((Edge)pTarget).getEnd()))
		{
			Node end = ((Edge)pTarget).getEnd();
			pElements.add(end);
		}
	}

	private void addEdgeStartIfItHasNoOtherFlow(List<DiagramElement> pElements, DiagramElement pTarget)
	{
		ControlFlow flow = new ControlFlow((SequenceDiagram)aDiagram);
		if( pTarget instanceof CallEdge && flow.onlyConnectedToOneCall((CallNode)((Edge)pTarget).getStart(), (CallEdge) pTarget))
		{
			Node start = ((Edge)pTarget).getStart();
			pElements.add(start);
		}
	}
	
	private void addNodeCallerIfItHasNoOtherFlow(List<DiagramElement> pElements, DiagramElement pElement)
	{
		ControlFlow flow = new ControlFlow((SequenceDiagram)aDiagram);
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
		ControlFlow flow = new ControlFlow((SequenceDiagram)aDiagram);
		if( pElement instanceof CallNode )
		{
			for( Node callee : flow.getCallees((Node)pElement))
			{
				if( flow.hasNoCallees((CallNode)callee))
				{
					pElements.add(callee);
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
			Point pStartPoint, Point pEndPoint)
	{
		if( pEdge.getClass() != CallEdge.class )
		{
			super.completeEdgeAdditionOperation(pOperation, pEdge, pStartNode, pEndNode, pStartPoint, pEndPoint);
			return;
		}
		Node start = pStartNode;
		if( start.getClass() == ImplicitParameterNode.class )
		{
			CallNode newCallNode = new CallNode();
			ImplicitParameterNode parent = (ImplicitParameterNode) pStartNode;
			pOperation.add(new SimpleOperation(()-> parent.addChild(newCallNode),
					()-> parent.removeChild(newCallNode)));
			start = newCallNode;
		}
		ImplicitParameterNode endParent = null;
		if( pEndNode.getClass() == ImplicitParameterNode.class )
		{
			endParent = (ImplicitParameterNode) pEndNode;
		}
		else
		{
			assert pEndNode.getClass() == CallNode.class;
			endParent = (ImplicitParameterNode)((CallNode)pEndNode).getParent();
		}
		CallNode newCallNode = new CallNode();
		final ImplicitParameterNode parent = endParent;
		pOperation.add(new SimpleOperation(()-> parent.addChild(newCallNode),
				()-> parent.removeChild(newCallNode)));
		int insertionIndex = computeInsertionIndex(start, pStartPoint.getY());
		pEdge.connect(start, newCallNode, aDiagram);
		pOperation.add(new SimpleOperation(()-> aDiagram.addEdge(insertionIndex, pEdge),
				()-> aDiagram.removeEdge(pEdge)));
	}
	
	private int computeInsertionIndex( Node pCaller, int pY)
	{
		for( CallEdge callee : new ControlFlow((SequenceDiagram)aDiagram).getCalls(pCaller))
		{
			if( callee.view().getConnectionPoints().getY1() > pY )
			{
				return aDiagram.indexOf(callee);
			}
		}
		return aDiagram.numberOfEdges();
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
				result = new SimpleOperation(()-> target.addChild((ChildNode)pNode),
						()-> target.removeChild((ChildNode)pNode));
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
			if(node instanceof ImplicitParameterNode && node.view().contains(pPoint))
			{
				if( !(pPoint.getY() < ((ImplicitParameterNode)node).getTopRectangle().getMaxY() + CALL_NODE_YGAP))
				{
					return (ImplicitParameterNode) node;
				}
			}
		}
		return null;
	}
}
