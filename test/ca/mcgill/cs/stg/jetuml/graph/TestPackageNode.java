package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.Direction;

public class TestPackageNode
{
	@Test
	public void testDefault()
	{
		PackageNode node = new PackageNode();
		assertEquals(new Rectangle2D.Double(0, 0, 100, 80), node.getBounds());
		assertEquals(0,node.getChildren().size());
		assertEquals(new Point2D.Double(100,40), node.getConnectionPoint(Direction.EAST));
		assertEquals(new Point2D.Double(0,40), node.getConnectionPoint(Direction.WEST));
		assertEquals(new Point2D.Double(50,0), node.getConnectionPoint(Direction.NORTH));
		assertEquals(new Point2D.Double(50,80), node.getConnectionPoint(Direction.SOUTH));
		assertEquals("", node.getContents().toString());
		assertEquals("", node.getName().toString());
		assertNull(node.getParent());
		assertEquals(new Rectangle2D.Double(0, 0, 100, 80), node.getShape().getBounds());
	}
}
