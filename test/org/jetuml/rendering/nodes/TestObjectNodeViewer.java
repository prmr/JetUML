/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 *******************************************************************************/
package org.jetuml.rendering.nodes;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.jetuml.testutils.GeometryUtils.osDependent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.JavaFXLoader;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestObjectNodeViewer
{
	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	private ObjectNode aNode; 
	private FieldNode aField1;
	private FieldNode aField2;
	private final ObjectNodeRenderer aViewer = new ObjectNodeRenderer(DiagramType.newRendererInstanceFor(new Diagram(DiagramType.OBJECT)));
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aField1 = new FieldNode();
		aField1.setName("");
		aField1.setValue("");
		aField2 = new FieldNode();
		aField2.setName("");
		aField2.setValue("");
		aNode = new ObjectNode();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testGetSplitPosition_NoField()
	{
		assertEquals(5, ObjectNodeRenderer.getSplitPosition(aNode));
	}
	
	@Test
	public void testGetSplitPosition_OneField()
	{
		aNode.addChild(aField1);
		assertEquals(osDependent(12, 11, 12), ObjectNodeRenderer.getSplitPosition(aNode));
	}
	
	@Test
	public void testGetSplitPosition_TwoFields()
	{
		aNode.addChild(aField1);
		aNode.addChild(aField2);
		aField2.setName("XXXXX");
		assertEquals(osDependent(47, 49, 58), ObjectNodeRenderer.getSplitPosition(aNode));
	}
	
	@Test
	public void testGetYPosition_OneField()
	{
		aNode.addChild(aField1);
		assertEquals(70, ObjectNodeRenderer.getYPosition(aNode, aField1));
	}
	
	@Test
	public void testGetYPosition_TwoFields()
	{
		aNode.addChild(aField1);
		aNode.addChild(aField2);
		assertEquals(70, ObjectNodeRenderer.getYPosition(aNode, aField1));
		assertEquals(osDependent(95, 95, 101), ObjectNodeRenderer.getYPosition(aNode, aField2));
	}
	
	@Test
	public void testGetBounds_NoFieldNoName()
	{
		aNode.setName("");
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertEquals(80, aViewer.getBounds(aNode).getWidth());
		assertEquals(60, aViewer.getBounds(aNode).getHeight());
	}
	
	@Test
	public void testGetBounds_shortNameNoField()
	{
		aNode.setName("X");
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertEquals(80, aViewer.getBounds(aNode).getWidth());
		assertEquals(60, aViewer.getBounds(aNode).getHeight());
	}
	
	@Test
	public void testGetBounds_LongNameNoField()
	{
		aNode.setName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertTrue(aViewer.getBounds(aNode).getWidth() > 80);
		assertEquals(60, aViewer.getBounds(aNode).getHeight());
	}
	
	@Test
	public void testGetBounds_OneFieldNoName()
	{
		aNode.setName("");
		aNode.addChild(aField1);
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertEquals(80, aViewer.getBounds(aNode).getWidth());
		assertEquals(90, aViewer.getBounds(aNode).getHeight());
	}
	
	@Test
	public void testGetBounds_TwoFieldsShortName()
	{
		aNode.setName("X");
		aNode.addChild(aField1);
		aNode.addChild(aField2);
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertEquals(80, aViewer.getBounds(aNode).getWidth());
		assertEquals(120, aViewer.getBounds(aNode).getHeight());
	}
}
