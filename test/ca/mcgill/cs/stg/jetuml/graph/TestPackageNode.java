package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.Direction;

public class TestPackageNode
{
	private PackageNode aPackage1;
	private PackageNode aPackage2;
	private ClassNode aClass1;
	private ClassNode aClass2;
	private ClassNode aClass3;
	
	@Before
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
		assertEquals(new Rectangle2D.Double(0, 0, 100, 80), aPackage1.getBounds());
		assertEquals(0,aPackage1.getChildren().size());
		assertEquals(new Point2D.Double(100,40), aPackage1.getConnectionPoint(Direction.EAST));
		assertEquals(new Point2D.Double(0,40), aPackage1.getConnectionPoint(Direction.WEST));
		assertEquals(new Point2D.Double(50,0), aPackage1.getConnectionPoint(Direction.NORTH));
		assertEquals(new Point2D.Double(50,80), aPackage1.getConnectionPoint(Direction.SOUTH));
		assertEquals("", aPackage1.getContents().toString());
		assertEquals("", aPackage1.getName().toString());
		assertNull(aPackage1.getParent());
		assertEquals(new Rectangle2D.Double(0, 0, 100, 80), aPackage1.getShape().getBounds());
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
		assertNull( aPackage2.getParent());
		
		aPackage1.removeChild(aClass1);
		assertEquals( 1, aPackage1.getChildren().size());
		assertEquals( aClass2, aPackage1.getChildren().get(0));
		assertNull( aClass1.getParent());
		
		aPackage1.removeChild(aClass2);
		assertEquals( 0, aPackage1.getChildren().size());
		assertNull( aClass2.getParent());
	}
}
