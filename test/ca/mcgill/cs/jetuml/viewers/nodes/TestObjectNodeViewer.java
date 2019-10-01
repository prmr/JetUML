/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.viewers.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ObjectDiagram;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;

public class TestObjectNodeViewer
{
	private ObjectNode aNode; 
	private FieldNode aField1;
	private FieldNode aField2;
	private ObjectDiagram aDiagram; 
	private final ObjectNodeViewer aViewer = new ObjectNodeViewer();
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aDiagram = new ObjectDiagram();
		aField1 = new FieldNode();
		aField2 = new FieldNode();
		aNode = new ObjectNode();
		aNode.attach(aDiagram);
		aField1.attach(aDiagram);
		aField2.attach(aDiagram);
	}
	
	@Test
	public void testGetSplitPosition_NoField()
	{
		assertEquals(5, aViewer.getSplitPosition(aNode));
	}
	
	@Test
	public void testGetSplitPosition_OneField()
	{
		aNode.addChild(aField1);
		assertEquals(11, aViewer.getSplitPosition(aNode));
	}
	
	@Test
	public void testGetSplitPosition_TwoFields()
	{
		aNode.addChild(aField1);
		aNode.addChild(aField2);
		aField2.setName("XXXXX");
		assertEquals(52, aViewer.getSplitPosition(aNode));
	}
	
//	@Test
//	public void testGetBounds_NoName()
//	{
//		aNode.setName("");
//		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
//		assertEquals(110, aViewer.getBounds(aNode).getWidth());
//		assertEquals(40, aViewer.getBounds(aNode).getHeight());
//	}
//	
//	@Test
//	public void testGetBounds_ShortName()
//	{
//		aNode.setName("X");
//		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
//		assertEquals(110, aViewer.getBounds(aNode).getWidth());
//		assertEquals(40, aViewer.getBounds(aNode).getHeight());
//	}
//	
//	@Test
//	public void testGetBounds_LongName()
//	{
//		aNode.setName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
//		assertTrue(aViewer.getBounds(aNode).getWidth() > 110);
//		assertEquals(40, aViewer.getBounds(aNode).getHeight());
//	}
}
