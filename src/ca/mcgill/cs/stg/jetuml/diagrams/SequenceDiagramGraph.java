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
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.ReturnEdge;
import ca.mcgill.cs.stg.jetuml.graph.ParentNode;

/**
 * A UML sequence diagram.
 */
public class SequenceDiagramGraph extends Graph
{
	private static final Node[] NODE_PROTOTYPES = new Node[]{new ImplicitParameterNode(), new CallNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[]{new CallEdge(), new ReturnEdge(), new NoteEdge()};
	
	@Override
	public boolean add(Node pNode, Point2D pPoint)
	{
		if(pNode instanceof CallNode) // must be inside an object
		{
			Collection<Node> nodes = getNodes();
			boolean inside = false;
			Iterator<Node> iter = nodes.iterator();
			while(!inside && iter.hasNext())
			{
				Node n2 = (Node)iter.next();
				if(n2 instanceof ImplicitParameterNode && n2.contains(pPoint)) 
				{
					inside = true;
					((CallNode)pNode).setImplicitParameter((ImplicitParameterNode)n2);
				}
			}
			if (!inside)
			{
				return false;
			}
		}

		if(!super.add(pNode, pPoint)) 
		{
			return false;
		}
		return true;
	}

	@Override
	public void removeEdge(Edge pEdge)
	{
		super.removeEdge(pEdge);
		if(pEdge instanceof CallEdge && ((ParentNode)pEdge.getEnd()).getChildren().size() == 0) 
		{
			removeNode(pEdge.getEnd());
		}		
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
         
			if(n instanceof CallNode && ((CallNode)n).getParent() == null)
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
	
	/**
	 * @param pTopLevelCalls an ArrayList of Nodes in the topLevel of Calls.
	 * @param pObjects an ArrayList of Nodes to work with.
	 * @param pGraphics2D Graphics2D from layout call.
	 * @param pGrid Grid from layout call.
	 */
	public void heightObjectLayout(ArrayList<Node> pTopLevelCalls, ArrayList<Node> pObjects, Graphics2D pGraphics2D, Grid pGrid)
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





