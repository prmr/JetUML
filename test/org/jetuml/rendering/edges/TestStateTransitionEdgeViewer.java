/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.rendering.edges;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.StateNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestStateTransitionEdgeViewer 
{
	private static String userDefinedFontName;
	private static int userDefinedFontSize;

	private StateNode aStateNode1 = new StateNode();
	private StateNode aStateNode2;
	private StateTransitionEdge aTransitionEdge = new StateTransitionEdge();
	private Diagram aDiagram = new Diagram(DiagramType.STATE);
	private StateTransitionEdgeRenderer aStateTransitionEdgeViewer = new StateTransitionEdgeRenderer(DiagramType.newRendererInstanceFor(aDiagram));
	
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
	
	@ParameterizedTest
	@CsvSource(value = {
			"apple banana orange kiwi peach grape raspberry, 1000, 100, 1", 
			"apple banana orange kiwi peach grape, 250, 100, 2",
			"apple banana orange kiwi peach grape, 200, 200, 3",
			"apple banana orange kiwi peach grape raspberry, 100, 0, 4"
	})
	public void testWrapLabelForEdgeBetweenTwoStates(String pString, int pDistanceInX, int pDistanceInY, int pExpectedNumberOfLines)
	{
		aStateNode2 = new StateNode();
		aStateNode2.translate(pDistanceInX, pDistanceInY);
		aTransitionEdge.setMiddleLabel(pString);
		aTransitionEdge.connect(aStateNode1, aStateNode2);
		String label = wrapLabel(aTransitionEdge);
		int numberOfLines = (int)label.chars().filter(c -> c == '\n').count() + 1;
		assertEquals(pExpectedNumberOfLines, numberOfLines);
	}

	private String wrapLabel(StateTransitionEdge pTransitionEdge) 
	{
		try 
		{
			Method method = StateTransitionEdgeRenderer.class.getDeclaredMethod("wrapLabel", StateTransitionEdge.class);
			method.setAccessible(true);
			String label = (String)method.invoke(aStateTransitionEdgeViewer, pTransitionEdge);
			return label;
		} 
		catch (ReflectiveOperationException e)
		{
			fail();
			return "";
		}
	}
}
