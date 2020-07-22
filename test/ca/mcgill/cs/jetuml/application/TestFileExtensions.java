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
package ca.mcgill.cs.jetuml.application;

import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.assertThat;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasNoNullElements;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.DiagramType;
import javafx.stage.FileChooser.ExtensionFilter;

public class TestFileExtensions 
{
	@Test
	public void all() 
	{
		List<ExtensionFilter> filters = FileExtensions.all();
		assertThat(filters, hasSize, 7);
		assertThat(filters, hasNoNullElements );
	}
	
	@Test
	public void all_Values_size()
	{
		FileExtensions.all().forEach( ext -> assertEquals(1, ext.getExtensions().size() ));
	}
	
	@Test
	public void all_Values_order()
	{
		List<ExtensionFilter> filters = FileExtensions.all();
		assertEquals("*.jet", filters.get(0).getExtensions().get(0));
		assertEquals("*.class.jet", filters.get(1).getExtensions().get(0));
		assertEquals("*.sequence.jet", filters.get(2).getExtensions().get(0));
		assertEquals("*.state.jet", filters.get(3).getExtensions().get(0));
		assertEquals("*.object.jet", filters.get(4).getExtensions().get(0));
		assertEquals("*.usecase.jet", filters.get(5).getExtensions().get(0));
		assertEquals("*.*", filters.get(6).getExtensions().get(0));
	}
	
	@Test
	public void test_forDiagram()
	{
		for( DiagramType type : DiagramType.values() )
		{
			assertTrue(FileExtensions.all().contains(FileExtensions.forDiagramType(type)));
		}
	}
	
	@Test
	public void testClipApplicationExtension_noExtension()
	{
		File file = new File("XXX");
		assertSame(file, FileExtensions.clipApplicationExtension(file));
	}
	
	@Test
	public void testClipApplicationExtension_hasExtension()
	{
		File file = new File("XXX.jet");
		assertNotSame(file, FileExtensions.clipApplicationExtension(file));
		assertEquals(new File("XXX").getAbsolutePath(), FileExtensions.clipApplicationExtension(file).getAbsolutePath());
	}
}
