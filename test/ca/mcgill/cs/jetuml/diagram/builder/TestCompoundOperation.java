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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCompoundOperation
{
	private StringBuilder aBuilder;
	private CompoundOperation aOperation;
	
	@BeforeEach
	public void setUp()
	{
		aBuilder = new StringBuilder();
		aOperation = new CompoundOperation();
	}
	
	@Test
	public void testEmpty()
	{
		// Just test that it does not crash
		aOperation.execute();
		aOperation.undo();
	}
	
	@Test
	public void testSingle()
	{
		aOperation.add(new SimpleOperation(()-> aBuilder.append("A"), ()->aBuilder.deleteCharAt(0)));
		aOperation.execute();
		assertEquals("A", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
		aOperation.execute();
		assertEquals("A", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
	}
	
	@Test
	public void testDual()
	{
		aOperation.add(new SimpleOperation(()-> aBuilder.append("A"), ()->aBuilder.deleteCharAt(0)));
		aOperation.add(new SimpleOperation(()-> aBuilder.append("B"), ()->aBuilder.deleteCharAt(1)));
		aOperation.execute();
		assertEquals("AB", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
		aOperation.execute();
		assertEquals("AB", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
	}
	
	@Test
	public void testTriple()
	{
		aOperation.add(new SimpleOperation(()-> aBuilder.append("A"), ()->aBuilder.deleteCharAt(0)));
		aOperation.add(new SimpleOperation(()-> aBuilder.append("B"), ()->aBuilder.deleteCharAt(1)));
		aOperation.add(new SimpleOperation(()-> aBuilder.append("C"), ()->aBuilder.deleteCharAt(2)));
		aOperation.execute();
		assertEquals("ABC", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
		aOperation.execute();
		assertEquals("ABC", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
	}
	
	@Test
	public void testMultipleLevels()
	{
		aOperation.add(new SimpleOperation(()-> aBuilder.append("A"), ()->aBuilder.deleteCharAt(aBuilder.toString().length()-1)));
		CompoundOperation sub1 = new CompoundOperation();
		sub1.add(new SimpleOperation(()-> aBuilder.append("B"), ()->aBuilder.deleteCharAt(aBuilder.toString().length()-1)));
		sub1.add(new SimpleOperation(()-> aBuilder.append("C"), ()->aBuilder.deleteCharAt(aBuilder.toString().length()-1)));
		aOperation.add(sub1);
		CompoundOperation sub2 = new CompoundOperation();
		sub2.add(new SimpleOperation(()-> aBuilder.append("D"), ()->aBuilder.deleteCharAt(aBuilder.toString().length()-1)));
		sub2.add(new SimpleOperation(()-> aBuilder.append("E"), ()->aBuilder.deleteCharAt(aBuilder.toString().length()-1)));
		aOperation.add(sub2);
		aOperation.execute();
		assertEquals("ABCDE", aBuilder.toString());
		aOperation.undo();
		assertEquals("", aBuilder.toString());
	}
	
	@Test
	public void testUndoOrder()
	{
		aOperation.add(new SimpleOperation(()-> aBuilder.append("A"), ()->aBuilder.append("1")));
		CompoundOperation sub1 = new CompoundOperation();
		sub1.add(new SimpleOperation(()-> aBuilder.append("B"), ()->aBuilder.append("2")));
		sub1.add(new SimpleOperation(()-> aBuilder.append("C"), ()->aBuilder.append("3")));
		aOperation.add(sub1);
		CompoundOperation sub2 = new CompoundOperation();
		sub2.add(new SimpleOperation(()-> aBuilder.append("D"), ()->aBuilder.append("4")));
		sub2.add(new SimpleOperation(()-> aBuilder.append("E"), ()->aBuilder.append("5")));
		aOperation.add(sub2);
		aOperation.execute();
		assertEquals("ABCDE", aBuilder.toString());
		aOperation.undo();
		assertEquals("ABCDE54321", aBuilder.toString());
	}
	
	@Test
	public void testIsEmpty()
	{
		assertTrue(aOperation.isEmpty());
		aOperation.add(new SimpleOperation(()-> aBuilder.append("A"), ()->aBuilder.append("1")));
		assertFalse(aOperation.isEmpty());
	}
}
