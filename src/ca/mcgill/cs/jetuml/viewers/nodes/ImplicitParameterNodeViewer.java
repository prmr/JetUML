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

import static ca.mcgill.cs.jetuml.geom.GeomUtils.max;

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

/**
 * An object to render an implicit parameter in a Sequence diagram.
 */
public final class ImplicitParameterNodeViewer extends AbstractNodeViewer
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	private static final int HORIZONTAL_PADDING = 10; // 2x the left and right padding around the name of the implicit parameter
	private static final int TAIL_HEIGHT = 20; // Piece of the life line below the last call node
	private static final int TOP_HEIGHT = 60;
	private static final int Y_GAP_SMALL = 20; 
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED, TextDecoration.UNDERLINED);
	private static final CallNodeViewer CALL_NODE_VIEWER = new CallNodeViewer();
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		Rectangle top = getTopRectangle(pNode);
		ViewerUtils.drawRectangle(pGraphics, top);
		NAME_VIEWER.draw(((ImplicitParameterNode)pNode).getName(), pGraphics, top);
		int xmid = top.getCenter().getX();
		ViewerUtils.drawLine(pGraphics, xmid,  top.getMaxY(), xmid, getBounds(pNode).getMaxY(), LineStyle.DOTTED);
	}
	
	@Override
	public boolean contains(Node pNode, Point pPoint)
	{
		final Rectangle bounds = getBounds(pNode);
		return bounds.getX() <= pPoint.getX() && pPoint.getX() <= bounds.getX() + bounds.getWidth();
	}

	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		Rectangle bounds = getBounds(pNode);
		if(pDirection == Direction.EAST)
		{
			return new Point(bounds.getMaxX(), bounds.getY() + TOP_HEIGHT / 2);
		}
		else
		{
			return new Point(bounds.getX(), bounds.getY() + TOP_HEIGHT / 2);
		}
	}
	
	private static Point getMaxXYofChildren(Node pNode)
	{
		int maxY = 0;
		int maxX = 0;
		for( Node child : ((ImplicitParameterNode)pNode).getChildren() )
		{
			Rectangle bounds = NodeViewerRegistry.getBounds(child);
			maxX = Math.max(maxX,  bounds.getMaxX());
			maxY = Math.max(maxY, bounds.getMaxY());
		}
		return new Point(maxX, maxY);
	}
	
	/**
     * Returns the rectangle at the top of the object node.
     * @param pNode the node.
     * @return the top rectangle
	 */
	public Rectangle getTopRectangle(Node pNode)
	{
		int width = Math.max(NAME_VIEWER.getDimension(((ImplicitParameterNode)pNode).getName()).width()+ 
				HORIZONTAL_PADDING, DEFAULT_WIDTH);
		int yVal = 0;
		if( isInConstructorCall(pNode) )
		{
			yVal = getYWithConstructorCall(pNode);
		}
		return new Rectangle(pNode.position().getX(), yVal, width, TOP_HEIGHT);
	}

	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Rectangle topRectangle = getTopRectangle(pNode);
		Point childrenMaxXY = getMaxXYofChildren(pNode);
		int width = max(topRectangle.getWidth(), DEFAULT_WIDTH, childrenMaxXY.getX() - pNode.position().getX());
		int height = max(DEFAULT_HEIGHT, childrenMaxXY.getY() + TAIL_HEIGHT) - topRectangle.getY();	
		return new Rectangle(pNode.position().getX(), topRectangle.getY(), width, height);
	}
	
	private int getYWithConstructorCall(Node pNode) 
	{
		assert isInConstructorCall(pNode);
		ControlFlow controlFlow = new ControlFlow(pNode.getDiagram().get());
		CallNode child = (CallNode) getFirstChild(pNode).get();
		// If the node is the first callee, set a fix distance from its caller
		if( controlFlow.isFirstCallee(child) )
		{
			CallNode caller = controlFlow.getCaller(child).get(); 
			return CALL_NODE_VIEWER.getY(caller) + Y_GAP_SMALL;
		}
		Node prevCallee = controlFlow.getPreviousCallee(child);
		// If the node is not the first callee but the previous callee is in constructor call
		if( controlFlow.isConstructorExecution(prevCallee) )
		{
			// Returns a fixed distance from the bound of the previous callee's parent
			return getBounds(prevCallee.getParent()).getMaxY();
		}
		else
		{
			// Returns a fixed distance from the previous callee
			return CALL_NODE_VIEWER.getMaxY(prevCallee) + Y_GAP_SMALL;
		}
	}

	/*
	 * Returns true if the ImplicitParameterNode is in the constructor call
	 */
	private static boolean isInConstructorCall(Node pNode) 
	{
		Optional<Diagram> diagram = pNode.getDiagram();
		if(diagram.isPresent())
		{
			ControlFlow flow = new ControlFlow(diagram.get());
			Optional<Node> child = getFirstChild(pNode);
			return child.isPresent() && flow.isConstructorExecution(child.get());
		}	
		return false;
	}
	
	/*
	 * Returns the Optional of the first child node of the ImplicitParameterNode if exists;
	 * otherwise, returns Optional.empty().
	 */
	private static Optional<Node> getFirstChild(Node pNode)
	{
		assert pNode!=null;
		List<Node> children = pNode.getChildren();
		if(!children.isEmpty()) 
		{
			return Optional.of(children.get(0));
		}
		return Optional.empty();
	}
}
