/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 * A method call node in a sequence diagram. In addition to edges,
 * the node is linked to it callee and callers.
*/
public class CallNode extends RectangularNode implements ChildNode
{
	public static final int CALL_YGAP = 20;

	private static final int DEFAULT_WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	private static final int MIN_YGAP = 10;

	private ImplicitParameterNode aImplicitParameter;
	private boolean aSignaled;
	private boolean aOpenBottom;

	/**
	 *  Construct a call node with a default size.
	 */
	public CallNode()
	{
		setBounds(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(Color.WHITE);
		pGraphics2D.fill(Conversions.toRectangle2D(getBounds()));
		pGraphics2D.setColor(oldColor);
		if(aOpenBottom)
		{
			Rectangle b = getBounds();
			double x1 = b.getX();
			double x2 = x1 + b.getWidth();
			double y1 = b.getY();
			double y3 = y1 + b.getHeight();
			double y2 = y3 - CALL_YGAP;
			pGraphics2D.draw(new Line2D.Double(x1, y1, x2, y1));
			pGraphics2D.draw(new Line2D.Double(x1, y1, x1, y2));
			pGraphics2D.draw(new Line2D.Double(x2, y1, x2, y2));
			Stroke oldStroke = pGraphics2D.getStroke();
			// CSOFF:
			pGraphics2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] { 5.0f, 5.0f }, 0.0f));
			// CSON:
			pGraphics2D.draw(new Line2D.Double(x1, y2, x1, y3));
			pGraphics2D.draw(new Line2D.Double(x2, y2, x2, y3));
			pGraphics2D.setStroke(oldStroke);
		}
		else
		{
			pGraphics2D.draw(Conversions.toRectangle2D(getBounds()));
		}
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

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.stg.jetuml.graph.RectangularNode#translate(double, double)
	 */
	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		// Prevent going above the ImplicitParameterNode
		if( getBounds().getY() < aImplicitParameter.getTopRectangle().getMaxY() + MIN_YGAP)
		{
			setBounds(new Rectangle(getBounds().getX(), (int) Math.round(aImplicitParameter.getTopRectangle().getMaxY()) + MIN_YGAP, 
					getBounds().getWidth(), getBounds().getHeight()));
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		assert aImplicitParameter != null;
		assert pGraph instanceof SequenceDiagramGraph;
		SequenceDiagramGraph graph = (SequenceDiagramGraph) pGraph;

		// Shift the node to its proper place on the X axis.
		translate(computeMidX(pGraph) - getBounds().getCenter().getX(), 0);

		// Compute the Y coordinate of the bottom of the node
		double bottomY = computeBottomY(graph, pGraphics2D, pGrid);

		Rectangle bounds = getBounds();

		double minHeight = DEFAULT_HEIGHT;
		Edge returnEdge = graph.findEdge(this, graph.getCaller(this));
		if(returnEdge != null)
		{
			Rectangle edgeBounds = returnEdge.getBounds();
			minHeight = Math.max(minHeight, edgeBounds.getHeight());         
		}
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), (int)Math.max(minHeight, bottomY - bounds.getY())));
	}
	
	/*
	 * @param pGraph
	 * @return All the nodes (CallNodes or ImplicitParameterNodes) that have a calledge
	 * originating at this CallNode. If an ImplicitParameterNode is in the list, it's always
	 * returned first.
	 */
	private List<Node> getCallees(Graph pGraph)
	{
		List<Node> callees = new ArrayList<>();
		for( Edge edge : pGraph.getEdges())
		{
			if( edge.getStart() == this && edge instanceof CallEdge )
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

	/*
	 * @return The X coordinate that should be the middle
	 * of this call node. Takes into account nested calls.
	 */
	private double computeMidX(Graph pGraph)
	{
		int xmid = aImplicitParameter.getBounds().getCenter().getX();

		// Calculate a shift for each caller with the same implicit parameter
		for(CallNode node = ((SequenceDiagramGraph)pGraph).getCaller(this); node != null && node != this; 
				node = ((SequenceDiagramGraph)pGraph).getCaller(node))
		{
			if(node.aImplicitParameter == aImplicitParameter)
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
	private double computeBottomY(SequenceDiagramGraph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		// Compute the Y coordinate of the bottom of the node
		double bottomY = getBounds().getY() + CALL_YGAP;

		for(Node node : getCallees(pGraph))
		{
			if(node instanceof ImplicitParameterNode) // <<create>>
			{
				node.translate(0, bottomY - ((ImplicitParameterNode) node).getTopRectangle().getCenterY());
				bottomY += ((ImplicitParameterNode)node).getTopRectangle().getHeight() / 2 + CALL_YGAP;
			}
			else if(node instanceof CallNode)
			{  
				Edge callEdge = pGraph.findEdge(this, node);
				// compute height of call edge
				if(callEdge != null)
				{
					bottomY += callEdge.getBounds().getHeight() - CALL_YGAP;
				}

				node.translate(0, bottomY - node.getBounds().getY());
				node.layout(pGraph, pGraphics2D, pGrid);
				if(((CallNode) node).aSignaled)
				{
					bottomY += CALL_YGAP;
				}
				else
				{
					bottomY += node.getBounds().getHeight() + CALL_YGAP;
				}
			}
		}
		if(aOpenBottom)
		{
			bottomY += 2 * CALL_YGAP;
		}
		return bottomY;
	}

	/**
	 * Sets the signaled property.
	 * @param pNewValue true if this node is the target of a signal edge
	 */      
	public void setSignaled(boolean pNewValue)
	{ 
		aSignaled = pNewValue; 
	}

	/**
	 * Gets the openBottom property.
	 * @return true if this node is the target of a signal edge
	 */
	public boolean isOpenBottom() 
	{ 
		return aOpenBottom; 
	}

	/**
	 * Sets the openBottom property.
	 * @param pNewValue true if this node is the target of a signal edge
	 */      
	public void setOpenBottom(boolean pNewValue)
	{ 
		aOpenBottom = pNewValue; 
	}

	@Override
	public CallNode clone()
	{
		CallNode cloned = (CallNode) super.clone();
		return cloned;
	}
	
	/**
     * Gets the parent of this node.
     * @return the parent node, or null if the node has no parent
	 */
	public ParentNode getParent() 
   	{ 
		return aImplicitParameter; 
	}

	/**
     * Sets the parent of this node.
     * @param pNode the parent node, or null if the node has no parent
	 */
	public void setParent(ParentNode pNode) 
	{
		assert pNode instanceof ImplicitParameterNode || pNode == null;
		aImplicitParameter = (ImplicitParameterNode) pNode;
	}

	@Override
	public boolean requiresParent()
	{
		return true;
	}
}
