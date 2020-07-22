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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestConstraintSet
{
	private Set<String> aMessages;
	
	private Constraint constraint(String pMessage, boolean pReturn)
	{
		return ()->
		{
			aMessages.add(pMessage);
			return pReturn;
		};
	}
	
	@BeforeEach
	public void setUp()
	{
		aMessages = new HashSet<>();
	}
	
	@Test
	public void testEmpty()
	{
		ConstraintSet constraints = new ConstraintSet();
		assertTrue(constraints.satisfied());
	}
	
	@Test
	public void testMergeIntoEmpty()
	{
		ConstraintSet set1 = new ConstraintSet();
		ConstraintSet set2 = new ConstraintSet(constraint("A", true), constraint("B", true));
		set1.merge(set2);
		set1.satisfied();
		assertEquals(2, aMessages.size());
		assertTrue(aMessages.contains("A"));
		assertTrue(aMessages.contains("B"));
	}
	
	@Test
	public void testMergeEmpty()
	{
		ConstraintSet set1 = new ConstraintSet();
		ConstraintSet set2 = new ConstraintSet(constraint("A", true), constraint("B", true));
		set2.merge(set1);
		set2.satisfied();
		assertEquals(2, aMessages.size());
		assertTrue(aMessages.contains("A"));
		assertTrue(aMessages.contains("B"));
	}
	
	@Test
	public void testMergeNonEmptyNonEmpty()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", true), constraint("Y", true));
		ConstraintSet set2 = new ConstraintSet(constraint("A", true), constraint("B", true));
		set2.merge(set1);
		set2.satisfied();
		assertEquals(4, aMessages.size());
		assertTrue(aMessages.contains("A"));
		assertTrue(aMessages.contains("B"));
		assertTrue(aMessages.contains("X"));
		assertTrue(aMessages.contains("Y"));
	}
	
	@Test
	public void testSatisfiedAllFalse()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", false), constraint("Y", false), constraint("Z", false));
		assertFalse(set1.satisfied());
	}
	
	@Test
	public void testSatisfiedSomeFalse()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", true), constraint("Y", true), constraint("Z", false));
		assertFalse(set1.satisfied());
	}
	
	@Test
	public void testSatisfiedTrue()
	{
		ConstraintSet set1 = new ConstraintSet(constraint("X", true), constraint("Y", true), constraint("Z", true));
		assertTrue(set1.satisfied());
	}
}
