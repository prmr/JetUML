///*******************************************************************************
// * JetUML - A desktop application for fast UML diagramming.
// *
// * Copyright (C) 2020 by McGill University.
// *     
// * See: https://github.com/prmr/JetUML
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see http://www.gnu.org/licenses.
// *******************************************************************************/
//package org.jetuml.rendering;
//
//import static java.util.stream.Collectors.toList;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.jetuml.diagram.Diagram;
//import org.jetuml.diagram.Edge;
//import org.jetuml.diagram.Node;
//import org.jetuml.diagram.edges.CallEdge;
//import org.jetuml.diagram.nodes.CallNode;
//
///**
// * Semantic representation of a sequence diagram, with algorithms to build it from 
// * the basic diagram structure.
// */
//public final class CallGraph
//{
////	private final Diagram aDiagram;
////	private final Optional<CallGraphNode> aRoot;
////	
////	CallGraph(Diagram pDiagram)
////	{
//////		aDiagram = pDiagram;
////////		Optional<Node> root = findRoot();
//////		if(root.isEmpty())
//////		{
//////			aRoot = Optional.empty();
//////		}
//////		else
//////		{
//////			aRoot = Optional.of(buildSubtreeFor(root.get()));
//////		}
////	}
//
//	
//	
//	private CallGraphNode buildSubtreeFor(Node pNode)
//	{
//		assert pNode instanceof CallNode;
//		CallGraphNode result = new CallGraphNode((CallNode)pNode);
//		for( Node callee : getCallees(pNode))
//		{
//			result.addCallee(buildSubtreeFor(callee));
//		}
//		return result;
//	}
//	
////	/**
////	 * Returns the list of nodes directly called by pNode,
////	 * in the order of the call sequence.
////	 * 
////	 * @param pNode The node to obtain the callees for.
////	 * @return All Nodes pointed to by an outgoing edge starting
////	 *     at pNode, or the empty list if there are none.
////	 * @pre pNode != null && contains(pNode)
////	 */
////	private List<Node> getCallees(Node pNode)
////	{
////		assert pNode != null && aDiagram.contains(pNode);
////		return aDiagram.edges().stream()
////				.filter(CallEdge.class::isInstance)
////				.filter(edge -> edge.getStart() == pNode)
////				.map(Edge::getEnd)
////				.collect(toList());
////	}
////	
//	/**
//	 * @return The call nodes in call order.
//	 */
////	public List<Node> callSequence()
////	{
////		// Preorder traversal
////	}
//	
////	private List<Call>
//	
//	/*
//	 * The root of the call sequence is the call node without a callee
//	 */
////	private Optional<Node> findRoot()
////	{
////		Set<Node> calledNodes = aDiagram.edges().stream()
////				.filter(edge -> edge.getClass().isAssignableFrom(CallEdge.class)) // Includes subclasses, such as constructor edges
////				.map(Edge::getEnd)
////				.collect(Collectors.toSet());
////		return aDiagram.allNodes().stream()
////			.filter(node -> node.getClass() == CallNode.class)
////			.filter(node -> !calledNodes.contains(node))
////			.findFirst();
////	}
//	
//	private static class CallGraphNode
//	{
//		private final CallNode aCaller;
//		private final List<CallGraphNode> aCallees = new ArrayList<>();
//		
//		CallGraphNode(CallNode pCaller)
//		{
//			aCaller = pCaller;
//		}
//		
//		void addCallee(CallGraphNode pNode)
//		{
//			aCallees.add(pNode);
//		}
//	}
//}
