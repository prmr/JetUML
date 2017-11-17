/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph.nodes;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import ca.mcgill.cs.stg.jetuml.graph.views.nodes.NodeView;
import ca.mcgill.cs.stg.jetuml.graph.views.nodes.PointNodeView;

/**
 *  An invisible node that is used in the toolbar to draw an
 *  edge, and in notes to serve as an end point of the node
 *  connector.
 */
public class PointNode extends AbstractNode2
{
	@Override
	protected NodeView generateView()
	{
		return new PointNodeView(this);
	}

	/**
	 * The persistence delegate recovers the position of the point.
	 * 
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(PointNode.class, new DefaultPersistenceDelegate()
		{
			protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
			{
				super.initialize(pType, pOldInstance, pNewInstance, pOut);
				int x = ((PointNode)pOldInstance).position().getX();
				int y = ((PointNode)pOldInstance).position().getY();
				pOut.writeStatement( new Statement(pOldInstance, "translate", new Object[]{ x, y }) );            
			}
		});
	}
}
