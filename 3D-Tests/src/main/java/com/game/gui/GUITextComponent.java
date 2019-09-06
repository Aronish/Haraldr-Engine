package com.game.gui;

import com.game.graphics.font.Font;
import com.game.math.Vector3f;

public abstract class GUITextComponent extends GUIComponent {

    public GUITextComponent(Vector3f position, float scale) {
        super(position, scale);
    }

    public abstract Font.TextRenderData getTextRenderData();
}
