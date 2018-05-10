/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

import static org.junit.Assert.*;

import org.junit.Test;

public class TestJavaVersion
{
	@Test
	public void testExplicitConstructor()
	{
		assertEquals("1.0.0", new JavaVersion(1,0,0).toString());
		assertEquals("1.5.0", new JavaVersion(1,5,0).toString());
		assertEquals("1.2.1", new JavaVersion(1,2,1).toString());
	}
	
	@Test
	public void testNullaryConstructorCorrectJava1()
	{
		setJavaVersion("1.0");
		assertEquals("1.0.0", new JavaVersion().toString());
		setJavaVersion("1.0.1");
		assertEquals("1.0.1", new JavaVersion().toString());
		setJavaVersion("1.1");
		assertEquals("1.1.0", new JavaVersion().toString());
		setJavaVersion("1.1.2");
		assertEquals("1.1.2", new JavaVersion().toString());
	}
	
	private static void setJavaVersion(String pVersion)
	{
		System.setProperty("java.version", pVersion);
	}
}
