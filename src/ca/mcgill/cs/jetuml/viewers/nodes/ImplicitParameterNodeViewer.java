/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.viewers.nodes;

import static ca.mcgill.cs.jetuml.geom.Util.max;

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
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
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
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, true);
	private static final CallNodeViewer CALL_NODE_VIEWER = new CallNodeViewer();
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		Rectangle top = getTopRectangle(pNode);
		ViewUtils.drawRectangle(pGraphics, top);
		NAME_VIEWER.draw(((ImplicitParameterNode)pNode).getName(), pGraphics, top);
		int xmid = top.getCenter().getX();
		ViewUtils.drawLine(pGraphics, xmid,  top.getMaxY(), xmid, getBounds(pNode).getMaxY(), LineStyle.DOTTED);
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
		if(pDirection.getX() > 0)
		{
			return new Point(bounds.getMaxX(), bounds.getY() + TOP_HEIGHT / 2);
		}
		else
		{
			return new Point(bounds.getX(), bounds.getY() + TOP_HEIGHT / 2);
		}
	}
	
	private Point getMaxXYofChildren(Node pNode)
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
		int width = Math.max(NAME_VIEWER.getDimension(((ImplicitParameterNode)pNode).getName()).getWidth()+ 
				HORIZONTAL_PADDING, DEFAULT_WIDTH); 
		int yVal = hasCaller(pNode)?getYWithCaller(pNode):0;
		return new Rectangle(pNode.position().getX(), yVal, width, TOP_HEIGHT);
	}

	@Override
	public Rectangle getBounds(Node pNode)
	{
		Rectangle topRectangle = getTopRectangle(pNode);
		Point childrenMaxXY = getMaxXYofChildren(pNode);
		int width = max(topRectangle.getWidth(), DEFAULT_WIDTH, childrenMaxXY.getX() - pNode.position().getX());
		List<Node> childNodes = ((ImplicitParameterNode)pNode).getChildren();
		int height = max(DEFAULT_HEIGHT, childrenMaxXY.getY() + TAIL_HEIGHT);	
		// Keep the default height only if the node is a constructor call with no child nodes
		if( !hasCaller(pNode) || childNodes.size()!=0 )
		{
			height -= topRectangle.getY();
		}
		return new Rectangle(pNode.position().getX(), topRectangle.getY(), width, height);
	}

	/**
	 * @param pNode The node to check.
	 * @return True if pNode has a caller.
	 */
	public boolean hasCaller(Node pNode) 
	{
		Optional<Diagram> diagram = pNode.getDiagram();
		if(diagram.isPresent())
		{
			ControlFlow flow = new ControlFlow(diagram.get());
			Optional<CallNode> caller = flow.getCaller(pNode);
			return caller.isPresent();
		}	
		return false;
	}
	
	/**
	 * 
	 * @param pNode The node to compute the position.
	 * @return If the node has child nodes, returns the y position of the last child node.
	 * Otherwise, returns a fixed distance from the y position of the node.
	 */
	public int getMaxYwithConstructorCall(Node pNode)
	{
		assert hasCaller(pNode);
		List<Node> child = ((ImplicitParameterNode)pNode).getChildren();
		if(child.size()>0)
		{
			return CALL_NODE_VIEWER.getMaxY(child.get(child.size()-1));
		}
		else 
		{
			return getYWithCaller(pNode) + Y_GAP_SMALL;
		}
	}
	
	private int getYWithCaller(Node pNode)
	{
		assert hasCaller(pNode);
		ControlFlow controlFlow = new ControlFlow(pNode.getDiagram().get());
		// If the node is the first callee of its parent
		if(controlFlow.isFirstCallee(pNode))
		{
			CallNode caller = controlFlow.getCaller(pNode).get(); 
			return CALL_NODE_VIEWER.getY(caller);
		}
		// If the node is not the first callee but the previous callee is a call node
		else if(controlFlow.getPreviousCallee(pNode) instanceof CallNode)
		{
			return CALL_NODE_VIEWER.getMaxY(controlFlow.getPreviousCallee(pNode)) + Y_GAP_SMALL;
		}
		// If the node is not the first callee but the previous callee is a implicit parameter node
		else if(controlFlow.getPreviousCallee(pNode) instanceof ImplicitParameterNode)
		{
			Node prevCallee = controlFlow.getPreviousCallee(pNode);
			List<Node> prevCallees = controlFlow.getCallees(prevCallee);
			if( prevCallees.size()==0 ) 
			{
				// Returns a fixed distance from the previous callee
				return getBounds(prevCallee).getMaxY() + Y_GAP_SMALL;
			}
			else 
			{
				Node lastCallee = prevCallees.get(prevCallees.size()-1);
				if(lastCallee instanceof CallNode)
				{
					return CALL_NODE_VIEWER.getMaxY(lastCallee) + Y_GAP_SMALL;
				}
				else 
				{
					return getYWithCaller(lastCallee);
				}
			}
		}
		return 0;
	}
}
