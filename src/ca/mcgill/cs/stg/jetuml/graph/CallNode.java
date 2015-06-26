/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;

/**
 * A method call node in a sequence diagram. In addition to edges,
 * the node is linked to it callee and callers.
*/
public class CallNode extends RectangularNode implements ParentChildNode
{
	public static final int CALL_YGAP = 20;

	private static final int DEFAULT_WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	private static final int MIN_YGAP = 10;

	private ImplicitParameterNode aImplicitParameter;
	private boolean aSignaled;
	private boolean aOpenBottom;

	private ArrayList<ParentChildNode> aCalls;
	private CallNode aCaller;

	/**
	 *  Construct a call node with a default size.
	 */
	public CallNode()
	{
		aCalls = new ArrayList<ParentChildNode>();
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(Color.WHITE);
		pGraphics2D.fill(getBounds());
		pGraphics2D.setColor(oldColor);
		if(aOpenBottom)
		{
			Rectangle2D b = getBounds();
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
			pGraphics2D.draw(getBounds());
		}
	}

	/**
	 * Gets the implicit parameter of this call.
	 * @return the implicit parameter node
	 */
	public ImplicitParameterNode getImplicitParameter()
	{
		return aImplicitParameter;
	}

	/**
	 * Sets the implicit parameter of this call.
	 * @param pNewValue the implicit parameter node
	 */
	public void setImplicitParameter(ImplicitParameterNode pNewValue)
	{
		aImplicitParameter = pNewValue;
	}

	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		if(pDirection.getX() > 0)
		{
			return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY());
		}
		else
		{
			return new Point2D.Double(getBounds().getX(), getBounds().getMinY());
		}
	}

	/**
	 * Add pChild to the right place in the list of callees, given it's coordinate.
	 * @param pChild The node to add
	 * @param pPoint The point to compare against.
	 */
	public void addChild(ParentChildNode pChild, Point2D pPoint)
	{
		int i = 0;
		while(i < aCalls.size() && aCalls.get(i).getBounds().getY() <= pPoint.getY())
		{
			i++;
		}
		addChild(i, pChild);
	}

	private static Edge findEdge(Graph pGraph, Node pStart, Node pEnd)
	{
		Collection<Edge> edges = pGraph.getEdges();
		Iterator<Edge> iter = edges.iterator(); 
		while(iter.hasNext())
		{
			Edge e = iter.next();
			if(e.getStart() == pStart && e.getEnd() == pEnd)
			{
				return e;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.stg.jetuml.graph.RectangularNode#translate(double, double)
	 */
	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		// Prevent going about the ImplicitParameterNode
		if( getBounds().getY() < aImplicitParameter.getTopRectangle().getMaxY() + MIN_YGAP)
		{
			setBounds(new Rectangle2D.Double(getBounds().getX(), aImplicitParameter.getTopRectangle().getMaxY() + MIN_YGAP, 
					getBounds().getWidth(), getBounds().getHeight()));
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		assert aImplicitParameter != null;

		// Shift the node to its proper place on the X axis.
		translate(computeMidX() - getBounds().getCenterX(), 0);

		// Compute the Y coordinate of the bottom of the node
		double bottomY = computeBottomY(pGraph, pGraphics2D, pGrid);

		Rectangle2D bounds = getBounds();

		double minHeight = DEFAULT_HEIGHT;
		Edge returnEdge = findEdge(pGraph, this, getParent());
		if(returnEdge != null)
		{
			Rectangle2D edgeBounds = returnEdge.getBounds();
			minHeight = Math.max(minHeight, edgeBounds.getHeight());         
		}
		setBounds(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), Math.max(minHeight, bottomY - bounds.getY())));
	}

	/*
	 * @return The X coordinate that should be the middle
	 * of this call node. Takes into account nested calls.
	 */
	private double computeMidX()
	{
		double xmid = aImplicitParameter.getBounds().getCenterX();

		// Calculate a shift for each parent (=caller) with the same implicit parameter
		for(CallNode node = (CallNode)getParent(); node != null; node = (CallNode)node.getParent())
		{
			if (node.aImplicitParameter == aImplicitParameter)
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
	private double computeBottomY(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		// Compute the Y coordinate of the bottom of the node
		double bottomY = getBounds().getY() + CALL_YGAP;

		for(ParentChildNode node : aCalls)
		{
			if(node instanceof ImplicitParameterNode) // <<create>>
			{
				node.translate(0, bottomY - ((ImplicitParameterNode) node).getTopRectangle().getCenterY());
				bottomY += ((ImplicitParameterNode)node).getTopRectangle().getHeight() / 2 + CALL_YGAP;
			}
			else if(node instanceof CallNode)
			{  
				Edge callEdge = findEdge(pGraph, this, node);
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

	@Override
	public void addChild(int pIndex, ParentChildNode pNode) 
	{
		if (pNode == null || pIndex < 0) //base cases to not deal with
		{
			return;
		}
		ParentChildNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aCalls.add(pIndex, pNode);
		pNode.setParent(this);
	}

	@Override
	public void removeChild(ParentChildNode pNode)
	{
		if (pNode.getParent() != this)
		{
			return;
		}
		aCalls.remove(pNode);
		pNode.setParent(null);
	}

	/**
	 * Adds a node at the end of the list.
	 * @param pNode The node to add.
	 */
	@Override
	public void addChild(ParentChildNode pNode)
	{
		addChild(aCalls.size(), pNode);
	}

	@Override
	public List<ParentChildNode> getChildren()
	{
		return aCalls;
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

	@SuppressWarnings("unchecked") //For cloning aCalls
	@Override
	public CallNode clone()
	{
		CallNode cloned = (CallNode) super.clone();
		cloned.aCalls = (ArrayList<ParentChildNode>) aCalls.clone();
		return cloned;
	}
	
	/**
     * Gets the parent of this node.
     * @return the parent node, or null if the node has no parent
	 */
	public ParentChildNode getParent() 
   	{ 
		return aCaller; 
	}

	/**
     * Sets the parent of this node.
     * @param pNode the parent node, or null if the node has no parent
	 */
	public void setParent(ParentChildNode pNode) 
	{
		assert pNode instanceof CallNode;
		aCaller = (CallNode) pNode;
	}
	
	/**
	 *  Adds a persistence delegate to a given encoder that
	 * encodes the child nodes of this node.
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(CallNode.class, new DefaultPersistenceDelegate()
		{
			protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
			{
				super.initialize(pType, pOldInstance, pNewInstance, pOut);
				for(ParentChildNode node : ((ParentChildNode) pOldInstance).getChildren())
				{
					pOut.writeStatement( new Statement(pOldInstance, "addChild", new Object[]{ node }) );            
				}
			}
		});
	}
}
