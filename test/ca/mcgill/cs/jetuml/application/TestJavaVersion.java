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
	public void testNullaryConstructorCorrectJavaNoUpdate()
	{
		setJavaVersion("1.0");
		assertEquals("1.0.0", new JavaVersion().toString());
		setJavaVersion("1.0.1");
		assertEquals("1.0.1", new JavaVersion().toString());
		setJavaVersion("1.1");
		assertEquals("1.1.0", new JavaVersion().toString());
		setJavaVersion("1.1.2");
		assertEquals("1.1.2", new JavaVersion().toString());
		setJavaVersion("1.2");
		assertEquals("2.0.0", new JavaVersion().toString());
		setJavaVersion("1.2.3");
		assertEquals("2.3.0", new JavaVersion().toString());
		setJavaVersion("1.3.4");
		assertEquals("3.4.0", new JavaVersion().toString());
		setJavaVersion("1.5");
		assertEquals("5.0.0", new JavaVersion().toString());
		setJavaVersion("1.6.0");
		assertEquals("6.0.0", new JavaVersion().toString());
		setJavaVersion("1.6.0");
		assertEquals("6.0.0", new JavaVersion().toString());
		setJavaVersion("1.7.0");
		assertEquals("7.0.0", new JavaVersion().toString());
		setJavaVersion("1.8.0");
		assertEquals("8.0.0", new JavaVersion().toString());
		setJavaVersion("9");
		assertEquals("9.0.0", new JavaVersion().toString());
		setJavaVersion("9.0.1");
		assertEquals("9.0.1", new JavaVersion().toString());
		setJavaVersion("10.0.1");
		assertEquals("10.0.1", new JavaVersion().toString());
	}
	
	@Test
	public void testNullaryConstructorCorrectJavaWithUpdate()
	{
		setJavaVersion("1.4.2_03");
		assertEquals("4.2.3", new JavaVersion().toString());
		setJavaVersion("1.5.0_03");
		assertEquals("5.0.3", new JavaVersion().toString());
		setJavaVersion("1.6.0_17");
		assertEquals("6.0.17", new JavaVersion().toString());
		setJavaVersion("1.7.0_1");
		assertEquals("7.0.1", new JavaVersion().toString());
		setJavaVersion("1.8.0_171");
		assertEquals("8.0.171", new JavaVersion().toString());
	}
	
	@Test(expected=JavaVersionNotDetectedException.class)
	public void testEmptyVersionString()
	{
		setJavaVersion("");
		new JavaVersion();
	}
	
	@Test
	public void testCompareTo()
	{
		JavaVersion v1 = new JavaVersion(2,3,4);
		JavaVersion v2 = new JavaVersion(3,4,5);
		
		assertTrue(v1.compareTo(v2) < 0);
		assertTrue(v2.compareTo(v1) > 0);
		assertEquals(0, v1.compareTo(v1));
		assertEquals(0, v2.compareTo(v2));
	}
	
	private static void setJavaVersion(String pVersion)
	{
		System.setProperty("java.version", pVersion);
	}
}
