package haraldr.ui;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.components.ListData;
import haraldr.ui.components.UIComponent;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UIDropDownMenu;

import java.util.ArrayList;
import java.util.List;

public class UIHeader extends UIComponent
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private Vector4f color;

    private float currentButtonPosition;
    private List<UIDropDownMenu> menuButtons = new ArrayList<>();

    public UIHeader(UIContainer parent, int layerIndex, Vector2f position, Vector2f size, Vector4f color)
    {
        super(parent, layerIndex);

        setPosition(position);
        setSize(size);
        this.color = color;
        currentButtonPosition = position.getX();
    }

    public void addMenuButton(String name, ListData... listDataEntries)
    {
        UIDropDownMenu menuButton = new UIDropDownMenu(parent, 0, name, Vector2f.addX(position, currentButtonPosition), listDataEntries);
        menuButtons.add(menuButton);
        currentButtonPosition += menuButton.getName().getPixelWidth() + MENU_BUTTON_PADDING;
    }

    @Override
    public void draw(Batch2D batch)
    {
        batch.drawQuad(position, size, color);
    }
}