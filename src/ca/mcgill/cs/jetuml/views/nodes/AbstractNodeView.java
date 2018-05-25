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
package ca.mcgill.cs.jetuml.views.nodes;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Basic services for drawing nodes.
 */
public abstract class AbstractNodeView implements NodeView
{
	public static final int SHADOW_GAP = 4;
	protected static final Color SHADOW_COLOR = Color.LIGHTGRAY;
	protected static final Color BACKGROUND_COLOR = Color.WHITE;
	protected static final double LINE_WIDTH = 0.6;
	
	private Node aNode;
	
	/**
	 * @param pNode The node to wrap.
	 */
	protected AbstractNodeView(Node pNode)
	{
		aNode = pNode;
	}
	
	/**
	 * @return The wrapped node.
	 */
	protected Node node()
	{
		return aNode;
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Paint oldFill = pGraphics.getFill();
		double oldLineWidth = pGraphics.getLineWidth();
		pGraphics.setLineWidth(LINE_WIDTH);
		pGraphics.translate(SHADOW_GAP, SHADOW_GAP);      
		fillShape(pGraphics, true);
		pGraphics.translate(-SHADOW_GAP, -SHADOW_GAP);
		fillShape(pGraphics, false);
		pGraphics.setFill(oldFill);
		pGraphics.setLineWidth(oldLineWidth);
	}
	
	/**
	 * Fills in shape of the node.
	 * @param pGraphics GraphicsContext in which to fill the shape.
	 * @param pShadow true when filling in the shadow of the shape.
	 */
	protected abstract void fillShape(GraphicsContext pGraphics, boolean pShadow);

	@Override
	public void layout(Diagram pGraph) {}

	@Override
	public void drawSelectionHandles(GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getBounds());		
	}
}
