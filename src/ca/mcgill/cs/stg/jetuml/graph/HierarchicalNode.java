package ca.mcgill.cs.stg.jetuml.graph;

import java.util.List;

public interface HierarchicalNode extends Node
{
	HierarchicalNode clone();
	HierarchicalNode getParent();
	void setParent(HierarchicalNode pNode);
	List<HierarchicalNode> getChildren(); 
	void addChild(int pIndex, HierarchicalNode pNode); 
	void addChild(HierarchicalNode pNode);
	void removeChild(HierarchicalNode pNode);
}
