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
package ca.mcgill.cs.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPackageNode
{
	private PackageNode aPackage1;
	private PackageNode aPackage2;
	private ClassNode aClass1;
	private ClassNode aClass2;
	private ClassNode aClass3;
	
	@BeforeEach
	public void setup()
	{
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		aClass1 = new ClassNode();
		aClass2 = new ClassNode();
		aClass2 = new ClassNode();
		aClass3 = new ClassNode();
	}
	
	@Test
	public void testDefault()
	{
		assertEquals(0,aPackage1.getChildren().size());
		assertEquals("", aPackage1.getName().toString());
		assertFalse(aPackage1.hasParent());
	}
	
	@Test
	public void testAddChild()
	{
		aPackage1.addChild(aClass1);
		assertEquals( 1, aPackage1.getChildren().size());
		assertEquals( aPackage1, aClass1.getParent());
		assertEquals( aClass1, aPackage1.getChildren().get(0));
		
		aPackage1.addChild(aPackage2);
		assertEquals( 2, aPackage1.getChildren().size());
		assertEquals( aPackage1, aClass1.getParent());
		assertEquals( aPackage1, aPackage2.getParent());
		assertEquals( aClass1, aPackage1.getChildren().get(0));
		assertEquals( aPackage2, aPackage1.getChildren().get(1));
		
		aPackage1.addChild(1, aClass2);
		assertEquals( 3, aPackage1.getChildren().size());
		assertEquals( aPackage1, aClass1.getParent());
		assertEquals( aPackage1, aPackage2.getParent());
		assertEquals( aPackage1, aClass2.getParent());
		assertEquals( aClass1, aPackage1.getChildren().get(0));
		assertEquals( aClass2, aPackage1.getChildren().get(1));
		assertEquals( aPackage2, aPackage1.getChildren().get(2));
		
		aPackage2.addChild(aClass3);
		assertEquals( 3, aPackage1.getChildren().size());
		assertEquals( 1, aPackage2.getChildren().size());
		assertEquals( aClass3, aPackage2.getChildren().get(0));
		
		// Add class3 to package1, which should remove it from package2
		aPackage1.addChild(aClass3);
		assertEquals( 4, aPackage1.getChildren().size());
		assertEquals( aClass3, aPackage1.getChildren().get(3));
		assertEquals( aPackage1, aClass3.getParent());
		assertEquals( 0, aPackage2.getChildren().size());
	}
	
	@Test
	public void testRemoveChild()
	{
		aPackage1.addChild(aClass1);
		aPackage1.addChild(aPackage2);
		aPackage1.addChild(aClass2);
		
		aPackage1.removeChild(aPackage2);
		assertEquals( 2, aPackage1.getChildren().size());
		assertEquals( aClass1, aPackage1.getChildren().get(0));
		assertEquals( aClass2, aPackage1.getChildren().get(1));
		assertFalse( aPackage2.hasParent());
		
		aPackage1.removeChild(aClass1);
		assertEquals( 1, aPackage1.getChildren().size());
		assertEquals( aClass2, aPackage1.getChildren().get(0));
		assertFalse( aClass1.hasParent());
		
		aPackage1.removeChild(aClass2);
		assertEquals( 0, aPackage1.getChildren().size());
		assertFalse( aClass2.hasParent());
	}
	
	@Test 
	public void testClone()
	{
		aPackage1.setName("Package");
		PackageNode clone = aPackage1.clone();
		assertEquals(0,clone.getChildren().size());
		assertEquals("Package", clone.getName().toString());
		assertFalse(clone.hasParent());
		
		aPackage2.setName("p2");
		aClass1.setName("c1");
		aClass2.setName("c2");
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aClass1);
		aPackage2.addChild(aClass2);
		
		clone = aPackage1.clone();
		assertEquals(1, clone.getChildren().size());
		PackageNode p2Clone = (PackageNode) clone.getChildren().get(0);
		assertFalse( p2Clone == aPackage2 );
		assertEquals("p2", p2Clone.getName());
		assertEquals(2, p2Clone.getChildren().size());
		ClassNode c1Clone = (ClassNode) p2Clone.getChildren().get(0);
		assertEquals("c1", c1Clone.getName().toString());
		assertFalse(c1Clone == aClass1);
		ClassNode c2Clone = (ClassNode) p2Clone.getChildren().get(1);
		assertEquals("c2", c2Clone.getName().toString());
		assertFalse(c2Clone == aClass2);
	}
}
