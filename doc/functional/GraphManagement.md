# Graph Management

## Scope

The graph management concern relates to what objects control and query the state of the a `Graph` object.

## Design

A `Graph` instance is a collection of nodes and edges that constitutes a diagram of a specific type. A graph instance is initially
created in `EditorFrame.addGraphType` and passed to the constructor of an instance of `GraphFrame`, from where it is 
directly passed to the constructor of the `GraphPanel` embedded in the GraphFrame. For simplicity only 
the `GraphPanel` instance retains the reachable reference to a `Graph`.

It is possible to set a single `GraphModificationListener` for a graph. This interface corresponds to an 
Observer in the Observer design pattern. This object get notified of changes to the graph
such as addition and removal of nodes. This role is fulfilled by an instance of a member class of `GraphPanel`, which uses
the information to maintain an undo stack. There is no particular reason for the decision to support only a single graph observer/listener,
except that in the current design only one is needed.

![JetUML Class Diagram](GraphManagement1.png)


