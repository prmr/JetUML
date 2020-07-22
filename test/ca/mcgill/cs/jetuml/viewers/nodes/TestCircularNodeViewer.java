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
package ca.mcgill.cs.jetuml.viewers.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestCircularNodeViewer
{
	private FinalStateNode aFinal; 
	private InitialStateNode aInitial;
	private final CircularStateNodeViewer aFinalViewer = new CircularStateNodeViewer(true);
	private final CircularStateNodeViewer aInitialViewer = new CircularStateNodeViewer(false);
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aFinal = new FinalStateNode();
		aInitial = new InitialStateNode();
	}
	
	@Test
	public void testGetBounds_NoName()
	{
		assertEquals(new Rectangle(0,0,20,20), aFinalViewer.getBounds(aFinal));
		assertEquals(new Rectangle(0,0,20,20), aInitialViewer.getBounds(aInitial));
	}
}
