# Font Management

## Scope

The font management feature concerns how different fonts, their size, their style, and alignment are managed and rendered.

## Design

All of the text rendered in diagrams is done through `StringRenderer` objects.
`StringRenderer` objects are flyweight and differ only in their alignment and text decorations. This way, different diagram types can obtain a `StringRenderer` object that fits their needs for text to be aligned to a specific position, and different font styles such as bold, italic, and underlined.

`StringRenderer` interfaces with three classes to render text:
* `UserPreferences`: The singleton _model_ in the Model-View-Controller decomposition for user preferences. User preferences data is stored and managed in this class. `StringRenderer` can retrieve the user font and size from here.
* `FontMetrics`: A utility class to calculate various font metrics. `StringRenderer` obtains text measurements from `FontMetrics`.
* `RenderingUtils`: `StringRenderer` delegates the actual rendering of text on the GUI to this class. Thus, it's convenient to think of `StringRenderer` as an entity that gathers all necessary information about a text (its font, alignment, decorations, position on the `Canvas`), calibrates variables based on this information, and let's `RenderingUtils` make the method call to just render the text on the `Canvas`.  

The following class diagram illustrates the design of the font rendering system.

![JetUML Class Diagram](FontRenderingClass.png)



And the following sequence diagram illustrates a scenario where text in a `TypeNode` is rendered.

![JetUML Class Diagram](FontRenderingSequence.png)

1. A call is made to `StringRenderer` to draw a text by some node or edge renderer. In this case, it is the `TypeNodeRenderer`.
2. The `StringRenderer` object accesses `UserPreferences` to retrieve the font family and size that the text is to be rendered in. Using this information, and whether the `StringRenderer` object has text decorations bold, and/or italic, the corresponding Font is created.
3. `GraphicsContext#translate` positions itself on the `Canvas` at the indicated (x, y) coordinate (this is the top-left corner of the bounding rectangle containing the text).
4. `RenderingUtils#drawText` will render the text in the specified font, taking into account the offset necessary for a specific alignment (e.g. Text aligned at the center will need to be shifted to the the middle, since the `GraphicsContext` is positioned at the top-left corner of the text bounds).
5. The rest of the sequence diagram will execute if the text needs underlining. Because text is rendered on a `Canvas`, the underline must be drawn using `GraphicsContext`.
6. A call to `FontMetrics#getDimension` and `FontMetrics#getBaselineOffset` is made to calculate the length and position of the underline.
7. The underline is rendered in a similar fashion as the text.
8. The `GraphicsContext` assumes its initial position on the Canvas before the text was rendered.