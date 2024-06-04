# Font Rendering

## Scope

The font rendering feature concerns how different fonts, their size, their style, and alignment are managed and rendered by the font rendering system.

## Design

All of the text rendered in diagrams is done through `StringRenderer` objects.
`StringRenderer` objects are flyweight and differ only in their alignment and text decorations. This way, different diagram types can obtain a `StringRenderer` object that fits their needs for text to be aligned to a specific position, and in different font styles such as bold, italic, and underlined.

`StringRenderer` interfaces with three classes to render text:
* `UserPreferences`: The _model_ in the Model-View-Controller decomposition. User settings are stored and managed here. `StringRenderer` can retrieve the user font and size from here.
* `FontMetrics`: A utility class to calculate various font metrics. `StringRenderer` will obtain the appropriate positioning of text on the GUI from FontMetrics.
* `RenderingUtils`: `StringRenderer` delegates the actual rendering of text on the GUI to this class.

The following class diagram illustrates the design of the font rendering system.

![JetUML Class Diagram](FontRenderingClass.png)



And the following sequence diagram illustrates a scenario where text in a `TypeNode` is rendered.

![JetUML Class Diagram](FontRenderingSequence.png)

The chain of events of a text rendered through the font management system looks like this:
1. A call is made to StringRenderer to draw a text by some node or edge renderer.
2. The StringRenderer object accesses the UserPreferences object to retrieve the font family and size that the text is to be rendered in.
3. The calculation for the amount of space the text requires to be displayed on the screen is delegated to the FontMetrics utility class.
4. Using the dimension retrieved from FontMetrics, the draw method calibrates the position within the text bounds and the text decorations.
5. The actual rendering of the text is delegated to the RenderingUtils class which then makes a direct method call on the GraphicsContext object of the Canvas.