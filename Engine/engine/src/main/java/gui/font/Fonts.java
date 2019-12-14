package gui.font;

import main.Window;
import math.Vector3f;

public class Fonts
{
    //public static final Font ROBOTO_THIN_BLUE = new Font("fonts/Roboto-Thin.ttf", 50, new Vector3f(0.0f, 0.0f, 1.0f));
    //public static final Font THE_BOLD_FONT = new Font("fonts/theboldfont.ttf", 80, new Vector3f(0.0f, 0.0f, 1.0f));
    public static Font VCR_OSD_MONO_1;
    public static Font ROBOTO_REGULAR;

    public static void init(Window window)
    {
        ROBOTO_REGULAR = new Font("fonts/Roboto-Regular.ttf", 90, new Vector3f(0.0f, 0.0f, 1.0f), window);
        VCR_OSD_MONO_1 = new Font("fonts/VCR_OSD_MONO_1.001.ttf", 75, new Vector3f(0.0f, 0.0f, 1.0f), window);
    }
}