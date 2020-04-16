# Architectural Description

## Introduction

JetUML is a simple UML editor developed exclusively as a pure-Java desktop application. Its main goal is to make the creation of simple UML diagrams as fast as possible, for classroom or professional use.

This project is originally based on the [Violet Project](http://www.horstmann.com/violet/). However, it was evolved from the original project prior to the [2.0 Release](http://alexdp.free.fr/violetumleditor/page.php) to retain the focus on the minimalist approach. The new name, JetUML, reflects the focus on the primary concern of the project: to support fast diagramming.

The goal of this document is to capture the major decisions related to the development of JetUML.

## Architectural Principles

The following principles guide the development of JetUML:

* **No dependencies:** The application depends on no external libraries. This decision is to minimize the development and evolution cost, minimize the risk of having to do effort-intensive library adaptations, and lower entry barriers for contributors.
* **Minimalist feature set:** The application only supports core UML diagramming features. 
* **Violate Encapsulation for Testing:** To goal for the design is to support the highest possible level of encapsulation, and this implies the most restrictive access modifiers. When necessary, the 
classes in the `test` source folders can use reflection to get around accessibility restrictions.
* **No reflection:** To avoid fragile and hard-to-understand code, the project does not rely on any heavily-reflective framework, such as Javabeans. 
* **No streaming:** The use of [streaming](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html) is explicitly avoided in the interface of classes. JetUML has few data-intensive operations, as diagrams typically have only a handful of elements. In this context, the downsides of streaming (harder to debug, problems with checked exceptions, dual-paradigm design) are deemed to outweigh the advantages (more compact code). When appropriate, use of streams provided by API classes can be used if limited to the scope of a method.

## Functional View
The functional view is split by functional concern.

 * [Tab Management](functional/TabManagement.md)
 * [Diagram Element Properties](functional/properties.md)
 
