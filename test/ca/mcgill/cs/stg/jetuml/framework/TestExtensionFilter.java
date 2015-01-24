/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.ExtensionFilter;

public class TestExtensionFilter
{
	@Test
	public void testBasicConstructor()
	{
		ExtensionFilter filter = new ExtensionFilter("", "");
		assertEquals("", filter.getDescription());
		assertEquals("", filter.getExtension());
		
		filter = new ExtensionFilter("Tar files", ".tar");
		assertEquals("Tar files", filter.getDescription());
		assertEquals(".tar", filter.getExtension());
	}
	
	@Test 
	public void testCompoundConstructor()
	{
		ExtensionFilter filter = new ExtensionFilter("Tar files", ".tar");
		assertEquals("Tar files", filter.getDescription());
		assertEquals(".tar", filter.getExtension());
		
		filter = new ExtensionFilter("Tar files", ".tar");
		assertEquals("Tar files", filter.getDescription());
		assertEquals(".tar", filter.getExtension());
		
		filter = new ExtensionFilter("Tar files", " .tar ");
		assertEquals("Tar files", filter.getDescription());
		assertEquals(" .tar ", filter.getExtension());
	}
	
	@Test 
	public void testAccept()
	{
		ExtensionFilter filter = new ExtensionFilter("", "");
		assertTrue(filter.accept(new File("test")));
		filter = new ExtensionFilter("Test files", ".txt");
		assertFalse(filter.accept(new File("README.md")));
		filter = new ExtensionFilter("Readme files", ".md");
		assertTrue(filter.accept(new File("README.md")));
	}
}
