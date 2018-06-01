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

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * An object to render a call node in a Sequence diagram.
 */
public class CallNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	private static final StrokeLineCap LINE_CAP = StrokeLineCap.ROUND;
	private static final StrokeLineJoin LINE_JOIN = StrokeLineJoin.ROUND;
	private static final double[] DASHES = new double[] {5, 5};
	
	/**
	 * @param pNode The node to wrap.
	 */
	public CallNodeView(CallNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private boolean openBottom()
	{
		return ((CallNode)node()).isOpenBottom();
	}
	
	private ImplicitParameterNode implicitParameter()
	{
		return (ImplicitParameterNode)((CallNode)node()).getParent();
	}
	
	@Override
	public void setBounds(Rectangle pNewBounds)
	{
		super.setBounds(pNewBounds);
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		double oldLineWidth = pGraphics.getLineWidth();
		pGraphics.setLineWidth(LINE_WIDTH);
		if(openBottom())
		{
			final Rectangle bounds = getBounds();
			int x1 = bounds.getX();
			int x2 = bounds.getMaxX();
			int y1 = bounds.getY();
			int y3 = bounds.getMaxY();
			int y2 = y3 - CallNode.CALL_YGAP;
			pGraphics.strokeLine(x1, y1, x2, y1);
			pGraphics.strokeLine(x1, y1, x1, y2);
			pGraphics.strokeLine(x2, y1, x2, y2);
			StrokeLineCap oldLineCap = pGraphics.getLineCap();
			StrokeLineJoin oldLineJoin = pGraphics.getLineJoin();
			double[] oldDashes = pGraphics.getLineDashes();
			pGraphics.setLineCap(LINE_CAP);
			pGraphics.setLineJoin(LINE_JOIN);
			pGraphics.setLineDashes(DASHES);
			
			pGraphics.strokeLine(x1, y2, x1, y3);
			pGraphics.strokeLine(x2, y2, x2, y3);
			
			pGraphics.setLineCap(oldLineCap);
			pGraphics.setLineJoin(oldLineJoin);
			pGraphics.setLineDashes(oldDashes);
		}
		else
		{
			Paint oldFill = pGraphics.getFill();
			pGraphics.setFill(BACKGROUND_COLOR);
			Rectangle bounds = getBounds();
			pGraphics.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			pGraphics.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			pGraphics.setFill(oldFill);
			
		}
		pGraphics.setLineWidth(oldLineWidth);
	}
	
	@Override
	public void layout(Diagram pGraph)
	{
		assert implicitParameter() != null;
		assert pGraph instanceof SequenceDiagram;
		SequenceDiagram graph = (SequenceDiagram) pGraph;

		// Shift the node to its proper place on the X axis.
		node().translate(computeMidX(pGraph) - getBounds().getCenter().getX(), 0);

		// Compute the Y coordinate of the bottom of the node
		int bottomY = computeBottomY(graph);

		final Rectangle bounds = getBounds();

		int minHeight = DEFAULT_HEIGHT;
		Edge returnEdge = graph.findEdge(node(), graph.getCaller(node()));
		if(returnEdge != null)
		{
			minHeight = Math.max(minHeight, returnEdge.view().getBounds().getHeight());         
		}
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), Math.max(minHeight, bottomY - bounds.getY())));
	}
	
	/*
	 * @return The X coordinate that should be the middle
	 * of this call node. Takes into account nested calls.
	 */
	private int computeMidX(Diagram pGraph)
	{
		int xmid = implicitParameter().view().getBounds().getCenter().getX();

		// Calculate a shift for each caller with the same implicit parameter
		for(CallNode node = ((SequenceDiagram)pGraph).getCaller(node()); node != null && node != node(); 
				node = ((SequenceDiagram)pGraph).getCaller(node))
		{
			if(((CallNode)node).getParent() == implicitParameter())
			{
				xmid += getBounds().getWidth() / 2;
			}
		}
		return xmid;
	}

	/*
	 * Compute the Y coordinate of the bottom of the CallNode. This 
	 * triggers the layout of all callee nodes.
	 */
	private int computeBottomY(SequenceDiagram pGraph)
	{
		// Compute the Y coordinate of the bottom of the node
		int bottomY = getBounds().getY() + CallNode.CALL_YGAP;

		for(Node node : getCallees(pGraph))
		{
			if(node instanceof ImplicitParameterNode) // <<create>>
			{
				node.translate(0, bottomY - ((ImplicitParameterNode) node).getTopRectangle().getCenter().getY());
				bottomY += ((ImplicitParameterNode)node).getTopRectangle().getHeight() / 2 + CallNode.CALL_YGAP;
			}
			else if(node instanceof CallNode)
			{  
				Edge callEdge = pGraph.findEdge(node(), node);
				// compute height of call edge
				if(callEdge != null)
				{
					bottomY += callEdge.view().getBounds().getHeight() - CallNode.CALL_YGAP;
				}

				node.translate(0, bottomY - node.view().getBounds().getY());
				node.view().layout(pGraph);
				if(((CallNode) node).isSignaled(pGraph))
				{
					bottomY += CallNode.CALL_YGAP;
				}
				else
				{
					bottomY += node.view().getBounds().getHeight() + CallNode.CALL_YGAP;
				}
			}
		}
		if(openBottom())
		{
			bottomY += 2 * CallNode.CALL_YGAP;
		}
		return bottomY;
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
		for( Edge edge : pGraph.getEdges())
		{
			if( edge.getStart() == node() && edge instanceof CallEdge )
			{
				if( edge.getEnd() instanceof ImplicitParameterNode )
				{
					callees.add(0, edge.getEnd());
				}
				else
				{
					callees.add(edge.getEnd());
				}
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
}
