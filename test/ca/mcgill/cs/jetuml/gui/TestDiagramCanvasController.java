/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;

public class TestDiagramCanvasController
{
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	private static boolean creationEnabled(Optional<? extends DiagramElement> pElement, DiagramElement pTool)
	{
		try
		{
			Method method = DiagramCanvasController.class.getDeclaredMethod("creationEnabled", Optional.class, DiagramElement.class);
			method.setAccessible(true);
			return (Boolean) method.invoke(null, pElement, pTool);
		}
		catch( ReflectiveOperationException exception )
		{
			fail();
		}
		return false;
	}
	
	@Test
	public void testCreationEnabled_NoElement()
	{
		assertTrue(creationEnabled(Optional.empty(), new ClassNode()));
	}
	
	@Test
	public void testCreationEnabled_Package()
	{
		assertTrue(creationEnabled(Optional.of(new PackageNode()), new ClassNode()));
	}
	
	@Test
	public void testCreationEnabled_Object()
	{
		assertTrue(creationEnabled(Optional.of(new ObjectNode()), new FieldNode()));
	}
	
	@Test
	public void testCreationEnabled_False()
	{
		assertFalse(creationEnabled(Optional.of(new ClassNode()), new ClassNode()));
	}
}
