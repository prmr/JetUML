/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.nodes;

import ca.mcgill.cs.jetuml.views.nodes.ActorNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 *  An actor node in a use case diagram.
 */
public final class ActorNode extends NamedNode
{
	private static final String DEFAULT_NAME = "Actor";
	
	/**
     * Construct an actor node with a default size and name.
	 */
	public ActorNode()
	{
		setName(DEFAULT_NAME);
	}
	
	@Override
	protected NodeView generateView()
	{
		return new ActorNodeView(this);
	}
}
