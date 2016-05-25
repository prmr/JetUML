# Selection

## Specifications
 * It shall be possible to select any node or edge by selecting it when the selection tool is active.
 * It shall be possible to select any number of nodes and edges by holding down the Ctrl key while selecting individual nodes and edges with the selection tool active; With the Ctrl key down in selection mode, selecting any already selected graph element shall remove it from the selection.
 * Dragging the mouse with the selection tool active shall select all nodes and edges completed included in the selection area.
 * Selecting a parent node shall automatically de-select any of its children nodes that might be selected, even if the Ctrl key is held down. It shall not be possible to select both a parent node and one of its child nodes. The children of a selected parent node are considered "selected" for all operations (copy, cut, move, etc.).
 
