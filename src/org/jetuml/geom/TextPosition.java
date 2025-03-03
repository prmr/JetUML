package org.jetuml.geom;

/**
 * How to position some text relative to a bounding box.
 */
public enum TextPosition
{
	TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

	/**
	 * @return true iif this object represents a top position.
	 */
	public boolean isTop()
	{
		return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT;
	}

	/**
	 * @return true iif this object represents a center position in the vertical axis.
	 */
	public boolean isVerticallyCentered()
	{
		return this == CENTER_LEFT || this == CENTER_CENTER || this == CENTER_RIGHT;
	}

	/**
	 * @return true iif this object represents a bottom position.
	 */
	public boolean isBottom()
	{
		return this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT;
	}

	/**
	 * @return true iif this object represents a left position.
	 */
	public boolean isLeft()
	{
		return this == TOP_LEFT || this == CENTER_LEFT || this == BOTTOM_LEFT;
	}

	/**
	 * @return true iif this object represents a centered position in the horizontal axis.
	 */
	public boolean isHorizontallyCentered()
	{
		return this == TOP_CENTER || this == CENTER_CENTER || this == BOTTOM_CENTER;
	}
}