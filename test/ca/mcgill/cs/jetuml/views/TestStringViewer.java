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
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(osDependent(73, 69, 69), osDependent(13, 12, 12)), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(79, 69, 69), osDependent(13, 12, 12)), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(87, 83, 83), osDependent(27, 26, 26)), topCenterPadded.getDimension("Display String"));
	}
	
	
}
