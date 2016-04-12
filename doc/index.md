# Architectural Description

## Introduction

JetUML is a simple UML editor developed exclusively as a pure-Java desktop application. Its main goal is to make the creation of simple UML diagrams as fast as possible, for classroom or professional use.

This project is originally based on the [Violet Project](http://www.horstmann.com/violet/). However, it was evolved from the original project prior to the [2.0 Release](http://alexdp.free.fr/violetumleditor/page.php) to retain the focus on the minimalist approach. The new name, JetUML, reflects the focus on the primary concern of the project: to support fast diagramming.

The goal of this document is to capture the major decisions related to the development of JetUML.

## Stakeholders

The development of JetUML recognizes the following categories stakeholders:

* **Lead Developer:** Moves this project forward.
* **Student Contributors:** Students who contribute to JetUML as part of a self-directed course.
* **External Contributors:** Anyone else who contributes something to JetUML.
* **Students:** Students learning to use UML, typically as part of a software design course.
* **Instructors:** Instructors using UML to teach software design or something similar.
* **Professional Users:** Anyone using JetUML as part of their work.
* **Learners:** Anyone studying the JetUML project in the hopes of learning something about Java development.

## Architectural Principles

The following principles guide the development of JetUML:

* **No dependencies:** The application depends on no external libraries. This decision is to minimize the development and evolution cost, minimize the risk of having to do effort-intensive library adaptations, and lower entry barriers for contributors.
* **Minimalist feature set:** The application only supports core UML diagramming features. 
* **Violate Encapsulation for Testing:** To goal for the design is to support the highest possible level of encapsulation, and this implies the most restrictive access modifiers. When necessary, the 
classes in the `test` source folders can use reflection to get around accessibility restrictions.

## Functional View
The functional view is split by functional concern.

 * [Frame management](functional/frameManagement.md)
 * [Graph management](functional/GraphManagement.md)
 * [Edge hierarchy](functional/EdgeHierarchy.md)
 * [Tool bar](functional/toolbar.md)
 
## Glossary
The glossary is limited to terms that are unique to JetUML or have a speific meaning in the context of the JetUML project.

**Diagram Type** One of the types of UML diagrams supported by JetUML, e.g., Class Diagram.

**Graph Frame** An internal frame that contains a UML diagram of any kind.

**Internal Frame** Generally a GUI window frame that is controlled by and within the boundaries of the main application window. In JetUML this means either the *Welcome Tab* or a *Graph Frame*.

**Tool Bar** The GUI component in charge of displaying a list of drawing tools and commands.

**Welcome Tab** The tab that comes up by default when JetUML is launched. It contains diagram creation shortcuts and shortcuts to open recently opened files.

