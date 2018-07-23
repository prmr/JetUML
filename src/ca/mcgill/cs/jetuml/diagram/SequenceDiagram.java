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

package ca.mcgill.cs.jetuml.diagram;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * A UML sequence diagram.
 */
public class SequenceDiagram extends Diagram
{
	private static final ImplicitParameterNode IMPLICIT_PARAMETER_NODE = new ImplicitParameterNode();
	private static final Node[] NODE_PROTOTYPES = new Node[]{IMPLICIT_PARAMETER_NODE, new CallNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[]{new CallEdge(), new ReturnEdge(), new NoteEdge()};
	
	
	static 
	{
		IMPLICIT_PARAMETER_NODE.addChild(new CallNode());
	}
	
	/**
	 * @param pNode The node to obtain the caller for.
	 * @return The CallNode that has a outgoing edge terminated
	 * at pNode, or null if there are none.
	 */
	public CallNode getCaller(Node pNode)
	{
		for( Edge edge : edges() )
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
		for(Edge edge : edges() )
		{
			if ( edge.getStart() == pNode && edge instanceof CallEdge )
			{
				callees.add(edge.getEnd());
			}
		}
		return callees;
	}
	
	/**
	 * @param pNode The node to test.
	 * @return True if pNode has a caller on the same implicit parameter node, false otherwise.
	 * @pre pNode != null
	 */
	public boolean isNested(CallNode pNode)
	{
		assert pNode != null;
		CallNode caller = getCaller(pNode);
		if( caller == null || pNode.getParent() == null )
		{
			return false;
		}
		return caller.getParent() == pNode.getParent();
	}
	
	/**
	 * @param pNode The node to test. Must have a caller. Can't be null
	 * @return true iif pNode is the first callee of its parent
	 */
	public boolean isFirstCallee(CallNode pNode)
	{
		assert pNode != null;
		CallNode caller = getCaller(pNode);
		assert caller != null;
		List<Node> callees = getCallees(caller);
		return callees.get(0) == pNode;
	}
	
	/**
	 * @param pNode The node to check.
	 * @return The node called before pNode by the parent. 
	 * @pre pNode !=null
	 * @pre getCaller(pNode) != null
	 * @pre !isFirstCallee(pNode)
	 */
	public CallNode getPreviousCallee(CallNode pNode)
	{
		assert pNode != null;
		CallNode caller = getCaller(pNode);
		assert caller != null;
		assert !isFirstCallee(pNode);
		List<Node> callees = getCallees(caller);
		int index = callees.indexOf(pNode);
		assert index >= 1;
		return (CallNode) callees.get(index-1);
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
		if( pNode instanceof CallNode )
		{
			for(Node child : getCallees(pNode))
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
