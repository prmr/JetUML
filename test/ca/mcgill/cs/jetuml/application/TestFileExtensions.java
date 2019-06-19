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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.stage.FileChooser.ExtensionFilter;

public class TestFileExtensions 
{
	@Test
	public void testGetAll() 
	{
		List<ExtensionFilter> filters = FileExtensions.getAll();
		assertThat(filters.size(), equalTo(7));
		assertThat(filters, everyItem(notNullValue(ExtensionFilter.class)));
	}
	
	@Test
	public void testGetOnValidInput() 
	{
		assertThat(FileExtensions.get("Class Diagram Files"), notNullValue());
		assertThat(FileExtensions.get("JetUML Files"), notNullValue());
		assertThat(FileExtensions.get("All Files"), notNullValue());
	}
	
	@Test
	public void testGetOnInvalidInput() 
	{
		assertThat(FileExtensions.get(""), nullValue());
	}
}
