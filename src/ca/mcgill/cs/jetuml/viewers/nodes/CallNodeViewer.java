/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.ControlFlow;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ViewerUtils;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a call node in a Sequence diagram.
 */
public final class CallNodeViewer extends AbstractNodeViewer
{
	private static final int WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	private static final int Y_GAP_BIG = 20;
	private static final int Y_GAP_SMALL = 20; // Was 10, changed to 20 to account for label space
	private static final int Y_GAP_TINY = 5; // Was 10, changed to 20 to account for label space
	private static final ImplicitParameterNodeViewer IMPLICIT_PARAMETER_NODE_VIEWER = new ImplicitParameterNodeViewer();
	// Inserts gaps between call nodes so that call edge labels don't intersect
	private static final StringViewer NODE_GAP_TESTER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	private static final String TEST_STRING = "|";
	private static final int MINIMUM_SHIFT_THRESHOLD = 10;
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		if(((CallNode)pNode).isOpenBottom())
		{
			pGraphics.setStroke(Color.WHITE);
			ViewerUtils.drawRectangle(pGraphics, getBounds(pNode));
			pGraphics.setStroke(Color.BLACK);
			final Rectangle bounds = getBounds(pNode);
			int x1 = bounds.getX();
			int x2 = bounds.getMaxX();
			int y1 = bounds.getY();
			int y3 = bounds.getMaxY();
			int y2 = y3 - CallNode.CALL_YGAP;
			ViewerUtils.drawLine(pGraphics, x1, y1, x2, y1, LineStyle.SOLID);
			ViewerUtils.drawLine(pGraphics, x1, y1, x1, y2, LineStyle.SOLID);
			ViewerUtils.drawLine(pGraphics, x2, y1, x2, y2, LineStyle.SOLID);
			ViewerUtils.drawLine(pGraphics, x1, y2, x1, y3, LineStyle.DOTTED);
			ViewerUtils.drawLine(pGraphics, x2, y2, x2, y3, LineStyle.DOTTED);
		}
		else
		{
			ViewerUtils.drawRectangle(pGraphics, getBounds(pNode));
		}
	}

	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		if(pDirection == Direction.EAST)
		{
			return new Point(getBounds(pNode).getMaxX(), getBounds(pNode).getY());
		}
		else
		{
			return new Point(getBounds(pNode).getX(), getBounds(pNode).getY());
		}
	}
	
	/*
	 * The x position is a function of the position of the implicit parameter
	 * node and the nesting depth of the call node.
	 */
	private static int getX(Node pNode)
	{
		final Diagram diagram = pNode.getDiagram().get();
		final ImplicitParameterNode implicitParameterNode = (ImplicitParameterNode) pNode.getParent();
		if(implicitParameterNode != null )
		{
			int depth = 0;
			if( diagram != null )
			{
				depth = new ControlFlow(diagram).getNestingDepth((CallNode)pNode);
			}
			return IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(implicitParameterNode).getCenter().getX() -
					WIDTH / 2 + depth * WIDTH/2;
		}
		else
		{
			return 0;
		}
	}
	
	/*
	 * If the node has a caller, the Y coordinate is a gap below the last return Y value
	 * of the caller or a set distance before the previous call node, whatever is lower.
	 * If not, it's simply a set distance below the previous call node.
	 */
	private int getYWithNoConstructorCall(Node pNode)
	{
		final CallNode callNode = (CallNode) pNode;
		final ImplicitParameterNode implicitParameterNode = (ImplicitParameterNode) callNode.getParent();
		final Diagram diagram = callNode.getDiagram().get();
		if( implicitParameterNode == null || diagram == null )
		{
			return 0; // Only used for the ImageCreator
		}
		ControlFlow flow = new ControlFlow(diagram);
		Optional<CallNode> caller = flow.getCaller(callNode);
		if( caller.isPresent() )
		{
			int result = 0;
			if( flow.isNested(callNode) && flow.isFirstCallee(callNode))
			{
				result = getY(caller.get()) + Y_GAP_BIG;
			}
			else if( flow.isNested(callNode) && !flow.isFirstCallee(callNode) )
			{
				result = getMaxY(flow.getPreviousCallee(callNode)) + Y_GAP_SMALL;
			}
			else if( !flow.isNested(callNode) && flow.isFirstCallee(callNode) )
			{
				result = getY(caller.get()) + Y_GAP_SMALL;
			}
			else
			{
				result = getMaxY(flow.getPreviousCallee(callNode)) + Y_GAP_SMALL;
			}
			return result;
		}
		else
		{
			return IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(implicitParameterNode).getMaxY() + Y_GAP_SMALL;
		}
	} 

	/**
	 * @param pNode the node.
	 * @return If there's no callee, returns a fixed offset from the y position.
	 *     Otherwise, return with a gap from last callee.
	 */
	public int getMaxY(Node pNode)
	{
		final CallNode callNode = (CallNode) pNode;
		final Diagram diagram = callNode.getDiagram().get();
		List<Node> callees = new ArrayList<>();
		if( diagram != null )
		{
			callees = new ControlFlow(diagram).getCallees(callNode);
		}
		if( callees.isEmpty() )
		{
			return getY(callNode) + DEFAULT_HEIGHT;
		}
		else
		{
			return getMaxY(callees.get(callees.size()-1)) + Y_GAP_SMALL;
		}
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		int y = getY(pNode);
		return new Rectangle(getX(pNode), y, WIDTH, getMaxY(pNode) - y);
	}
	
	private static int getYWithConstructorCall(Node pNode) 
	{
		final ImplicitParameterNode implicitParameterNode = (ImplicitParameterNode) pNode.getParent();
		return IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(implicitParameterNode).getMaxY() + Y_GAP_TINY;
	}

	private static boolean isInConstructorCall(Node pNode)
	{
		Optional<Diagram> diagram = pNode.getDiagram();
		if(diagram.isPresent())
		{
			ControlFlow flow = new ControlFlow(diagram.get());
			return flow.isConstructorExecution(pNode);
		}
		return false;
	}
	
	protected int getY(Node pNode)
	{
		int shift = NODE_GAP_TESTER.getDimension(TEST_STRING).height() / 3;
		// Only apply shift if necessary
		if ( shift < MINIMUM_SHIFT_THRESHOLD )
		{
			shift = 0;
		}
		if(isInConstructorCall(pNode))
		{
			return getYWithConstructorCall(pNode) + shift;
		}
		else 
		{
			return getYWithNoConstructorCall(pNode) + shift;
		}
	}
}
