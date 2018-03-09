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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode2;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an actor in a use case diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class ActorNodeView2 extends RectangleBoundedNodeView2
{
	private static final int DEFAULT_WIDTH = 48;
	private static final int DEFAULT_HEIGHT = 64;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	
	// Stick man
	// CSOFF:
	private static final int GAP_ABOVE = 4;
	private static final int HEAD_SIZE = DEFAULT_WIDTH*4/12;
	private static final int BODY_SIZE = DEFAULT_WIDTH*5/12;
	private static final int LEG_SIZE  = DEFAULT_WIDTH*5/12;
	private static final int ARMS_SIZE = DEFAULT_WIDTH*6/12; 
	// CSON:
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ActorNodeView2(ActorNode2 pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private String name()
	{
		return ((ActorNode2)node()).getName();
	}
	
	@Override
	public void layout(Graph2 pGraph)
	{
		Rectangle top = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Rectangle bot = NAME_VIEWER.getBounds(name());
		Rectangle bounds = new Rectangle(getBounds().getX(), getBounds().getY(),
            Math.max(top.getWidth(), bot.getWidth()), top.getHeight() + bot.getHeight());
		setBounds(Grid.snapped(bounds));
	}
    
	@Override
	public void draw(GraphicsContext pGraphics)
	{	
		Rectangle bounds = getBounds();

		// Draw stick person
		GeneralPath path = new GeneralPath();
		float neckX = (float) (bounds.getX() + bounds.getWidth() / 2);
		float neckY = (float) (bounds.getY() + HEAD_SIZE + GAP_ABOVE);
		// head
		path.moveTo(neckX, neckY);
		path.quadTo(neckX + HEAD_SIZE / 2, neckY, neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE / 2);
		path.quadTo(neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX, neckY - HEAD_SIZE);
		path.quadTo(neckX - HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX-HEAD_SIZE / 2, neckY - HEAD_SIZE / 2);
		path.quadTo(neckX - HEAD_SIZE / 2, neckY, neckX, neckY);
		// body
		float hipX = neckX;
		float hipY = neckY + BODY_SIZE;
		path.lineTo(hipX, hipY);
		// arms
		path.moveTo(neckX - ARMS_SIZE / 2, neckY + BODY_SIZE / 3);
		path.lineTo(neckX + ARMS_SIZE / 2, neckY + BODY_SIZE / 3);
		// legs
		float dx = (float) (LEG_SIZE / Math.sqrt(2));
		float feetX1 = hipX - dx;
		float feetX2 = hipX + dx + 1;
		float feetY  = hipY + dx + 1;
		path.moveTo(feetX1, feetY);
		path.lineTo(hipX, hipY);
		path.lineTo(feetX2, feetY);

		pGraphics.draw(path);

		// Draw name
		Rectangle nameBox = NAME_VIEWER.getBounds(name());

		Rectangle namebox = new Rectangle(bounds.getX() + (int)((bounds.getWidth() - nameBox.getWidth()) / 2.0), 
				bounds.getY() + DEFAULT_HEIGHT, nameBox.getWidth(), nameBox.getHeight());
		NAME_VIEWER.draw(name(), pGraphics, namebox);
	}
	
	@Override
	public Shape getShape()
	{
		return Conversions.toRectangle2D(getBounds());
	}
}
