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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;

/**
 * A method call node in a scenario diagram.
*/
public class CallNode extends HierarchicalNode
{
	public static final int CALL_YGAP = 20;
	
	private static final int DEFAULT_WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	
	private ImplicitParameterNode aImplicitParameter;
	private boolean aSignaled;
	private boolean aOpenBottom;
	
	private ArrayList<HierarchicalNode> aCalls;
	
   /**
    *  Construct a call node with a default size.
    */
	public CallNode()
	{
		aCalls = new ArrayList<HierarchicalNode>();
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

	@Override
	public boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		Node end = pEdge.getEnd();
		if(end == null)
		{
			return false;
		}

		if(pEdge instanceof ReturnEdge)
		{
			return end == getParent();
		}
         
		if(!(pEdge instanceof CallEdge))
		{
			return false;
		}
      
		Node n = null;
		if(end instanceof CallNode) 
		{
			// check for cycles
			HierarchicalNode parent = this; 
			while(parent != null && end != parent)
			{
				parent = parent.getParent();
			}
         
			if(((CallNode)end).getParent() == null && end != parent)
			{
				n = end;
			}
			else
			{
				CallNode c = new CallNode();
				c.aImplicitParameter = ((CallNode)end).aImplicitParameter;
				pEdge.connect(this, c);
				n = c;
			}
		}
		else if(end instanceof ImplicitParameterNode)
		{
			if(((ImplicitParameterNode)end).getTopRectangle().contains(pPoint2))
			{
				n = end;
				((CallEdge)pEdge).setMiddleLabel("\u00ABcreate\u00BB");
			}
			else
			{
				CallNode c = new CallNode();
				c.aImplicitParameter = (ImplicitParameterNode)end;
				pEdge.connect(this, c);
				n = c;
			}
		}
		else
		{
			return false;
		}

		int i = 0;
		while(i < aCalls.size() && aCalls.get(i).getBounds().getY() <= pPoint1.getY())
		{
			i++;
		}
		addChild(i, (HierarchicalNode)n);
		return true;
	}

	@Override
	public void removeEdge(Graph pGraph, Edge pEdge)
	{
		if(pEdge.getStart() == this)
		{
			removeChild((HierarchicalNode)pEdge.getEnd());
		}
	}

	@Override
	public void removeNode(Graph pGraph, Node pNode)
	{
      if(pNode == getParent() || pNode == aImplicitParameter)
      {
    	  pGraph.removeNode(this);
      }
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

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		if(aImplicitParameter == null)
		{
			return;
		}
		double xmid = aImplicitParameter.getBounds().getCenterX();

		for(CallNode c = (CallNode)getParent(); c != null; c = (CallNode)c.getParent())
		{
			if (c.aImplicitParameter == aImplicitParameter)
			{
				xmid += getBounds().getWidth() / 2;
			}
		}

		translate(xmid - getBounds().getCenterX(), 0);
		double ytop = getBounds().getY() + CALL_YGAP;

		for(int i = 0; i < aCalls.size(); i++)
		{
			Node n = aCalls.get(i);
			if(n instanceof ImplicitParameterNode) // <<create>>
			{
				n.translate(0, ytop - ((ImplicitParameterNode) n).getTopRectangle().getCenterY());
				ytop += ((ImplicitParameterNode)n).getTopRectangle().getHeight() / 2 + CALL_YGAP;
			}
			else if(n instanceof CallNode)
			{  
				Edge callEdge = findEdge(pGraph, this, n);
				// compute height of call edge
				if(callEdge != null)
				{
					Rectangle2D edgeBounds = callEdge.getBounds();
					ytop += edgeBounds.getHeight() - CALL_YGAP;
				}
            
				n.translate(0, ytop - n.getBounds().getY());
				n.layout(pGraph, pGraphics2D, pGrid);
				if(((CallNode) n).aSignaled)
				{
					ytop += CALL_YGAP;
				}
				else
				{
					ytop += n.getBounds().getHeight() + CALL_YGAP;
				}
			}
		}
		if(aOpenBottom)
		{
			ytop += 2 * CALL_YGAP;
		}
		Rectangle2D b = getBounds();
      
		double minHeight = DEFAULT_HEIGHT;
		Edge returnEdge = findEdge(pGraph, this, getParent());
		if(returnEdge != null)
		{
			Rectangle2D edgeBounds = returnEdge.getBounds();
			minHeight = Math.max(minHeight, edgeBounds.getHeight());         
		}
		setBounds(new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth(), Math.max(minHeight, ytop - b.getY())));
	}

	@Override
	public void addChild(int pIndex, HierarchicalNode pNode) 
	{
		if (pNode == null || pIndex < 0) //base cases to not deal with
		{
			return;
		}
		HierarchicalNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aCalls.add(pIndex, pNode);
		pNode.setParent(this);
	}
	
	@Override
	public void removeChild(HierarchicalNode pNode)
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
	public void addChild(HierarchicalNode pNode)
	{
		addChild(aCalls.size(), pNode);
	}
	
	@Override
	public List<HierarchicalNode> getChildren()
	{
		return aCalls;
	}
	
	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		return pNode instanceof PointNode;
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
		cloned.aCalls = (ArrayList<HierarchicalNode>) aCalls.clone();
		return cloned;
	}
}
