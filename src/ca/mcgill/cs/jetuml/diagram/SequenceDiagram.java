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
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;

/**
 * A UML sequence diagram.
 */
public final class SequenceDiagram extends Diagram
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
	 * at pNode, if there is one.
	 * @pre pNode != null && contains(pNode)
	 */
	public Optional<CallNode> getCaller(Node pNode)
	{
		assert pNode != null && contains(pNode);
		for( Edge edge : edges() )
		{
			if( edge.getEnd() == pNode  && edge instanceof CallEdge )
			{
				return Optional.of((CallNode) edge.getStart());
			}
		}
		return Optional.empty();
	}
	
	/**
	 * @param pNode The node to obtain the callees for.
	 * @return All Nodes pointed to by an outgoing edge starting
	 * at pNode, or null if there are none.
	 * @pre pNode != null && contains(pNode)
	 */
	public List<Node> getCallees(Node pNode)
	{
		assert pNode != null;
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
	 * @pre pNode != null && contains(pNode) && pNode.getParent() != null
	 */
	public boolean isNested(CallNode pNode)
	{
		assert pNode != null;
		Optional<CallNode> caller = getCaller(pNode);
		if( !caller.isPresent() )
		{
			return false;
		}
		return caller.get().getParent() == pNode.getParent();
	}
	
	/**
	 * @param pNode The node to test. Must have a caller. 
	 * @return true iif pNode is the first callee of its parent
	 * @pre pNode != null && getCaller(pNode).isPresent() && contains(pNode)
	 */
	public boolean isFirstCallee(CallNode pNode)
	{
		assert pNode != null;
		Optional<CallNode> caller = getCaller(pNode);
		assert caller.isPresent();
		List<Node> callees = getCallees(caller.get());
		return callees.get(0) == pNode;
	}
	
	/**
	 * @param pNode The node to check.
	 * @return The node called before pNode by the parent. 
	 * @pre pNode !=null
	 * @pre getCaller(pNode).isPresent()
	 * @pre !isFirstCallee(pNode)
	 * @pre contains(pNode)
	 */
	public CallNode getPreviousCallee(CallNode pNode)
	{
		assert pNode != null;
		Optional<CallNode> caller = getCaller(pNode);
		assert caller.isPresent();
		assert !isFirstCallee(pNode);
		List<Node> callees = getCallees(caller.get());
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
}
