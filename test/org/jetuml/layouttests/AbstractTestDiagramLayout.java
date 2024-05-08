/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2021 by McGill University.
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
package org.jetuml.layouttests;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.PropertyName;
import org.jetuml.diagram.edges.SingleLabelEdge;
import org.jetuml.diagram.nodes.NamedNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.persistence.PersistenceService;
import org.jetuml.persistence.PersistenceTestUtils;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.nodes.NoteNodeRenderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/*
 * Superclass for classes that test the layout of a given diagram.
 * Declares functionality to load the diagram, and convenience methods
 * to access diagram elements.
 */
public abstract class AbstractTestDiagramLayout
{	
	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	/**
	 * We add two pixels to the length of an edge to account for the stroke width and/or the arrow head.
	 */
	private static final int BUFFER = 2; 
	
	protected final Diagram aDiagram; 
	protected final DiagramRenderer aRenderer;
	
	AbstractTestDiagramLayout(Path pDiagramPath) throws IOException
	{
		aDiagram = PersistenceService.read(pDiagramPath.toFile());
		aRenderer = DiagramType.newRendererInstanceFor(aDiagram);
	}
	
	/**
	 * Ensures that pActual is either pExpected or within a BUFFER distance,
	 * inclusively.
	 * @param pExpected The value we expect.
	 * @param pActual The actual value.
	 */
	protected static void assertWithDefaultTolerance(int pExpected, int pActual)
	{
		assertTrue( (pActual <= pExpected + BUFFER) && (pActual >= pExpected - BUFFER));
	}
	
	/*
	 * Returns a named node with the matching name
	 */
	protected Node nodeByName(String pName)
	{
		return PersistenceTestUtils.getAllNodes(aDiagram).stream()
			.filter(node -> node instanceof NamedNode )
			.filter( node -> node.properties().get(PropertyName.NAME).get().equals(pName))
			.findFirst()
			.get();
	}
	
	/*
	 * Returns the edge with the corresponding middle label
	 */
	protected Edge edgeByMiddleLabel(String pLabel)
	{
		return aDiagram.edges().stream()
				.filter(edge -> edge instanceof SingleLabelEdge )
				.filter( edge -> edge.properties().get(PropertyName.MIDDLE_LABEL).get().equals(pLabel))
				.findFirst()
				.get();
	}
	
	/*
	 * Returns the edge with the corresponding type and kind.
	 * This assumes that the kind is available on the type.
	 */
	protected List<Edge> edgesByType(Class<?> pType)
	{
		return aDiagram.edges().stream()
				.filter(edge -> edge.getClass() == pType)
				.collect(Collectors.toUnmodifiableList());
	}
	
	/*
	 * Returns all the nodes of a certain type
	 */
	protected List<Node> nodesByType(Class<?> pType)
	{
		return PersistenceTestUtils.getAllNodes(aDiagram).stream()
				.filter(node -> node.getClass() == pType)
				.collect(Collectors.toUnmodifiableList());
	}
	
	protected static int getStaticIntFieldValue(Class<?> pClass, String pFieldName)
	{
		try 
		{
			Field field = pClass.getDeclaredField(pFieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(null);
			return fieldValue;
		} 
		catch (ReflectiveOperationException e)
		{
			assert false;
			fail();
			return -1;
		}
	}
	
	protected void verifyDefaultDimensions(Node pNode, int pDefaultWidth, int pDefaultHeight)
	{
		Rectangle bounds = aRenderer.getBounds(pNode);
		assertEquals(pDefaultWidth, bounds.width());
		assertEquals(pDefaultHeight, bounds.height());
	}
	
	protected static void verifyPosition(Node pNode, int pExpectedX, int pExpectedY)
	{
		assertEquals(pExpectedX, pNode.position().x());
		assertEquals(pExpectedY, pNode.position().y());
	}
	
	protected void verifyNoteNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(NoteNodeRenderer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(NoteNodeRenderer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
}
