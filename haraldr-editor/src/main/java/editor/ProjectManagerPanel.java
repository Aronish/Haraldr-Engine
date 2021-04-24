package editor;

import haraldr.debug.Logger;
import haraldr.dockspace.DockablePanel;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.FileDialogs;
import haraldr.ui.components.UIButton;
import haraldr.ui.components.UIInfoLabel;
import haraldr.ui.components.UIInputField;
import haraldr.ui.components.UILabeledList;
import haraldr.ui.groups.UIVerticalListGroup;
import haraldr.ui.groups.VerticalListInsertData;

import java.io.File;

public class ProjectManagerPanel extends DockablePanel<UIVerticalListGroup>
{
    private UIVerticalListGroup projectControls = new UIVerticalListGroup();
    private String selectedFolder = "C:\\";
    private UIInfoLabel selectedPath = new UIInfoLabel(uiLayerStack, 1, selectedFolder);

    public ProjectManagerPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
        UILabeledList controlsList = new UILabeledList(uiLayerStack, 1, new Vector2f(0f, headerSize.getY()), size);
        controlsList.addComponent("New project path ", selectedPath);
        UIButton findProjectFolder = new UIButton(uiLayerStack, 1, () ->
        {
            String path = FileDialogs.selectFolder("Create Project Folder");
            selectedPath.setValue(selectedFolder = path.isEmpty() ? "C:\\" : path);
        });
        controlsList.addComponent("Browse ", findProjectFolder);
        UIInputField<UIInputField.StringValue> projectNameInput = new UIInputField<>(uiLayerStack, 1, new UIInputField.StringValue(""), (projectName) ->
        {
            String currentPath = selectedFolder + "\\" + projectName;
            selectedPath.setValue(currentPath);
        });
        controlsList.addComponent("Project name ", projectNameInput);
        UIButton createProject = new UIButton(uiLayerStack, 1, () ->
        {
            File projectFolder = new File(selectedPath.getValue());
            if (!projectFolder.exists())
            {
                if (!projectFolder.mkdirs())
                {
                    Logger.error("Couldn't create project at path " + selectedPath.getValue());
                }
            }
        });
        controlsList.addComponent("Create project ", createProject);

        projectControls.addComponent(new VerticalListInsertData(controlsList));

        //uiLayerStack.getLayer(0).addComponent(new PanelModel());
        draw();
    }
}