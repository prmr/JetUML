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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.builder.ClassDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.ObjectDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.SequenceDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.StateDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.UseCaseDiagramBuilder;

public class TestDiagramType
{
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@Test
	public void testNewBuilderInstance()
	{
		assertSame(ClassDiagramBuilder.class, DiagramType.newBuilderInstanceFor(new Diagram(DiagramType.CLASS)).getClass());
		assertSame(ObjectDiagramBuilder.class, DiagramType.newBuilderInstanceFor(new Diagram(DiagramType.OBJECT)).getClass());
		assertSame(SequenceDiagramBuilder.class, DiagramType.newBuilderInstanceFor(new Diagram(DiagramType.SEQUENCE)).getClass());
		assertSame(StateDiagramBuilder.class, DiagramType.newBuilderInstanceFor(new Diagram(DiagramType.STATE)).getClass());
		assertSame(UseCaseDiagramBuilder.class, DiagramType.newBuilderInstanceFor(new Diagram(DiagramType.USECASE)).getClass());
	}
	
	@Test
	public void testFromName_Valid()
	{
		assertSame(DiagramType.CLASS, DiagramType.fromName(DiagramType.CLASS.getName()));
		assertSame(DiagramType.OBJECT, DiagramType.fromName(DiagramType.OBJECT.getName()));
		assertSame(DiagramType.SEQUENCE, DiagramType.fromName(DiagramType.SEQUENCE.getName()));
		assertSame(DiagramType.STATE, DiagramType.fromName(DiagramType.STATE.getName()));
		assertSame(DiagramType.USECASE, DiagramType.fromName(DiagramType.USECASE.getName()));
	}
	
	@Test
	public void testFromName_InvalidNotNull()
	{
		assertThrows(IllegalArgumentException.class, () -> DiagramType.fromName("XXX")); 
	}
	
	@Test
	public void testFromName_Null()
	{
		assertThrows(IllegalArgumentException.class, () -> DiagramType.fromName(null)); 
	}
}
