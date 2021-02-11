package haraldr.ui;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FileDialogs
{
    public static String openFile(String prompt, String fileType)
    {
        String path;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer filterPatterns = stack.mallocPointer(1);
            filterPatterns.put(stack.UTF8("*." + fileType));
            filterPatterns.flip();
            path = TinyFileDialogs.tinyfd_openFileDialog(prompt, "", filterPatterns, "", false);
            if (path == null) path = "";
        }
        return path;
    }
}
