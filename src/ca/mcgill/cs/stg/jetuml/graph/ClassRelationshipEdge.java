/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  An edge that is shaped like a line with up to 
 *  three segments with an arrowhead.
 */
public class ClassRelationshipEdge extends SegmentedLineEdge
{
	private BentStyle aBentStyle;
	
	/**
     *  Constructs a straight edge.
     */
	public ClassRelationshipEdge()
	{
		aBentStyle = BentStyle.STRAIGHT;
	}
	
	/**
	 * A segmented edge with a diamond arrowhead representing the aggregation of a class
	 * @return a new aggregation-styled edge
	 */
	public static ClassRelationshipEdge createAggregationEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.HVH);
		cre.setStartArrowHead(ArrowHead.DIAMOND);
		return cre;
	}
	
	/**
	 * A segmented edge with a v arrowhead representing an association between classes
	 * @return a new association-styled edge
	 */
	public static ClassRelationshipEdge createAssociationEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.HVH);
		cre.setEndArrowHead(ArrowHead.V);
		return cre;
	}
	
	/**
	 * A segmented edge with a black diamond arrowhead representing a composition of the class.
	 * @return a new composition-styled edge
	 */
	public static ClassRelationshipEdge createCompositionEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.HVH);
		cre.setStartArrowHead(ArrowHead.BLACK_DIAMOND);
		return cre;
	}
	
	/**
	 * A straight edge representing communication between two classes.
	 * @return a new communication-styled edge
	 */
	public static ClassRelationshipEdge createCommunicationEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.STRAIGHT);
		cre.setLineStyle(LineStyle.SOLID);
		cre.setEndArrowHead(ArrowHead.NONE);
		return cre;
	}
	
	/**
	 * A straight dotted edge with a v arrowhead representing dependency between classes.
	 * @return a new dependency-styled edge
	 */
	public static ClassRelationshipEdge createDependencyEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.STRAIGHT);
		cre.setLineStyle(LineStyle.DOTTED);
		cre.setEndArrowHead(ArrowHead.V);
		return cre;
	}
	
	/**
	 * A segmented solid edge with a triangle arrowhead representing inheritance from a class.
	 * @return a new inheritance-styled edge
	 */
	public static ClassRelationshipEdge createInheritanceEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.VHV);
		cre.setEndArrowHead(ArrowHead.TRIANGLE);
		return cre;
	}
	
	/**
	 * A segmented dotted edge with a triangle arrowhead representing implementation of an interface.
	 * @return a new interface-styled edge
	 */
	public static ClassRelationshipEdge createInterfaceInheritanceEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.VHV);
		cre.setLineStyle(LineStyle.DOTTED);
		cre.setEndArrowHead(ArrowHead.TRIANGLE);
		return cre;
	}
	
	/**
	 * A straight dotted edge with a v arrowhead and proper middle label representing extending the relation between two classes.
	 * @return a new extending-relationship-style edge
	 */
	public static ClassRelationshipEdge createExtendRelationEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.STRAIGHT);
		cre.setLineStyle(LineStyle.DOTTED);
		cre.setEndArrowHead(ArrowHead.V);
		cre.setMiddleLabel("\u00ABextend\u00BB");
		return cre;
	}
	
	/**
	 * A straight dotted edge with a v arrowhead and proper middle label representing including the relation between two classes.
	 * @return a new include-relationship-styled edge
	 */
	public static ClassRelationshipEdge createIncludeRelationEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.STRAIGHT);
	    cre.setLineStyle(LineStyle.DOTTED);
	    cre.setEndArrowHead(ArrowHead.V);
	    cre.setMiddleLabel("\u00ABinclude\u00BB");
	    return cre;
	}
	
	/**
	 * A straight edge with a triangle arrowhead representing including the generalization between two classes.
	 * @return a new generalization-styled edge
	 */
	public static ClassRelationshipEdge createGeneralizationEdge()
	{
		ClassRelationshipEdge cre =  new ClassRelationshipEdge();
		cre.setBentStyle(BentStyle.STRAIGHT);
	    cre.setLineStyle(LineStyle.SOLID);
	    cre.setEndArrowHead(ArrowHead.TRIANGLE);
		return cre;
	}
	
	/**
     *  Sets the bentStyle property.
     * @param pNewValue the bent style
     */
	protected void setBentStyle(BentStyle pNewValue)
	{ aBentStyle = pNewValue; }
   
	/**
     * Gets the bentStyle property.
     * @return the bent style
     */
	public BentStyle getBentStyle() 
	{ return aBentStyle; }
   
	@Override
	public ArrayList<Point2D> getPoints()
	{
		return aBentStyle.getPath(getStart().getBounds(), getEnd().getBounds());
   }
}
