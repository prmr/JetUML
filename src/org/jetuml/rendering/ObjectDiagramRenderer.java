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

import org.jetuml.annotations.Singleton;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.viewers.edges.ObjectCollaborationEdgeViewer;
import org.jetuml.viewers.edges.ObjectReferenceEdgeViewer;
import org.jetuml.viewers.nodes.FieldNodeViewer;
import org.jetuml.viewers.nodes.ObjectNodeViewer;

/**
 * The renderer for object diagrams.
 */
@Singleton
public final class ObjectDiagramRenderer extends AbstractDiagramRenderer
{
	public static final ObjectDiagramRenderer INSTANCE = new ObjectDiagramRenderer();
	
	private ObjectDiagramRenderer()
	{
		addElementRenderer(FieldNode.class, new FieldNodeViewer());
		addElementRenderer(ObjectNode.class, new ObjectNodeViewer());
		addElementRenderer(ObjectReferenceEdge.class, new ObjectReferenceEdgeViewer());
		addElementRenderer(ObjectCollaborationEdge.class, new ObjectCollaborationEdgeViewer());
	}
}