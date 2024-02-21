package org.jetuml.geom;

public class YCoordinateStrategy implements CoordinateStrategy {
	@Override
	public int of(Point pPoint) {
		return pPoint.getY();
	}		
}
