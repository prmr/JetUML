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

import ca.mcgill.cs.jetuml.views.nodes.NodeView;
import ca.mcgill.cs.jetuml.views.nodes.NoteNodeView;

/**
 *  A note node in a UML diagram. The name of the node
 *  is the text of the note.
 */
public final class NoteNode extends NamedNode
{
	@Override
	protected NodeView generateView()
	{
		return new NoteNodeView(this);
	}
}
