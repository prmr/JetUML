/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.geom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.rendering.Side;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestSide
{
	@Test
	@DisplayName("All sides of an empty rectangle at origin")
	void testWithEmptyRectangleAtOrigin()
	{
		Rectangle rectangle = new Rectangle(0, 0, 0, 0);
		assertEquals(new Line(0, 0, 0, 0), Side.TOP.getCorrespondingLine(rectangle));
		assertEquals(new Line(0, 0, 0, 0), Side.BOTTOM.getCorrespondingLine(rectangle));
		assertEquals(new Line(0, 0, 0, 0), Side.RIGHT.getCorrespondingLine(rectangle));
		assertEquals(new Line(0, 0, 0, 0), Side.LEFT.getCorrespondingLine(rectangle));
	}

	@Test
	@DisplayName("All sides of an empty rectangle not at origin")
	void testWithEmptyRectangleNotAtOrigin()
	{
		Rectangle rectangle = new Rectangle(1, 2, 0, 0);
		assertEquals(new Line(1, 2, 1, 2), Side.TOP.getCorrespondingLine(rectangle));
		assertEquals(new Line(1, 2, 1, 2), Side.BOTTOM.getCorrespondingLine(rectangle));
		assertEquals(new Line(1, 2, 1, 2), Side.RIGHT.getCorrespondingLine(rectangle));
		assertEquals(new Line(1, 2, 1, 2), Side.LEFT.getCorrespondingLine(rectangle));
	}

	@Test
	@DisplayName("For the top side")
	void testTop()
	{
		Rectangle rectangle = new Rectangle(10, 10, 60, 40);
		assertEquals(new Line(10, 10, 70, 10), Side.TOP.getCorrespondingLine(rectangle));
	}

	@Test
	@DisplayName("For the bottom side")
	void testBottom()
	{
		Rectangle rectangle = new Rectangle(10, 10, 60, 40);
		assertEquals(new Line(10, 50, 70, 50), Side.BOTTOM.getCorrespondingLine(rectangle));
	}

	@Test
	@DisplayName("For the right side")
	void testRight()
	{
		Rectangle rectangle = new Rectangle(10, 10, 60, 40);
		assertEquals(new Line(70, 10, 70, 50), Side.RIGHT.getCorrespondingLine(rectangle));
	}

	@Test
	@DisplayName("For the left side")
	void testLeft()
	{
		Rectangle rectangle = new Rectangle(10, 10, 60, 40);
		assertEquals(new Line(10, 10, 10, 50), (Side.LEFT.getCorrespondingLine(rectangle)));
	}
}
