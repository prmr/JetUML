# Font Rendering

## Scope

The font rendering feature concerns how different fonts, their size, their style, and alignment are managed and rendered by the font rendering system.

## Design

All of the text rendered in diagrams is done through `StringRenderer` objects.
`StringRenderer` objects are flyweight and differ only in their alignment and text decorations. This way, different diagram types can obtain a `StringRenderer` object that fits their needs for text to be aligned to a specific position, and in different font styles such as bold, italic, and underlined.

`StringRenderer` interfaces with three classes to render text:
* `UserPreferences`: The singleton _model_ in the Model-View-Controller decomposition for user settings. User setting data is stored and managed in this class. `StringRenderer` can retrieve the user font and size from here.
* `FontMetrics`: A utility class to calculate various font metrics. `StringRenderer` will obtain the appropriate positioning of text on the GUI from FontMetrics.
* `RenderingUtils`: `StringRenderer` delegates the actual rendering of text on the GUI to this class.

The following class diagram illustrates the design of the font rendering system.

![JetUML Class Diagram](FontRenderingClass.png)



And the following sequence diagram illustrates a scenario where text in a `TypeNode` is rendered.

![JetUML Class Diagram](FontRenderingSequence.png)

1. A call is made to StringRenderer to draw a text by some node or edge renderer.
2. The StringRenderer object accesses the UserPreferences object to retrieve the font family and size that the text is to be rendered in.
3. `GraphicsContext#translate` positions itself on the `Canvas` at the indicated (x, y) coordinate (this is the top-left corner of the bounding rectangle containing the text).
4. The actual rendering of the text is delegated to the RenderingUtils class which then makes a direct method call on the GraphicsContext object of the Canvas.
5. `RenderingUtils#drawText` will render the text in the specified font, taking into account the offset necessary for a specific alignment (e.g. Text aligned at the center will need to be shifted to the the middle, since the `GraphicsContext` is positioned at the top-left corner of the text bounds).
6. The bottom-half section of the sequence diagram will execute if the text needs underlining.
7. A call to `FontMetrics#getDimension` and `FontMetrics#getBaselineOffset` is made to calculate the position and length of the underline.
8. The line is rendered in a similar fashion as the text.
9. The `GraphicsContext` assumes its initial position on the Canvas before the text was rendered.