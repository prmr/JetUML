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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ObjectDiagram;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestFieldAndObjectNodeView
{
	private ObjectNode aObjectNode1;
	private FieldNode aFieldNode1;
	private Graphics2D aGraphics;
	private ObjectDiagram aDiagram;
	
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
		aObjectNode1 = new ObjectNode();
		aFieldNode1 = new FieldNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aDiagram = new ObjectDiagram();
		aDiagram.addRootNode(aObjectNode1);
	}
	
	@Test
	public void testDimensionsUnattachedWithNameString()
	{
		aFieldNode1.setName("XXXXX");
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		assertEquals(47, view.leftWidth());    // The length of the string
		assertEquals(36, view.rightWidth());   // Half the default width + mid offset.
		assertEquals(22, view.getHeight());    // The height of the string
	}
	
	@Test
	public void testDimensionsUnattachedWithValueString()
	{
		aFieldNode1.setValue("XXXXX");
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		assertEquals(6, view.leftWidth());    	// Just the length of the mid offset
		assertEquals(47, view.rightWidth());  	// The length of the string
		assertEquals(22, view.getHeight());    	// The height of the string
	}
	
	@Test
	public void testGetBoundsUnattachedNoStrings()
	{
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		// x = axis (30) - offset (6)
		// y = 0
		// w = default length (30)/2 + 2* offset (6) = 42
		// h = default height = 20
		assertEquals( new Rectangle(24,0,42,20), view.getBounds());
	}
	
	@Test
	public void testGetBoundsUnattachedNameValueString()
	{
		aFieldNode1.setName("XXXXX");
		aFieldNode1.setValue("XXXXX");
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		// x = axis (30) - offset + length (47)  = -17
		// y = 0
		// w = 47 * 2
		// h = text height 22
		assertEquals( new Rectangle(-17,0,94,22), view.getBounds());
	}
	
	@Test
	public void testGetConnectionPointsUnattached()
	{
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		// x = half-point of the left side.
		// y = half-point of the default height
		assertEquals( new Point(55,10), view.getConnectionPoint(Direction.EAST));
	}
	
	// NEW
	
	@Test
	public void testDimensionsAttachedNoStrings()
	{
		aObjectNode1.addChild(aFieldNode1);
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		assertEquals(6, view.leftWidth());    // Just the length of the mid offset
		assertEquals(36, view.rightWidth());  // Half the default width + mid offset.
		assertEquals(20, view.getHeight());   // Default height
	}
	
	@Test
	public void testDimensionsAttachedObjectString()
	{
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.setName("XXXXXXXXXXXXXXXXXXX");
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		assertEquals(6, view.leftWidth());    // Just the length of the mid offset
		assertEquals(36, view.rightWidth());  // Half the default width + mid offset.
		assertEquals(20, view.getHeight());   // Default height
	}
	
	@Test
	public void testDimensionsAttachedWithNameString()
	{
		aObjectNode1.addChild(aFieldNode1);
		aFieldNode1.setName("XXXXX");
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		assertEquals(47, view.leftWidth());    // The length of the string
		assertEquals(36, view.rightWidth());   // Half the default width + mid offset.
		assertEquals(22, view.getHeight());    // The height of the string
	}
	
	@Test
	public void testDimensionsAttachedWithValueString()
	{
		aObjectNode1.addChild(aFieldNode1);
		aFieldNode1.setValue("XXXXX");
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		assertEquals(6, view.leftWidth());    	// Just the length of the mid offset
		assertEquals(47, view.rightWidth());  	// The length of the string
		assertEquals(22, view.getHeight());    	// The height of the string
	}
	
	@Test
	public void testGetBoundsAttachedNoStrings()
	{
		aObjectNode1.addChild(aFieldNode1);
		FieldNodeView view = (FieldNodeView) aFieldNode1.view();
		// x = axis (45) - offset (6) = 39
		// y = top node height
		// w = left + right
		// h = default height
		assertEquals( new Rectangle(39,70,42,20), view.getBounds());
	}
	
	@After
	public void teardown()
	{
		aGraphics.dispose();
	}
}
