/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.ControlFlowGraph;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a call node in a Sequence diagram.
 */
public class CallNodeView extends AbstractNodeView
{
	private static final int WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	private static final int Y_GAP_BIG = 20;
	private static final int Y_GAP_SMALL = 10;
	
	private SequenceDiagram aDiagram = null;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public CallNodeView(CallNode pNode)
	{
		super(pNode);
	}
	
	/**
	 * @param pDiagram The diagram to set.
	 * @pre pDiagram != null
	 */
	public void setDiagram(SequenceDiagram pDiagram)
	{
		aDiagram = pDiagram;
	}
	
	@Override
	protected CallNode node()
	{
		return (CallNode)super.node();
	}
	
	private boolean openBottom()
	{
		return node().isOpenBottom();
	}
	
	private ImplicitParameterNode implicitParameter()
	{
		return (ImplicitParameterNode)node().getParent();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		if(openBottom())
		{
			final Rectangle bounds = getBounds();
			int x1 = bounds.getX();
			int x2 = bounds.getMaxX();
			int y1 = bounds.getY();
			int y3 = bounds.getMaxY();
			int y2 = y3 - CallNode.CALL_YGAP;
			ViewUtils.drawLine(pGraphics, x1, y1, x2, y1, LineStyle.SOLID);
			ViewUtils.drawLine(pGraphics, x1, y1, x1, y2, LineStyle.SOLID);
			ViewUtils.drawLine(pGraphics, x2, y1, x2, y2, LineStyle.SOLID);
			ViewUtils.drawLine(pGraphics, x1, y2, x1, y3, LineStyle.DOTTED);
			ViewUtils.drawLine(pGraphics, x2, y2, x2, y3, LineStyle.DOTTED);
		}
		else
		{
			ViewUtils.drawRectangle(pGraphics, getBounds());
			
		}
	}
	
	/*
	 * @param pGraph
	 * @return All the nodes (CallNodes or ImplicitParameterNodes) that have a calledge
	 * originating at this CallNode. If an ImplicitParameterNode is in the list, it's always
	 * returned first.
	 */
	private List<Node> getCallees(Diagram pGraph)
	{
		List<Node> callees = new ArrayList<>();
		for( Edge edge : pGraph.edges())
		{
			if( edge.getStart() == node() && edge instanceof CallEdge )
			{
				callees.add(edge.getEnd());
			}
		}
		return callees;
	}

	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		if(pDirection.getX() > 0)
		{
			return new Point(getBounds().getMaxX(), getBounds().getY());
		}
		else
		{
			return new Point(getBounds().getX(), getBounds().getY());
		}
	}
	
	/*
	 * The x position is a function of the position of the implicit parameter
	 * node and the nesting depth of the call node.
	 */
	private int getX()
	{
		if(implicitParameter() != null )
		{
			int depth = 0;
			if( aDiagram != null )
			{
				depth = new ControlFlowGraph(aDiagram).getNestingDepth(node());
			}
			return ((ImplicitParameterNodeView)implicitParameter().view()).getTopRectangle().getCenter().getX() -
					WIDTH / 2 + depth * WIDTH/2;
		}
		else
		{
			return 0;
		}
	}
	
	/*
	 * If the node has a caller, the Y coordinate is a gap below the last return Y value
	 * of the caller. If not, it's simply a set distance below the previous call node.
	 * CSOFF:
	 */
	private int getY()
	{
		Optional<CallNode> caller = Optional.empty();
		if( aDiagram != null )
		{
			ControlFlowGraph flow = new ControlFlowGraph(aDiagram);
			caller = flow.getCaller(node());
		}
		if( !caller.isPresent() )
		{
			Optional<CallNode> previous = getPreviousCallNode();
			if( previous.isPresent() )
			{
				return ((CallNodeView)previous.get().view()).getMaxY() + Y_GAP_BIG;
			}
			else if( implicitParameter() != null )
			{
				return ((ImplicitParameterNodeView)implicitParameter().view()).getTopRectangle().getMaxY() + Y_GAP_BIG;
			}
			else
			{
				return 0; // Only used for the image creator
			}
		}
		else // there are four cases here: nested call (y/n) and first callee (y/n) 
		{
			ControlFlowGraph flow = new ControlFlowGraph(aDiagram);
			if( flow.isNested(node()) && flow.isFirstCallee(node()))
			{
				return ((CallNodeView)caller.get().view()).getY() + Y_GAP_BIG;
			}
			if( flow.isNested(node()) && !flow.isFirstCallee(node()) )
			{
				return ((CallNodeView)flow.getPreviousCallee(node()).view()).getMaxY() + Y_GAP_SMALL;
			}
			if( !flow.isNested(node()) && flow.isFirstCallee(node()) )
			{
				return ((CallNodeView)caller.get().view()).getY() + Y_GAP_SMALL;
			}
			else
			{
				return ((CallNodeView)flow.getPreviousCallee(node()).view()).getMaxY() + Y_GAP_SMALL;
			}
		}
	} // CSON:
	
	/**
	 * @return If there's no callee, returns a fixed offset from the y position.
	 * Otherwise, return with a gap from last callee.
	 */
	public int getMaxY()
	{
		List<Node> callees = new ArrayList<>();
		if( aDiagram != null )
		{
			callees = getCallees(aDiagram);
		}
		if( callees.isEmpty() )
		{
			return getY() + DEFAULT_HEIGHT;
		}
		else
		{
			return ((CallNodeView)callees.get(callees.size()-1).view()).getMaxY() + Y_GAP_SMALL;
		}
	}
	
	private Optional<CallNode> getPreviousCallNode()
	{
		List<ChildNode> callNodes = new ArrayList<>();
		if( implicitParameter() != null )
		{
			callNodes = implicitParameter().getChildren();
		}
		if( callNodes.size() < 2 )
		{
			return Optional.empty();
		}
		if( callNodes.get(0) == node())
		{
			return Optional.empty();
		}
		CallNode previous = (CallNode) callNodes.get(0);
		for(int i = 1; i < callNodes.size(); i++ )
		{
			CallNode current = (CallNode)callNodes.get(i);
			if( current == node())
			{
				break;
			}
			previous = current;
		}
		return Optional.of(previous);
	}

	@Override
	public Rectangle getBounds()
	{
		int y = getY();
		return new Rectangle(getX(), y, WIDTH, getMaxY() - y);
	}
	
}
