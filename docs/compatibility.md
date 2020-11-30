# Compatibility Guide

Information on how diagrams saved with one version of the application can be read by other versions.

## Releases 3.0 and 3.1

When reading in a diagram from version 2.0-2.6, some conversions may be automatically applied to the structure of the diagram. 
These transformations target **Class diagrams** only.

* A Package element with both textual content and children elements will lose its textual content; 
* Dependency associations that have the same start and end elements are eliminated;
* The start label and end label property of dependency associations are eliminated; 
* Dependency associations between the same two given nodes, but in both directions, are replaced with a single 
   bidirectional edge whose label is the concatenation of the two original ones.
* The interface stereotype is eliminated from the name of interface nodes.
* All labels of Generalization edges are eliminated;

## Release 2.6

Starting with this release all saved diagrams are encoded in UTF-8. Any diagram saved with a version 2.1-2.5 that contains non-ASCII characters can be made compatible simply by changing the encoding with the help of a text editor.

The easiest way to do this is to create a new file set with UTF-8 encoding, and paste the content of the old file in.