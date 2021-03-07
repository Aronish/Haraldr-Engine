package haraldr.ui;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FileDialogs
{
    public static String openFile(String prompt, String fileType)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer filterPatterns = stack.mallocPointer(1);
            filterPatterns.put(stack.UTF8("*." + fileType));
            filterPatterns.flip();
            String path = TinyFileDialogs.tinyfd_openFileDialog(prompt, "", filterPatterns, "", false);
            return path == null ? "" : path;
        }
    }

    public static String saveFile(String prompt, String fileType)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer filterPatterns = stack.mallocPointer(1);
            filterPatterns.put(stack.UTF8("*." + fileType));
            filterPatterns.flip();
            String path = TinyFileDialogs.tinyfd_saveFileDialog(prompt, "", filterPatterns, "");
            return path == null ? "" : path;
        }
    }
}