/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.edges;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.Canvas;

/**
 * Utility class to create edge viewers.
 */
public final class EdgeViewerFactory
{
	private EdgeViewerFactory() {}
	
	/**
	 * @return A new EdgeViewer instance to be used with NoteEdge instances.
	 */
	public static EdgeViewer createNoteEdgeViewer()
	{
		return new StraightEdgeViewer(LineStyle.DOTTED, ArrowHead.NONE);
	}
	
	/**
	 * @return A new EdgeViewer instance to be used with UseCaseAssociationEdge instances.
	 */
	public static EdgeViewer createUseCaseAssociationEdgeViewer()
	{
		return new StraightEdgeViewer(LineStyle.SOLID, ArrowHead.NONE);
	}
	
	/**
	 * @return A new EdgeViewer instance to be used with UseCaseGeneralizationEdge instances.
	 */
	public static EdgeViewer createUseCaseGeneralizationEdgeViewer()
	{
		return new StraightEdgeViewer(LineStyle.SOLID, ArrowHead.TRIANGLE);
	}
	
	/**
	 * @return A new EdgeViewer instance to be used with UseCaseDependencyEdge instances.
	 */
	public static EdgeViewer createUseCaseDependencyEdgeViewer()
	{
		// Anonymous customization to distinguish between the two types of dependencies in the tool icon
		return new LabeledStraightEdgeViewer(LineStyle.DOTTED, ArrowHead.V,
				edge -> ((UseCaseDependencyEdge)edge).getType().getLabel())
		{
			@Override
			public Canvas createIcon(Edge pEdge)
			{
				Canvas canvas = super.createIcon(pEdge);
				final float scale = 0.75f;
				canvas.getGraphicsContext2D().scale(scale, scale);
				new StringViewer(StringViewer.Align.CENTER, false, false)
				.draw(getIconTag(pEdge), canvas.getGraphicsContext2D(), new Rectangle(1, BUTTON_SIZE, 1, 1));
				return canvas;
			}
			
			private String getIconTag(Edge pEdge)
			{
				return ((UseCaseDependencyEdge)pEdge).getType().getLabel().substring(1, 2).toUpperCase();
			}
		};
	}
	
	/**
	 * @return A new EdgeViewer instance to be used with ObjectReferenceEdge instances.
	 */
	public static EdgeViewer createObjectReferenceEdgeViewer()
	{
		return new ObjectReferenceEdgeViewer();
	}
	
	/**
	 * @return A new EdgeViewer instance to be used with ObjectCollaborationEdge instances.
	 */
	public static EdgeViewer createObjectCollaborationEdgeViewer()
	{
		return new LabeledStraightEdgeViewer(LineStyle.SOLID, ArrowHead.NONE, 
				edge -> ((ObjectCollaborationEdge)edge).getMiddleLabel());
	}
	
	/**
	 * @return A new EdgeViewer instance to be used with StateTransitionEdge instances.
	 */
	public static EdgeViewer createStateTransitionEdgeViewer()
	{
		return new StateTransitionEdgeViewer();
	}
}
