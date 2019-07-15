/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2019 by the contributors of the JetUML project.
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

/**
 * @author Martin P. Robillard
 */

package ca.mcgill.cs.jetuml.diagram.edges;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.edges.LabeledStraightEdgeView;
import javafx.scene.canvas.Canvas;

/**
 *  An edge that that represents a UML dependency
 *  between use cases.
 */
public final class UseCaseDependencyEdge extends AbstractEdge
{
	/**
	 * Type of use case dependency with corresponding edge label.
	 */
	public enum Type 
	{
		None(""), Include("\u00ABinclude\u00BB"), Extend("\u00ABextend\u00BB");
		
		private final String aLabel;
		
		Type(String pLabel)
		{ aLabel = pLabel; }
		
		public String getLabel()
		{ return aLabel; }
	}
	
	private Type aType = Type.None;
	
	/**
	 * Creates a general dependency.
	 */
	public UseCaseDependencyEdge()
	{}
	
	/**
	 * Creates a typed dependency.
	 * @param pType The type of dependency.
	 */
	public UseCaseDependencyEdge(Type pType)
	{
		aType = pType;
	}
	
	/**
	 * @return The type of this dependency edge.
	 */
	public Type getType()
	{
		return aType;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("Dependency Type", () -> aType, pType -> aType = Type.valueOf((String)pType));
	}
	
	@Override
	protected EdgeView generateView()
	{
		// Anonymous customization to distinguish between the two types of dependencies in the tool icon
		return new LabeledStraightEdgeView(this, LineStyle.DOTTED, ArrowHead.V, () -> aType.getLabel()) 
		{
			@Override
			public Canvas createIcon()
			{
				Canvas canvas = super.createIcon();
				final float scale = 0.75f;
				canvas.getGraphicsContext2D().scale(scale, scale);
				new StringViewer(StringViewer.Align.CENTER, false, false)
				  .draw(getIconTag(), canvas.getGraphicsContext2D(), new Rectangle(1, BUTTON_SIZE, 1, 1));
				return canvas;
			}
		};
	}
	
	private String getIconTag()
	{
		return aType.getLabel().substring(1, 2).toUpperCase();
	}
}
