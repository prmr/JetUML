package ca.mcgill.cs.jetuml.viewers.edges;

import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import javafx.geometry.Point2D;

public class TestAssociationEdgeViewer 
{
	private static int userDefinedFontSize;

	private AssociationEdgeViewer aAssociationEdgeViewer = new AssociationEdgeViewer();
	private ArrowHead aArrowHead = ArrowHead.DIAMOND;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@ParameterizedTest
	@CsvSource(value = {
			"apple banana orange kiwi peach grape raspberry, 1000, 100, 1", 
			"apple banana orange kiwi peach grape, 250, 100, 2",
			"apple banana orange kiwi peach grape, 200, 200, 3",
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
	
	@ParameterizedTest
	@CsvSource(value = {
			"0, 0, 0, 100, 15, 15, false, false, 9, 82",
			"0, 0, 0, 100, 15, 15, true, false, 3, 32",
			"0, 0, 0, 100, 1, 1, true, false, 3, 46",
			"100, 0, 0, 100, 1, 1, true, false, 53, 46",
			"0, 100, 0, 0, 1, 1, false, false, 9, 3",
			"100, 500, 200, 500, 15, 15, false, false, 182, 475",
			"100, 500, 200, 500, 15, 15, true, false, 143, 482",
			"100, 500, 200, 500, 1, 1, true, false, 150, 496",
			"100, 500, 200, 500, 150, 150, true, false, 203, 347",
			"100, 500, 200, 500, 10, 10, true, true, 145, 487",
			"100, 100, 100, 500, 10, 10, true, true, 103, 303"
	})
	public void testGetAttachmentPoint(int pPoint1X, int pPoint1Y, int pPoint2X, int pPoint2Y, 
			int pTextDimensionWidth, int pTextDimensionHeight, boolean pCenter, 
			boolean pIsStepUp, int pExpectedX, int pExpectedY)
	{
		Point2D point1 = new Point2D(pPoint1X, pPoint1Y);
		Point2D point2 = new Point2D(pPoint2X, pPoint2Y);
		Rectangle textBounds = new Rectangle(0, 0, pTextDimensionWidth, pTextDimensionHeight);
		Point2D result = getAttachmentPoint(point1, point2, aArrowHead, textBounds, pCenter, pIsStepUp);
		assertEquals(pExpectedX, (int)result.getX());
		assertEquals(pExpectedY, (int)result.getY());
	}
	
	/**
	 * Calls the private method SegmentedEdgeViewer.wrapLabel(...) with the given parameters. 
	 */
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
	
	/**
	 * Calls the private method SegmentedEdgeViewer.getAttachmentPoint(...) with the given parameters. 
	 */
	private Point2D getAttachmentPoint(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, Rectangle pDimension, boolean pCenter, boolean pIsStepUp) 
	{
		try 
		{
			Method method = SegmentedEdgeViewer.class.getDeclaredMethod("getAttachmentPoint", 
					Point2D.class, Point2D.class, ArrowHead.class, Rectangle.class, boolean.class, boolean.class);
			method.setAccessible(true);
			Point2D point = (Point2D)method.invoke(aAssociationEdgeViewer, 
					pEndPoint1, pEndPoint2, pArrow, pDimension, pCenter, pIsStepUp);
			return point;
		} 
		catch (ReflectiveOperationException e)
		{
			assert false;
			fail();
			return null;
		}
	}
}
