package org.jetuml.geom;

public class XCoordinateStrategy implements CoordinateStrategy {
	@Override
	public int of(Point pPoint) {
		return pPoint.getX();
	}		
}
