/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
package org.jetuml.rendering;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.rendering.edges.ObjectCollaborationEdgeRenderer;
import org.jetuml.rendering.edges.ObjectReferenceEdgeRenderer;
import org.jetuml.rendering.nodes.FieldNodeRenderer;
import org.jetuml.rendering.nodes.ObjectNodeRenderer;

/**
 * The renderer for object diagrams.
 */
public final class ObjectDiagramRenderer extends AbstractDiagramRenderer
{
	/**
	 * @param pDiagram Diagram being rendered.
	 */
	public ObjectDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(FieldNode.class, new FieldNodeRenderer(this));
		addElementRenderer(ObjectNode.class, new ObjectNodeRenderer(this));
		addElementRenderer(ObjectReferenceEdge.class, new ObjectReferenceEdgeRenderer(this));
		addElementRenderer(ObjectCollaborationEdge.class, new ObjectCollaborationEdgeRenderer(this));
	}
}