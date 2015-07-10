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

package ca.mcgill.cs.stg.jetuml.diagrams;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.CallNode;
import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.ReturnEdge;

/**
 * A UML sequence diagram.
 */
public class SequenceDiagramGraph extends Graph
{
	private static final Node[] NODE_PROTOTYPES = new Node[]{new ImplicitParameterNode(), new CallNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[]{new CallEdge(), new ReturnEdge(), new NoteEdge()};
	
	private static final int CALL_NODE_YGAP = 5;
	
	/* 
	 * Adds the node, ensuring that call nodes can only be added if the
	 * point is inside the space of the related ImplicitParameterNode
	 * @see ca.mcgill.cs.stg.jetuml.graph.Graph#add(ca.mcgill.cs.stg.jetuml.graph.Node, java.awt.geom.Point2D)
	 */
	@Override
	public boolean add(Node pNode, Point2D pPoint)
	{
		if(pNode instanceof CallNode) 
		{
			ImplicitParameterNode target = insideTargetArea(pPoint);
			if( target != null )
			{
				target.addChild((ChildNode)pNode);
				super.add(pNode, pPoint);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			super.add( pNode, pPoint );
			return true;
		}
	}
	
	/*
	 * If pPoint is inside an ImplicitParameterNode but below its top
	 * rectangle, returns that node. Otherwise, returns null.
	 */
	private ImplicitParameterNode insideTargetArea(Point2D pPoint)
	{
		for( Node node : getNodes() )
		{
			if(node instanceof ImplicitParameterNode && node.contains(pPoint))
			{
				if( !(pPoint.getY() < ((ImplicitParameterNode)node).getTopRectangle().getMaxY() + CALL_NODE_YGAP))
				{
					return (ImplicitParameterNode) node;
				}
			}
		}
		return null;
	}
	
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2)
	{
		if( !super.canConnect(pEdge, pNode1, pNode2) )
		{
			return false;
		}
		if(pNode1 instanceof CallNode && pEdge instanceof ReturnEdge)
		{
			return pNode2 == getCaller(pNode1);
		}
		if(pNode1 instanceof CallNode && !(pEdge instanceof CallEdge))
		{
			return false;
		}
		if(pNode1 instanceof CallNode && !(pNode2 instanceof CallNode) && !(pNode2 instanceof ImplicitParameterNode ))
		{
			return false;
		}
		if(pNode1 instanceof ImplicitParameterNode )
		{
			return false;
		}
		return true;
	}
	
	@Override
	protected void addEdge(Node pOrigin, Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		if( !(pOrigin instanceof CallNode) )
		{
			return;
		}
		CallNode origin = (CallNode) pOrigin;
		if( pEdge instanceof ReturnEdge )
		{
			return;
		}
		Node end = pEdge.getEnd();
		
		// Case 1 End is on the same implicit parameter -> create a self call
		// Case 2 End is on an existing call node on a different implicit parameter -> connect
		// Case 3 End is on a different implicit parameter -> create a new call
		// Case 4 End is on an implicit parameter top node -> creates node
		if( end instanceof CallNode )
		{
			CallNode endAsCallNode = (CallNode) end;
			if( endAsCallNode.getParent() == origin.getParent() ) // Case 1
			{
				CallNode newCallNode = new CallNode();
				((ImplicitParameterNode)origin.getParent()).addChild(newCallNode, pPoint1);
				pEdge.connect(origin, newCallNode);
			}
			else // Case 2
			{
				// Simple connect
			}
		}
		else if( end instanceof ImplicitParameterNode )
		{
			ImplicitParameterNode endAsImplicitParameterNode = (ImplicitParameterNode) end;
			if(endAsImplicitParameterNode.getTopRectangle().contains(pPoint2)) // Case 4
			{
				((CallEdge)pEdge).setMiddleLabel("\u00ABcreate\u00BB");
			}
			else // Case 3
			{
				CallNode newCallNode = new CallNode();
				endAsImplicitParameterNode.addChild(newCallNode, pPoint1);
				pEdge.connect(pOrigin, newCallNode);
			}
		}
		
//		Node n = null;
//		if(end instanceof CallNode) 
//		{
//			// check for cycles
//			CallNode caller = (CallNode)pOrigin; 
//			while(caller != null && end != caller)
//			{
//				caller = getCaller(caller);
//			}
//         
//			if( getCaller(end) == null && end != caller)
//			{
//				n = end;
//			}
//			else
//			{
//				CallNode c = new CallNode();
//				
//				c.setParent(((CallNode)end).getParent());
//				pEdge.connect(pOrigin, c);
//				n = c;
//			}
//		}
//		else if(end instanceof ImplicitParameterNode)
//		{
//			if(((ImplicitParameterNode)end).getTopRectangle().contains(pPoint2))
//			{
//				n = end;
//				((CallEdge)pEdge).setMiddleLabel("\u00ABcreate\u00BB");
//			}
//			else
//			{
//				CallNode c = new CallNode();
//				c.setParent((ImplicitParameterNode) end);
//				pEdge.connect(pOrigin, c);
//				n = c;
//			}
//		}
	}

	@Override
	public void removeEdge(Edge pEdge)
	{
		super.removeEdge(pEdge);
		if(pEdge instanceof CallEdge && hasNoCallees(pEdge.getEnd())) 
		{
			removeNode(pEdge.getEnd());
		}		
	}
	
	/**
	 * @param pNode The node to check
	 * @return True if pNode is a call node that does not have any outgoing
	 * call edge.
	 */
	private boolean hasNoCallees(Node pNode)
	{
		if( !(pNode instanceof CallNode ))
		{
			return false;
		}
		assert pNode instanceof CallNode;
		for( Edge edge : aEdges )
		{
			if( edge.getStart() == pNode )
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param pNode The node to obtain the caller for.
	 * @return The CallNode that has a outgoing edge terminated
	 * at pNode, or null if there are none.
	 */
	public CallNode getCaller(Node pNode)
	{
		for( Edge edge : aEdges )
		{
			if( edge.getEnd() == pNode  && edge instanceof CallEdge )
			{
				return (CallNode) edge.getStart();
			}
		}
		return null;
	}
	
	/**
	 * @param pStart The starting node.
	 * @param pEnd The end node.
	 * @return The edge that starts at node pStart and ends at node pEnd, or null if there is no 
	 * such edge.
	 */
	public Edge findEdge(Node pStart, Node pEnd)
	{
		for( Edge edge : aEdges )
		{
			if(edge.getStart() == pStart && edge.getEnd() == pEnd)
			{
				return edge;
			}
		}
		return null;
	}
 
	@Override
	public void layout(Graphics2D pGraphics2D, Grid pGrid)
	{
		super.layout(pGraphics2D, pGrid);

		ArrayList<Node> topLevelCalls = new ArrayList<>();
		ArrayList<Node> objects = new ArrayList<>();
		Collection<Node> nodes = getNodes();
		Iterator<Node> iter = nodes.iterator();
		while(iter.hasNext())
		{
			Node n = (Node)iter.next();
         
			if(n instanceof CallNode && getCaller(n) == null)
			{
				topLevelCalls.add(n);
			} 
			else if(n instanceof ImplicitParameterNode)
			{	
				objects.add(n);
			}      
		}
		Collection<Edge> edges = getEdges();
		Iterator<Edge> iter2 = edges.iterator();
		while(iter2.hasNext())
		{
			Edge e = (Edge)iter2.next();
			if(e instanceof CallEdge)
			{
				Node end = e.getEnd();
				if(end instanceof CallNode)
				{
					((CallNode)end).setSignaled(((CallEdge)e).isSignal());
				}
			}
		}
		heightObjectLayout(topLevelCalls, objects, pGraphics2D, pGrid);
	}
	
	/*
	 * @param pTopLevelCalls an ArrayList of Nodes in the topLevel of Calls.
	 * @param pObjects an ArrayList of Nodes to work with.
	 * @param pGraphics2D Graphics2D from layout call.
	 * @param pGrid Grid from layout call.
	 */
	private void heightObjectLayout(ArrayList<Node> pTopLevelCalls, ArrayList<Node> pObjects, Graphics2D pGraphics2D, Grid pGrid)
	{
		// find the max of the heights of the objects
		Collection<Node>nodes = getNodes();
		Iterator<Node> iter;
		double top = 0;
		for(int i = 0; i < pObjects.size(); i++)
		{
			ImplicitParameterNode n = (ImplicitParameterNode)pObjects.get(i);
			n.translate(0, -n.getBounds().getY());
			top = Math.max(top, n.getTopRectangle().getHeight());
		}

		for (int i = 0; i < pTopLevelCalls.size(); i++)
		{
			CallNode call = (CallNode) pTopLevelCalls.get(i);
			call.layout(this, pGraphics2D, pGrid);
		}

		iter = nodes.iterator();
		while(iter.hasNext())
		{
			Node n = (Node)iter.next();
			if(n instanceof CallNode) 
			{
				top = Math.max(top, n.getBounds().getY() + n.getBounds().getHeight());
			}
		}

		top += CallNode.CALL_YGAP;

		for(int i = 0; i < pObjects.size(); i++)
		{
			ImplicitParameterNode n = (ImplicitParameterNode) pObjects.get(i);
			Rectangle2D b = n.getBounds();
			n.setBounds(new Rectangle2D.Double(
					b.getX(), b.getY(), 
					b.getWidth(), top - b.getY()));         
		}
	}

	@Override
	public void draw(Graphics2D pGraphics2D, Grid pGrid)
	{
		layout(pGraphics2D, pGrid);
		Collection<Node> nodes = getNodes();
		Iterator<Node> iter = nodes.iterator();
		while (iter.hasNext())
		{
			Node n = (Node) iter.next();
			if(!(n instanceof CallNode)) 
			{
				n.draw(pGraphics2D);
			}
		}

		iter = nodes.iterator();
		while(iter.hasNext())
		{
			Node n = (Node) iter.next();
			if(n instanceof CallNode) 
			{
				n.draw(pGraphics2D);
			}
		}
		Collection<Edge> edges = getEdges();
		Iterator<Edge> iter2 = edges.iterator();
		while(iter2.hasNext())
		{
			Edge e = (Edge) iter2.next();
			e.draw(pGraphics2D);
		}
	}

	@Override
	public Node[] getNodePrototypes()
	{
		return NODE_PROTOTYPES;
	}

	@Override
	public Edge[] getEdgePrototypes()
	{
		return EDGE_PROTOTYPES;
	}
	
	@Override
	public String getFileExtension() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("sequence.extension");
	}

	@Override
	public String getDescription() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("sequence.name");
	}
}





