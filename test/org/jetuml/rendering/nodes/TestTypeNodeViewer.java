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

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.jetuml.JavaFXLoader;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.TypeNode;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestTypeNodeViewer
{
	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	private static final TypeNodeRenderer aViewer = new TypeNodeRenderer(DiagramType.newRendererInstanceFor(new Diagram(DiagramType.CLASS)));
	private final Method aMethodNameBoxHeight;
	
	public TestTypeNodeViewer() throws ReflectiveOperationException
	{
		aMethodNameBoxHeight = TypeNodeRenderer.class.getDeclaredMethod("nameBoxHeight", 
				TypeNode.class, int.class, int.class);
		aMethodNameBoxHeight.setAccessible(true);
	}
	
	private int callNameBoxHeight(TypeNode pNode, int pAttributeBoxHeight, int pMethodBoxHeight)
	{
		try
		{
			return (int) aMethodNameBoxHeight.invoke(aViewer, pNode, pAttributeBoxHeight, pMethodBoxHeight);
		}
		catch( ReflectiveOperationException e )
		{
			fail("Reflection problem: " + e.getMessage());
			return -1;
		}
	}
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForTestBounds")
	@EnabledOnOs(OS.WINDOWS)
	public void testBounds(TypeNode pNode, Rectangle pOracle)
	{
		assertEquals(pOracle, aViewer.getBounds(pNode));
	}
	
	private static Stream<Arguments> provideArgumentsForTestBounds() {
	    return Stream.of(
	      createClassNode1(),
	      createClassNode2(),
	      createClassNode3(),
	      createClassNode4(),
	      createClassNode5(),
	      createClassNode6()
	    );
	}
	
	// At (0,0); name is just the interface prototype, no methods
	private static Arguments createClassNode1()
	{
		return Arguments.of(new ClassNode(), 
				new Rectangle(0,0, 100, 60)); // Default width and height
	}
	
	// At (10,20); name is just the interface prototype, no methods
	private static Arguments createClassNode2()
	{
		ClassNode node = new ClassNode();
		node.moveTo(new Point(10,20));
		return Arguments.of(node, 
				new Rectangle(10, 20, 100, 60)); // Default width and height, translated
	}
	
	// At (0,0), name is a single line
	private static Arguments createClassNode3()
	{
		ClassNode node = new ClassNode();
		node.setName(node.getName() + "NAME");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	// At (0,0) name is two lines, no methods
	private static Arguments createClassNode4()
	{
		ClassNode node = new ClassNode();
		node.setName(node.getName() + "NAME");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	// At (0,0) name is four lines, no methods
	private static Arguments createClassNode5()
	{
		ClassNode node = new ClassNode();
		node.setName("NAME1\nNAME2\nNAME3\nNAME4");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 76)); // Default width and additional height
	}

	// Name is just the interface prototype, one methods
	private static Arguments createClassNode6()
	{
		ClassNode node = new ClassNode();
		node.setMethods("METHODS");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	@Test
	public void testNameBoxHeight_OneLineName()
	{
		assertEquals(60, callNameBoxHeight(new InterfaceNode(), 0, 0));
	}
	
	@Test
	public void testNameBoxHeight_MultiLineName()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName("X\nX\nX\nX\nX");
		assertTrue(callNameBoxHeight(node, 0, 0) > 60);
	}
	
	@Test
	public void testNameBoxHeight_OneLineNameAndAttribute()
	{
		ClassNode node = new ClassNode();
		assertEquals(40, callNameBoxHeight(node, 20, 0));
	}
	
	@Test
	public void testNameBoxHeight_OneLineNameAndAttributeAndMethods()
	{
		ClassNode node = new ClassNode();
		assertEquals(20, callNameBoxHeight(node, 20, 40));
	}
}
