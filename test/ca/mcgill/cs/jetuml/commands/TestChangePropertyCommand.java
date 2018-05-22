/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.commands;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagram.Properties;

import static org.junit.Assert.*;

public class TestChangePropertyCommand 
{
    private Properties aProperties;
    private String aValue = "ONE";
    private ChangePropertyCommand aCommand;

    @Before
    public void setUp()
    {
        aProperties = new Properties();
        aProperties.add("test", () -> aValue, value -> aValue = (String) value);
        aCommand = new ChangePropertyCommand(aProperties.get("test"),  "ONE", "TWO");
    }

    @Test
    public void testExecuteUndo() 
    {
        aCommand.execute();
        assertEquals("TWO", aProperties.get("test").get());
        aCommand.undo();
        assertEquals("ONE", aProperties.get("test").get());
        aCommand.execute();
        assertEquals("TWO", aProperties.get("test").get());
        aCommand.undo();
        assertEquals("ONE", aProperties.get("test").get());
    }
}
