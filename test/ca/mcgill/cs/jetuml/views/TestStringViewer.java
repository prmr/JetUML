package ca.mcgill.cs.jetuml.views;

import static ca.mcgill.cs.jetuml.testutils.GeometryUtils.osDependent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.views.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.views.StringViewer.TextDecoration;

public class TestStringViewer {
	
	private StringViewer topCenter;
	private StringViewer topCenterPadded;
	private StringViewer topCenterBold;
	private StringViewer bottomCenterPadded;
	
	@BeforeEach
	public void setup()
	{
		topCenter = StringViewer.get(Alignment.TOP_CENTER);
		topCenterPadded = StringViewer.get(Alignment.TOP_CENTER, TextDecoration.PADDED);
		topCenterBold = StringViewer.get(Alignment.TOP_CENTER, TextDecoration.BOLD);
		bottomCenterPadded = StringViewer.get(Alignment.BOTTOM_CENTER, TextDecoration.PADDED);
	}
	

	@Test
	public void testFlyweightProperty()
	{
		StringViewer stringViewer = StringViewer.get(Alignment.TOP_CENTER);
		
		assertNotSame(topCenterPadded, stringViewer);
		assertNotSame(bottomCenterPadded, stringViewer);
		assertSame(topCenter, stringViewer);
	}
	
	@Test
	public void testDimensionEmptyPaddedNoPaddedBold()
	{
		assertEquals(topCenter.getDimension(""), new Dimension(0, 0));
		assertEquals(topCenterPadded.getDimension(""), new Dimension(0, 0));
		assertEquals(topCenter.getDimension("Display String"), new Dimension(osDependent(69, 69, 69), osDependent(12, 12, 12)));
		assertEquals(topCenterBold.getDimension("Display String"), new Dimension(osDependent(69, 69, 69), osDependent(12, 12, 12)));
		assertEquals(topCenterPadded.getDimension("Display String"), new Dimension(osDependent(83, 83, 83), osDependent(26, 26, 26)));
	}
	
	
}
