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
package ca.mcgill.jetuml.layouttests;

import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.edges.SingleLabelEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.NamedNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.persistence.PersistenceService;
import ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.NoteNodeViewer;

/*
 * Superclass for classes that test the layout of a given diagram.
 * Declares functionality to load the diagram, and convenience methods
 * to access diagram elements.
 */
public abstract class AbstractTestDiagramLayout
{	
	private static int userDefinedFontSize;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	/**
	 * We add two pixels to the length of an edge to account for the stroke width and/or the arrow head.
	 */
	private static final int BUFFER = 2; 
	
	protected final Diagram aDiagram; 
	
	AbstractTestDiagramLayout(Path pDiagramPath) throws IOException
	{
		aDiagram = PersistenceService.read(pDiagramPath.toFile()).diagram();
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
	
	protected static void verifyDefaultDimensions(Node pNode, int pDefaultWidth, int pDefaultHeight)
	{
		Rectangle bounds = NodeViewerRegistry.getBounds(pNode);
		assertEquals(pDefaultWidth, bounds.getWidth());
		assertEquals(pDefaultHeight, bounds.getHeight());
	}
	
	protected static void verifyPosition(Node pNode, int pExpectedX, int pExpectedY)
	{
		assertEquals(pExpectedX, pNode.position().getX());
		assertEquals(pExpectedY, pNode.position().getY());
	}
	
	protected static void verifyNoteNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(NoteNodeViewer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(NoteNodeViewer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
}
