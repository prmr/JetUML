package ca.mcgill.cs.jetuml.viewers.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javafx.geometry.Point2D;

public class TestAssociationEdgeViewer 
{
	private AssociationEdgeViewer aAssociationEdgeViewer = new AssociationEdgeViewer();
	
	@ParameterizedTest
	@CsvSource(value = {
			"apple banana orange kiwi peach grape raspberry, 1000, 100, 1", 
			"apple banana orange kiwi peach grape raspberry, 250, 100, 2",
			"apple banana orange kiwi peach grape raspberry, 200, 200, 3",
			"apple banana orange kiwi peach grape raspberry, 100, 0, 4"
	})
	public void testWrapLabel(String pString, int pDistanceInX, int pDistanceInY, int pExpectedNumberOfLines)
	{
		Point2D point1 = new Point2D(0.0, 0.0);
		Point2D point2 = new Point2D(pDistanceInX, pDistanceInY);
		String label = wrapLabel(pString, point1, point2);
		int numberOfLines = (int)label.chars().filter(c -> c == '\n').count() + 1;
		assertEquals(pExpectedNumberOfLines, numberOfLines);
	}
	
	private String wrapLabel(String pString, Point2D pPoint1, Point2D pPoint2) 
	{
		try 
		{
			Method method = SegmentedEdgeViewer.class.getDeclaredMethod("wrapLabel", String.class, Point2D.class, Point2D.class);
			method.setAccessible(true);
			String label = (String)method.invoke(aAssociationEdgeViewer, pString, pPoint1, pPoint2);
			return label;
		} 
		catch (ReflectiveOperationException e)
		{
			assert false;
			fail();
			return "";
		}
	}
}
