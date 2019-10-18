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
package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public class TestDiagramTabToolBar
{
	private DiagramTabToolBar aToolbar;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aToolbar = new DiagramTabToolBar(new ClassDiagram());
	}
	
	@SuppressWarnings("unchecked")
	private SelectableToolButton getButtonAtPosition(int pPosition)
	{
		try
		{
			final List<Node> nodes = (List<Node>) ToolBar.class.getDeclaredMethod("getItems").invoke(aToolbar);
			return (SelectableToolButton) nodes.get(pPosition);
		}
		catch( ReflectiveOperationException exception )
		{
			exception.printStackTrace();
			fail();
		}
		return null;
	}
	
	/*
	 * Tests that an object has the selection tool as the first 
	 * button, and that it is selected.
	 */
	@Test
	public void testInit()
	{
		SelectableToolButton firstButton = getButtonAtPosition(0);
		assertEquals(RESOURCES.getString("toolbar.select.tooltip"), firstButton.getTooltip().getText());
		assertTrue(firstButton.isSelected());
	}
}
