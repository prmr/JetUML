/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.AbstractGraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.nodes.NodeView2;

/**
 * Common elements for the Node hierarchy.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractNode extends AbstractGraphElement implements Node
{
	private NodeView2 aView2;
	private Point aPosition = new Point(0, 0);
	
	/**
	 * Calls an abstract delegate to generate the view for this node
	 * and positions the node at (0,0).
	 */
	protected AbstractNode()
	{
		aView2 = generateView2();
	}
	
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		aPosition = new Point( aPosition.getX() + pDeltaX, aPosition.getY() + pDeltaY );
	}
	
	/**
	 * Generates a view2 for this node. Because of cloning, this cannot
	 * be done in the constructor, because when a node is cloned a new 
	 * wrapper view2 must be produced for the clone.
	 * 
	 * @return The view2 that wraps this node.
	 */
	protected abstract NodeView2 generateView2();
	
	@Override
	public NodeView2 view2()
	{
		return aView2;
	}
	
	@Override
	public Point position()
	{
		return aPosition;
	}
	
	@Override
	public void moveTo(Point pPoint)
	{
		aPosition = pPoint;
	}

	@Override
	public AbstractNode clone()
	{
		AbstractNode clone = (AbstractNode) super.clone();
		clone.aView2 = clone.generateView2();
		return clone;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + view2().getBounds();
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().addInvisible("x", () -> aPosition.getX(), pX -> aPosition.setX((int)pX)); 
		properties().addInvisible("y", () -> aPosition.getY(), pY -> aPosition.setY((int)pY));
	}
}
