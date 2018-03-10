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

package ca.mcgill.cs.jetuml.graph.nodes;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.edges.CallEdge;
import ca.mcgill.cs.jetuml.views.nodes.CallNodeView;
import ca.mcgill.cs.jetuml.views.nodes.CallNodeView2;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView2;

/**
 * A method call node in a sequence diagram. In addition to edges,
 * the node is linked to it callee and callers.
*/
public class CallNode extends AbstractNode implements ChildNode
{
	public static final int CALL_YGAP = 20;
	private static final int MIN_YGAP = 10;

	private ImplicitParameterNode aImplicitParameter;
	private boolean aOpenBottom;

	@Override
	protected NodeView generateView()
	{
		return new CallNodeView(this);
	}
	
	@Override
	protected NodeView2 generateView2()
	{
		return new CallNodeView2(this);
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.jetuml.graph.RectangularNode#translate(int, int)
	 */
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		// Prevent going above the ImplicitParameterNode
		// TODO Remove first clause once XML serialization is replaced.
		if( aImplicitParameter != null && view().getBounds().getY() < aImplicitParameter.getTopRectangle().getMaxY() + MIN_YGAP)
		{
			((CallNodeView)view()).setBounds(new Rectangle(view().getBounds().getX(), 
					Math.round(aImplicitParameter.getTopRectangle().getMaxY()) + MIN_YGAP, 
					view().getBounds().getWidth(), view().getBounds().getHeight()));
		}
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
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("openBottom", () -> aOpenBottom, pOpen -> aOpenBottom = (boolean) pOpen);
	}
	
	/**
	 * @param pGraph The graph containing the node.
	 * @return True if this node is signaled.
	 */
	public boolean isSignaled(Graph pGraph)
	{
		for( Edge edge : pGraph.getEdges(this))
		{
			if( edge instanceof CallEdge && edge.getEnd() == this && ((CallEdge)edge).isSignal())
			{
				return true;
			}
		}
		return false;
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
