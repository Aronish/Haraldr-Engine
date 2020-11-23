package haraldr.dockspace;

import haraldr.dockspace.uicomponents.Font;
import haraldr.dockspace.uicomponents.TextBatch;
import haraldr.dockspace.uicomponents.TextLabel;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class WindowHeader
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector2f position, size;
    private Vector4f color;

    private float currentButtonPosition;
    private List<MenuButton> menuButtons = new ArrayList<>();
    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);

    public WindowHeader(Vector2f position, float size, Vector4f color)
    {
        this.position = position;
        this.size = new Vector2f(size, 20f);
        this.color = color;
        currentButtonPosition = position.getX();

        addMenuButton("File");
        addMenuButton("Edit");
    }

    private void addMenuButton(String name)
    {
        MenuButton menuButton = new MenuButton(textBatch.createTextLabel(
                name,
                Vector2f.add(position, new Vector2f(currentButtonPosition + MENU_BUTTON_PADDING / 2f, 0f)),
                new Vector4f(1f)
        ));
        menuButtons.add(menuButton);
        currentButtonPosition += menuButton.name.getPixelWidth() + MENU_BUTTON_PADDING;
    }

    public void renderToBatch(Batch2D batch)
    {
        batch.drawQuad(position, size, color);
        float currentButtonPosition = position.getX();
        for (MenuButton menuButton : menuButtons)
        {
            float buttonWidth = menuButton.name.getPixelWidth() + MENU_BUTTON_PADDING;
            batch.drawQuad(Vector2f.add(position, new Vector2f(currentButtonPosition, position.getY())), new Vector2f(buttonWidth, size.getY()), new Vector4f(0.5f, 0.5f, 0.5f, 1f));
            currentButtonPosition += buttonWidth;
        }
    }

    public void renderText()
    {
        textBatch.render();
    }

    public Vector2f getSize()
    {
        return size;
    }

    public TextBatch getTextBatch()
    {
        return textBatch;
    }

    private static class MenuButton
    {
        private TextLabel name;

        private MenuButton(TextLabel name)
        {
            this.name = name;
        }
    }
}