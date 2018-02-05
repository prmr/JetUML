/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Field;

import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Zoom;

public class TestZoom
{
	private static int MAX_LEVELS;
	private static double INCREMENT;
	
	static
	{
		try
		{
			Field field = Zoom.class.getDeclaredField("MAX_LEVELS");
			field.setAccessible(true);
			MAX_LEVELS = (int) field.get(null);
			field = Zoom.class.getDeclaredField("ZOOM_FACTOR_INCREMENT");
			field.setAccessible(true);
			INCREMENT = (double) field.get(null);
		}
		catch( NoSuchFieldException | IllegalAccessException e )
		{
			fail();
		}
		
	}
	
	@Test
	public void testInit()
	{
		Zoom zoom = new Zoom();
		assertEquals(1.0, zoom.factor(), 0.0);
	}
	
	@Test
	public void testZoomIn()
	{
		Zoom zoom = new Zoom();
		for( int level = 1; level <= MAX_LEVELS; level++ )
		{
			zoom.increaseLevel();
			assertEquals(INCREMENT * level, zoom.factor(), 0.0);
		}
		zoom.increaseLevel(); // should not increase because reached max
		assertEquals(INCREMENT * MAX_LEVELS, zoom.factor(), 0.0);
	}
	
	@Test
	public void testZoomOut()
	{
		Zoom zoom = new Zoom();
		for( int level = 1; level <= MAX_LEVELS; level++ )
		{
			zoom.decreaseLevel();
			assertEquals(1/(INCREMENT * level), zoom.factor(), 0.0);
		}
		zoom.decreaseLevel(); // should not increase because reached min
		assertEquals(1/(INCREMENT * MAX_LEVELS), zoom.factor(), 0.0);
	}
	
	@Test 
	public void testZoomPositive()
	{
		assumeTrue(INCREMENT == Math.sqrt(2));
		// If the original value of INCREMENT was modified, update
		// this test accordingly.
		// Tests that the rounding works as expected
		Zoom zoom = new Zoom();
		zoom.increaseLevel();
		assertEquals(1, zoom.zoom(1)); // Rounds downs from 1.41
		assertEquals(3, zoom.zoom(2)); // Rounds up from 2.82
		assertEquals(4, zoom.zoom(3)); // Rounds down from 4.24
		assertEquals(0, zoom.zoom(0)); // Zero never moves
	}
	
	@Test 
	public void testZoomNegative()
	{
		assumeTrue(INCREMENT == Math.sqrt(2));
		// If the original value of INCREMENT was modified, update
		// this test accordingly.
		// Tests that the rounding works as expected
		Zoom zoom = new Zoom();
		zoom.decreaseLevel();
		zoom.decreaseLevel();
		assertEquals(0, zoom.zoom(1)); // Rounds downs from 0.35
		assertEquals(1, zoom.zoom(2)); // Rounds up from 0.71
		assertEquals(1, zoom.zoom(3)); // Rounds down from 1.06
		assertEquals(2, zoom.zoom(5)); // Rounds up from 1.77
		assertEquals(0, zoom.zoom(0)); // Zero never moves
	}
	
	@Test 
	public void testDezoomPositive()
	{
		assumeTrue(INCREMENT == Math.sqrt(2));
		// If the original value of INCREMENT was modified, update
		// this test accordingly.
		// Tests that the rounding works as expected
		Zoom zoom = new Zoom();
		zoom.increaseLevel();
		assertEquals(1, zoom.dezoom(1)); // Rounds up from .71
		assertEquals(1, zoom.dezoom(2)); // Rounds down from 1.41
		assertEquals(2, zoom.dezoom(3)); // Rounds down from 2.12
		assertEquals(0, zoom.dezoom(0)); // Zero never moves
	}
	
	@Test 
	public void testDezoomNegative()
	{
		assumeTrue(INCREMENT == Math.sqrt(2));
		// If the original value of INCREMENT was modified, update
		// this test accordingly.
		// Tests that the rounding works as expected
		Zoom zoom = new Zoom();
		zoom.decreaseLevel();
		assertEquals(1, zoom.dezoom(1)); // Rounds downs from 1.41
		assertEquals(3, zoom.dezoom(2)); // Rounds up from 2.82
		assertEquals(4, zoom.dezoom(3)); // Rounds down from 4.24
		assertEquals(0, zoom.dezoom(0)); // Zero never moves
	}
	

}
