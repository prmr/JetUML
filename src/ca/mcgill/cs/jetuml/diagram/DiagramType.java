/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram;

import ca.mcgill.cs.jetuml.diagram.builder.ClassDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.ObjectDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.SequenceDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.StateDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.UseCaseDiagramBuilder;

import ca.mcgill.cs.jetuml.views.DiagramView;
import ca.mcgill.cs.jetuml.views.SequenceDiagramView;


/**
 * The different types of UML diagrams supported by 
 * this application.
 */
public enum DiagramType
{
	CLASS(ClassDiagram.class, ClassDiagramBuilder.class, DiagramView.class), 
	SEQUENCE(SequenceDiagram.class, SequenceDiagramBuilder.class, SequenceDiagramView.class), 
	STATE(StateDiagram.class, StateDiagramBuilder.class, DiagramView.class), 
	OBJECT(ObjectDiagram.class, ObjectDiagramBuilder.class, DiagramView.class), 
	USECASE(UseCaseDiagram.class, UseCaseDiagramBuilder.class, DiagramView.class);
	
	private final Class<?> aClass;
	private final Class<?> aBuilderClass;
	private final Class<?> aViewClass;
	
	DiagramType(Class<?> pClass, Class<?> pBuilderClass, Class<?> pViewClass)
	{
		aClass = pClass;
		aBuilderClass = pBuilderClass;
		aViewClass = pViewClass;
	}
	
	/**
	 * @param pDiagram The diagram whose type we want to check.
	 * @return The type of pDiagram.
	 * @pre pDiagram != null
	 */
	public static DiagramType typeOf(Diagram pDiagram)
	{
		assert pDiagram != null;
		for( DiagramType type : values())
		{
			if( pDiagram.getClass() == type.aClass )
			{
				return type;
			}
		}
		assert false;
		return null;
	}
	
	/**
	 * @return A new instance of the diagram type that corresponds to this value.
	 */
	public Diagram newInstance()
	{
		try
		{
			return (Diagram) aClass.getDeclaredConstructor().newInstance();
		}
		catch(ReflectiveOperationException exception)
		{
			assert false;
			return null;
		}
	}
	
	/**
	 * @param pDiagram The diagram for which we want to build a builder.
	 * @return A new instance of a builder for this diagram type.
	 * @pre pDiagram != null
	 */
	public static DiagramBuilder newBuilderInstanceFor(Diagram pDiagram)
	{
		assert pDiagram != null;
		try
		{
			return (DiagramBuilder) typeOf(pDiagram).aBuilderClass.getDeclaredConstructor(Diagram.class).newInstance(pDiagram);
		}
		catch(ReflectiveOperationException exception)
		{
			assert false;
			return null;
		}
	}
	
	/**
	 * @param pDiagram The diagram for which we want to create a view.
	 * @return A new instance of a view for this diagram type.
	 * @pre pDiagram != null
	 */
	public static DiagramView newViewInstanceFor(Diagram pDiagram)
	{
		assert pDiagram != null;
		try
		{
			return (DiagramView) typeOf(pDiagram).aViewClass.getDeclaredConstructor(Diagram.class).newInstance(pDiagram);
		}
		catch(ReflectiveOperationException exception)
		{
			assert false;
			return null;
		}
	}
	
	/**
	 * @return The name of the handler, which is the simple name of the corresponding
	 * class in all lower case.
	 */
	public String getName()
	{
		return aClass.getSimpleName().toLowerCase();
	}
}
