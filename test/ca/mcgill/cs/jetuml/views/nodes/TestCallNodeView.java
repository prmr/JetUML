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
package ca.mcgill.cs.jetuml.views.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestCallNodeView
{
	private ImplicitParameterNode aImplicitParameterNode1;
	private CallNode aCallNode1;
	private SequenceDiagram aDiagram;
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	@Before
	public void setup()
	{
		aDiagram = new SequenceDiagram();
		aImplicitParameterNode1 = new ImplicitParameterNode();
		aCallNode1 = new CallNode();
		aDiagram.addRootNode(aImplicitParameterNode1);
		aImplicitParameterNode1.addChild(aCallNode1);
	}
	
	@Test
	public void testGetBoundsSingleCallNode()
	{
		assertEquals(new Rectangle(32,80,16,30), aCallNode1.view().getBounds());
	}
	
	@Test
	public void testGetBoundsSelfCall()
	{
		CallNode inner = new CallNode();
		aImplicitParameterNode1.addChild(inner);
		CallEdge edge = new CallEdge();
		edge.connect(aCallNode1, inner, aDiagram);
		aDiagram.addEdge(edge);
		assertEquals(new Rectangle(32,80,16,60), aCallNode1.view().getBounds());
		assertEquals(new Rectangle(40,100,16,30), inner.view().getBounds());
	}
}
