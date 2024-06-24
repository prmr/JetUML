# Dark Mode

## Scope

The Dark Mode feature concerns the core components responsible for the color scheme, and how they are integrated with the rest of the application.

## Design

-To the scene of the main stage and dialog stage
- GraphicsContext is responsible for the color of all diagram elements
- CSS is responsible for all UI components like the color of the layout, menus, and toolbar.

![JetUML Class Diagram](properties1.png)

### UI Controls 

- CSS is applied at the Scene level of the main JetUML stage and DialogStage. This ensures the styling will propagate to all children nodes of the Scene.
-CSS:
	-JetUML stage and DialogStage: simply add CSS to the scene by calling Scene#getStylesheets() on the respective stages.
	-DialogStage is a unique Stage for all dialogs. This is possible because there can only be one dialog open at a time, and different dialogs can appear on the stage by assigning themselves as the root of the DialogStage. However, modifying the DialogStage within a specific dialog should be done with care because the DialogStage is shared with all dialogs and unwanted changes can persist as different dialogs take control of the DialogStage.

![JetUML Class Diagram](properties1o.png)

### Diagram Elements

- GraphicsContext uses ColorScheme is an Enum class which defines the colors of various diagram elements such as the color of the canvas, fill color of nodes, and stroke color of text and edges, for each ColorScheme, LIGHT and DARK. to set the appropriate colors of diagram elements

-ColorScheme(Sequence Diagram):
	-For example, the sequence diagram below illustrates how in dark mode, the text is rendered in white:
	1. A StringRenderer object calls RenderingUtils#drawText. 
	2. The attributes of the GraphicsContext is saved.
	3. The font is set
	4. The Fill color is obtained by first retrieving the current ColorScheme (LIGHT or DARK), which depends on the user setting, and getting the stroke color of the text. In the case of text, its fill color is the stroke color of the ColorScheme.
	5. The text is rendered.
	6. The attributes of the GraphicsContext is restored to its state before the operation.
	
### Tool Bar Icons

-DiagramTabToolBar (Object or Sequence)
	- The background color and buttons are style by CSS
	- The icons of the toolbar buttons and the popup menu items, which appear when right-clicking on the canvas, are done using the GraphicsContext.


```java
private void recreateButtonIcons()
	{
		List<Node> toolBarItems = getItems();
		List<MenuItem> contextMenuItems = aPopupMenu.getItems();
		for( int i = 0; i < toolBarItems.size(); i++ )
		{
			ButtonBase button = (ButtonBase) toolBarItems.get(i);
			if( toolBarItems.get(i) instanceof SelectableToolButton toolButton && 
					toolButton.getPrototype().isPresent() )
			{
				button.setGraphic(aDiagramRenderer.createIcon(toolButton.getPrototype().get()));
				contextMenuItems.get(i).setGraphic(aDiagramRenderer.createIcon(toolButton.getPrototype().get()));
			}
		}
	}
```

***future changes need to consider whether dark mode feature will be relevant.