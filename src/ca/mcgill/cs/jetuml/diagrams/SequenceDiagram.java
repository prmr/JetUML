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

package ca.mcgill.cs.jetuml.diagrams;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.edges.CallEdge;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.graph.nodes.CallNode;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import javafx.scene.canvas.GraphicsContext;

/**
 * A UML sequence diagram.
 */
public class SequenceDiagram extends Graph
{
	private static final ImplicitParameterNode IMPLICIT_PARAMETER_NODE = new ImplicitParameterNode();
	private static final Node[] NODE_PROTOTYPES = new Node[]{IMPLICIT_PARAMETER_NODE, new CallNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[]{new CallEdge(), new ReturnEdge(), new NoteEdge()};
	
	private static final int CALL_NODE_YGAP = 5;
	
	static 
	{
		IMPLICIT_PARAMETER_NODE.addChild(new CallNode());
	}
	
	/* 
	 * Adds the node, ensuring that call nodes can only be added if the
	 * point is inside the space of the related ImplicitParameterNode
	 * @see ca.mcgill.cs.jetuml.graph.Graph#add(ca.mcgill.cs.jetuml.graph.Node, java.awt.geom.Point2D)
	 */
	@Override
	public boolean addNode(Node pNode, Point pPoint, int pMaxWidth, int pMaxHeight)
	{
		if(pNode instanceof CallNode) 
		{
			ImplicitParameterNode target = insideTargetArea(pPoint);
			if( target != null )
			{
				target.addChild((ChildNode)pNode);
			}
			else
			{
				return false;
			}
		}
		super.addNode(pNode, pPoint, pMaxWidth, pMaxHeight);
		return true;
	}
	
	/*
	 * If pPoint is inside an ImplicitParameterNode but below its top
	 * rectangle, returns that node. Otherwise, returns null.
	 */
	private ImplicitParameterNode insideTargetArea(Point pPoint)
	{
		for( Node node : getRootNodes() )
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
	
	@Override
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2, Point pPoint2)
	{
		boolean lReturn = true;
		if( !super.canConnect(pEdge, pNode1, pNode2, pPoint2) )
		{
			lReturn = false;
		}
		else if(pNode1 instanceof CallNode && pEdge instanceof ReturnEdge && pNode2 instanceof CallNode)
		{
			// The end node has to be the caller, and adding a return edge on the same object is not allowed.
			lReturn = pNode2 == getCaller(pNode1) && 
					!(((CallNode)pNode1).getParent() == ((CallNode)pNode2).getParent());
		}
		else if(pNode1 instanceof CallNode && !(pEdge instanceof CallEdge))
		{
			lReturn = false;
		}
		else if(pNode1 instanceof CallNode && !(pNode2 instanceof CallNode) && !(pNode2 instanceof ImplicitParameterNode ))
		{
			lReturn = false;
		}
		else if(pNode1 instanceof ImplicitParameterNode )
		{
			lReturn = false;
		}
		else if( pNode1 instanceof CallNode && pEdge instanceof CallEdge && pNode2 instanceof ImplicitParameterNode && getCaller(pNode2) != null)
		{
			lReturn = !((ImplicitParameterNode)pNode2).getTopRectangle().contains(pPoint2);
		}
		return lReturn;
	}
	
	/*
	 * Returns true if pCalle is in the control-flow of pPotentialCaller
	 */
	private boolean isCallDominator(CallNode pPotentialCaller, CallNode pCallee)
	{
		for( CallNode caller = getCaller(pCallee); caller != null; caller = getCaller(caller))
		{
			if( caller == pPotentialCaller )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void completeEdgeAddition(Node pOrigin, Edge pEdge, Point pPoint1, Point pPoint2)
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
		// Case 5 End is on an implicit parameter node in the same call graph -> new callnode.
		if( end instanceof CallNode )
		{
			CallNode endAsCallNode = (CallNode) end;
			if( endAsCallNode.getParent() == origin.getParent() ) // Case 1
			{
				CallNode newCallNode = new CallNode();
				((ImplicitParameterNode)origin.getParent()).addChild(newCallNode, pPoint1);
				pEdge.connect(origin, newCallNode, this);
			}
			else // Case 2
			{
				if( isCallDominator(endAsCallNode, origin))
				{
					CallNode newCallNode = new CallNode();
					((ImplicitParameterNode)endAsCallNode.getParent()).addChild(newCallNode, pPoint1);
					pEdge.connect(origin, newCallNode, this);
				}
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
				pEdge.connect(pOrigin, newCallNode, this);
			}
		}
	}

	@Override
	public void removeEdge(Edge pEdge)
	{
		super.removeEdge(pEdge);
		if(pEdge instanceof CallEdge && hasNoCallees(pEdge.getEnd())) 
		{
			removeNode(pEdge.getEnd());
		}
		
		// Also delete the return edge, if it exists
		if( pEdge instanceof CallEdge )
		{
			Edge returnEdge = null;
			for( Edge edge : aEdges )
			{
				if( edge instanceof ReturnEdge && edge.getStart() == pEdge.getEnd() && edge.getEnd() == pEdge.getStart())
				{
					returnEdge = edge;
					break;
				}
			}
			if( returnEdge != null )
			{
				removeEdge(returnEdge);
			}
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
	 * @param pNode The node to obtain the callees for.
	 * @return All Nodes pointed to by an outgoing edge starting
	 * at pNode, or null if there are none.
	 */
	private List<Node> getCallees(Node pNode)
	{
		List<Node> callees = new ArrayList<Node>();
		for (Edge edge : aEdges )
		{
			if ( edge.getStart() == pNode && edge instanceof CallEdge )
			{
				callees.add(edge.getEnd());
			}
		}
		return callees;
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
	public void layout()
	{
		super.layout();

		ArrayList<Node> topLevelCalls = new ArrayList<>();
		ArrayList<Node> objects = new ArrayList<>();
		
		for( Node rootNode : aRootNodes )
		{
			if( rootNode instanceof ImplicitParameterNode )
			{
				objects.add(rootNode);
				for( Node callNode : ((ImplicitParameterNode) rootNode).getChildren())
				{
					if( getCaller(callNode) == null )
					{
						topLevelCalls.add(callNode);
					}
				}
			}
		}
		heightObjectLayout(topLevelCalls, objects);
	}
	
	/*
	 * Find the max of the heights of the objects
	 * @param pTopLevelCalls an ArrayList of Nodes in the topLevel of Calls.
	 * @param pObjects an ArrayList of Nodes to work with.
	 * @param pGrid Grid from layout call.
	 */
	private void heightObjectLayout(ArrayList<Node> pTopLevelCalls, ArrayList<Node> pObjects)
	{
		double top = 0;
		for(Node node : pObjects)
		{
			node.translate(0, -node.view().getBounds().getY());
			top = Math.max(top, ((ImplicitParameterNode)node).getTopRectangle().getHeight());
		}

		for(Node node : pTopLevelCalls )
		{
			node.view().layout(this);
		}

		for(Node node : aRootNodes )
		{
			if( node instanceof ImplicitParameterNode )
			{
				for( Node callNode : ((ImplicitParameterNode) node).getChildren())
				{
					top = Math.max(top, callNode.view().getBounds().getY() + callNode.view().getBounds().getHeight());
				}
			}
		}

		top += CallNode.CALL_YGAP;

		for( Node node : pObjects )
		{
			Rectangle bounds = node.view().getBounds();
			((ImplicitParameterNode)node).setBounds(new Rectangle(bounds.getX(), 
					bounds.getY(), bounds.getWidth(), (int)top - bounds.getY()));         
		}
	}

	@Override
	public void draw(GraphicsContext pGraphics)
	{
		layout();
		super.draw(pGraphics);
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
		return RESOURCES.getString("sequencediagram.file.extension");
	}

	@Override
	public String getDescription() 
	{
		return RESOURCES.getString("sequencediagram.file.name");
	}

	@Override
	protected Node deepFindNode( Node pNode, Point pPoint )
	{		
		if ( pNode instanceof CallNode )
		{
			for (Node child : getCallees(pNode))
			{
			
				if ( child != null )
				{
					Node node = deepFindNode(child, pPoint);
					if ( node != null )
					{
						return node;
					}
				}
			}
		}
		return super.deepFindNode(pNode, pPoint);
	}
}
