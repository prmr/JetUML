/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import javafx.stage.FileChooser.ExtensionFilter;

public class TestFileExtensions 
{
	@Test
	public void testGetAll() 
	{
		List<ExtensionFilter> filters = FileExtensions.getAll();
		assertEquals(7, filters.size());
		for(ExtensionFilter filter : filters) 
		{
			assertNotNull(filter);
		}
	}
	
	@Test
	public void testGetOnValidInput() 
	{
		assertNotNull(FileExtensions.get("Class Diagram Files"));
		assertNotNull(FileExtensions.get("JetUML Files"));
		assertNotNull(FileExtensions.get("All Files"));
	}
	
	@Test
	public void testGetOnInvalidInput() 
	{
		assertEquals(null, FileExtensions.get(""));
	}
}
