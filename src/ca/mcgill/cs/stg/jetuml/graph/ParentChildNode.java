package ca.mcgill.cs.stg.jetuml.graph;

/**
 * A node that can act as both a parent node and as a child node
 * in a diagram-specific parent-child relationship.
 * 
 * @author Martin P. Robilard.
 *
 */
public interface ParentChildNode extends ChildNode, ParentNode
{}
