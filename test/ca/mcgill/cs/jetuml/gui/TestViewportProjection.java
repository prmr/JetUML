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
package ca.mcgill.cs.jetuml.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestViewportProjection
{
	@Test
	public void testGetWidthRatioEquals()
	{
		ViewportProjection projection = new ViewportProjection(1000, 500, 1000, 500, 0, 0);
		assertEquals(1.0, projection.getWidthRatio(), 0.0);
		assertEquals(1.0, projection.getHeightRatio(), 0.0);
	}
	
	@Test
	public void testGetWidthRatioSmaller1()
	{
		ViewportProjection projection = new ViewportProjection(1000, 500, 2000, 1000, 0, 0);
		assertEquals(0.5, projection.getWidthRatio(), 0.0);
		assertEquals(0.5, projection.getHeightRatio(), 0.0);
	}
	
	@Test
	public void testGetWidthRatioSmaller2()
	{
		ViewportProjection projection = new ViewportProjection(250, 250, 1000, 1000, 0, 0);
		assertEquals(0.25, projection.getWidthRatio(), 0.0);
		assertEquals(0.25, projection.getHeightRatio(), 0.0);
	}
	
	@Test
	public void testGetHiddenLeft()
	{
		ViewportProjection projection = new ViewportProjection(1000, 1000, 1000, 1000, 0, 0.343);
		assertEquals(0, projection.getHiddenLeft());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0, 0.343);
		assertEquals(0, projection.getHiddenLeft());
		projection = new ViewportProjection(500, 500, 1000, 1000, 1, 0.343);
		assertEquals(500, projection.getHiddenLeft());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.5, 0.343);
		assertEquals(250, projection.getHiddenLeft());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.25, 0.343);
		assertEquals(125, projection.getHiddenLeft());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.125, 0.343);
		assertEquals(63, projection.getHiddenLeft());
	}
	
	@Test
	public void testGetHiddenRight()
	{
		ViewportProjection projection = new ViewportProjection(1000, 1000, 1000, 1000, 0, 0.343);
		assertEquals(0, projection.getHiddenRight());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0, 0.343);
		assertEquals(500, projection.getHiddenRight());
		projection = new ViewportProjection(500, 500, 1000, 1000, 1, 0.343);
		assertEquals(0, projection.getHiddenRight());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.5, 0.343);
		assertEquals(250, projection.getHiddenRight());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.25, 0.343);
		assertEquals(375, projection.getHiddenRight());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.125, 0.343);
		assertEquals(438, projection.getHiddenRight());
	}
	
	@Test
	public void testGetHiddenTop()
	{
		ViewportProjection projection = new ViewportProjection(1000, 1000, 1000, 1000, 0.343, 0);
		assertEquals(0, projection.getHiddenTop());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0);
		assertEquals(0, projection.getHiddenTop());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 1);
		assertEquals(500, projection.getHiddenTop());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.5);
		assertEquals(250, projection.getHiddenTop());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.25);
		assertEquals(125, projection.getHiddenTop());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.125);
		assertEquals(63, projection.getHiddenTop());
	}
	
	@Test
	public void testGetHiddenBottom()
	{
		ViewportProjection projection = new ViewportProjection(1000, 1000, 1000, 1000, 0.343, 0);
		assertEquals(0, projection.getHiddenBottom());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0);
		assertEquals(500, projection.getHiddenBottom());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 1);
		assertEquals(0, projection.getHiddenBottom());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.5);
		assertEquals(250, projection.getHiddenBottom());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.25);
		assertEquals(375, projection.getHiddenBottom());
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.125);
		assertEquals(438, projection.getHiddenBottom());
	}
	
	@Test
	public void testIsHiddenLeft()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0, 0.343);
		assertFalse(projection.isHiddenLeft(0));
		assertFalse(projection.isHiddenLeft(1));
		assertFalse(projection.isHiddenLeft(999));
		assertFalse(projection.isHiddenLeft(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 1, 0.343);
		assertTrue(projection.isHiddenLeft(0));
		assertTrue(projection.isHiddenLeft(1));
		assertTrue(projection.isHiddenLeft(499));
		assertFalse(projection.isHiddenLeft(500));
		assertFalse(projection.isHiddenLeft(999));
		assertFalse(projection.isHiddenLeft(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.5, 0.343);
		assertTrue(projection.isHiddenLeft(0));
		assertTrue(projection.isHiddenLeft(1));
		assertTrue(projection.isHiddenLeft(249));
		assertFalse(projection.isHiddenLeft(250));
		assertFalse(projection.isHiddenLeft(251));
	}
	
	@Test
	public void testIsHiddenRight()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0, 0.343);
		assertFalse(projection.isHiddenRight(0));
		assertFalse(projection.isHiddenRight(1));
		assertFalse(projection.isHiddenRight(500));
		assertTrue(projection.isHiddenRight(501));
		assertTrue(projection.isHiddenRight(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 1, 0.343);
		assertFalse(projection.isHiddenRight(0));
		assertFalse(projection.isHiddenRight(1));
		assertFalse(projection.isHiddenRight(499));
		assertFalse(projection.isHiddenRight(500));
		assertFalse(projection.isHiddenRight(999));
		assertFalse(projection.isHiddenRight(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.5, 0.343);
		assertFalse(projection.isHiddenRight(0));
		assertFalse(projection.isHiddenRight(1));
		assertFalse(projection.isHiddenRight(249));
		assertFalse(projection.isHiddenRight(250));
		assertFalse(projection.isHiddenRight(251));
		assertFalse(projection.isHiddenRight(500));
		assertFalse(projection.isHiddenRight(750));
		assertTrue(projection.isHiddenRight(751));
	}
	
	@Test
	public void testIsHiddenTop()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0);
		assertFalse(projection.isHiddenTop(0));
		assertFalse(projection.isHiddenTop(1));
		assertFalse(projection.isHiddenTop(999));
		assertFalse(projection.isHiddenTop(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 1);
		assertTrue(projection.isHiddenTop(0));
		assertTrue(projection.isHiddenTop(1));
		assertTrue(projection.isHiddenTop(499));
		assertFalse(projection.isHiddenTop(500));
		assertFalse(projection.isHiddenTop(999));
		assertFalse(projection.isHiddenTop(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.5);
		assertTrue(projection.isHiddenTop(0));
		assertTrue(projection.isHiddenTop(1));
		assertTrue(projection.isHiddenTop(249));
		assertFalse(projection.isHiddenTop(250));
		assertFalse(projection.isHiddenTop(251));
	}
	
	@Test
	public void testIsHiddenBottom()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0);
		assertFalse(projection.isHiddenBottom(0));
		assertFalse(projection.isHiddenBottom(1));
		assertFalse(projection.isHiddenBottom(500));
		assertTrue(projection.isHiddenBottom(501));
		assertTrue(projection.isHiddenBottom(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 1);
		assertFalse(projection.isHiddenBottom(0));
		assertFalse(projection.isHiddenBottom(1));
		assertFalse(projection.isHiddenBottom(499));
		assertFalse(projection.isHiddenBottom(500));
		assertFalse(projection.isHiddenBottom(999));
		assertFalse(projection.isHiddenBottom(1000));
		
		projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0.5);
		assertFalse(projection.isHiddenBottom(0));
		assertFalse(projection.isHiddenBottom(1));
		assertFalse(projection.isHiddenBottom(249));
		assertFalse(projection.isHiddenBottom(250));
		assertFalse(projection.isHiddenBottom(251));
		assertFalse(projection.isHiddenBottom(500));
		assertFalse(projection.isHiddenBottom(750));
		assertTrue(projection.isHiddenBottom(751));
	}
	
	@Test
	public void testGetAdjustedHValueToRevealXNone()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0, 0.343);
		assertEquals(0, projection.getAdjustedHValueToRevealX(0), 0.0);
		assertEquals(0, projection.getAdjustedHValueToRevealX(499), 0.0);
		assertEquals(0, projection.getAdjustedHValueToRevealX(500), 0.0);
	}
	
	@Test
	public void testGetAdjustedHValueToRevealXMoveLeft()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 1, 0.343);
		assertEquals(0, projection.getAdjustedHValueToRevealX(0), 0.0);
		assertEquals(0.5, projection.getAdjustedHValueToRevealX(250), 0.0);
		assertEquals(0.25, projection.getAdjustedHValueToRevealX(125), 0.0);
		assertEquals(0.126, projection.getAdjustedHValueToRevealX(63), 0.0);
	}
	
	@Test
	public void testGetAdjustedHValueToRevealXMoveRight()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0, 0.343);
		assertEquals(0, projection.getAdjustedHValueToRevealX(0), 0.0);
		assertEquals(1, projection.getAdjustedHValueToRevealX(1000), 0.0);
		assertEquals(0.5, projection.getAdjustedHValueToRevealX(750), 0.0);
		assertEquals(0.75, projection.getAdjustedHValueToRevealX(875), 0.0);
		assertEquals(0.876, projection.getAdjustedHValueToRevealX(938), 0.0);
	}
	
	@Test
	public void testGetAdjustedVValueToRevealYNone()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0);
		assertEquals(0, projection.getAdjustedVValueToRevealY(0), 0.0);
		assertEquals(0, projection.getAdjustedVValueToRevealY(499), 0.0);
		assertEquals(0, projection.getAdjustedVValueToRevealY(500), 0.0);
	}
	
	@Test
	public void testGetAdjustedVValueToRevealYMoveUp()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 1);
		assertEquals(0, projection.getAdjustedVValueToRevealY(0), 0.0);
		assertEquals(0.5, projection.getAdjustedVValueToRevealY(250), 0.0);
		assertEquals(0.25, projection.getAdjustedVValueToRevealY(125), 0.0);
		assertEquals(0.126, projection.getAdjustedVValueToRevealY(63), 0.0);
	}
	
	@Test
	public void testGetAdjustedVValueToRevealYMoveDown()
	{
		ViewportProjection projection = new ViewportProjection(500, 500, 1000, 1000, 0.343, 0);
		assertEquals(0, projection.getAdjustedVValueToRevealY(0), 0.0);
		assertEquals(1, projection.getAdjustedVValueToRevealY(1000), 0.0);
		assertEquals(0.5, projection.getAdjustedVValueToRevealY(750), 0.0);
		assertEquals(0.75, projection.getAdjustedVValueToRevealY(875), 0.0);
		assertEquals(0.876, projection.getAdjustedVValueToRevealY(938), 0.0);
	}
	
	@Test
	public void testGetAdjustedVValueToRevealYMoveDown2()
	{
		ViewportProjection projection = new ViewportProjection(1585, 407, 1588, 782, 0, 0);
		assertEquals(0.152, projection.getAdjustedVValueToRevealY(464), 0.001);
	}
	
}
