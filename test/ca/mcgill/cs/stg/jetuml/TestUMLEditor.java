/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml;

import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.UMLEditor;
import static org.junit.Assert.*;

public class TestUMLEditor
{
	@Test
	public void testIsOKJVMVersion()
	{
		assertTrue(UMLEditor.isOKJVMVersion("1.7.0_67"));
		assertTrue(UMLEditor.isOKJVMVersion("1.7.1_0"));
		assertTrue(UMLEditor.isOKJVMVersion("1.7.0"));
		assertFalse(UMLEditor.isOKJVMVersion("1.6.0_35"));
	}
}
