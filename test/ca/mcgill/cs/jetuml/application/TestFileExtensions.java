/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018, 2019 by the contributors of the JetUML project.
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

import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.assertThat;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasNoNullElements;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.stage.FileChooser.ExtensionFilter;

public class TestFileExtensions 
{
	@Test
	public void testGetAll() 
	{
		List<ExtensionFilter> filters = FileExtensions.all();
		assertThat(filters, hasSize, 7);
		assertThat(filters, hasNoNullElements );
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"Class Diagram Files", "JetUML Files", "All Files"})
	public void testGetOnValidInput(String pExtensionDescription) 
	{
		assertNotNull(FileExtensions.get(pExtensionDescription));
	}
	
	@Test
	public void testGetOnInvalidInput() 
	{
		assertNull(FileExtensions.get(""));
	}
}
