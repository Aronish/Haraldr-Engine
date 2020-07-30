package engine.graphics.lighting;

import engine.graphics.UniformBuffer;

public interface SceneLightCompat
{
    void updateBufferData(UniformBuffer lightSetup, int offset);
    void updateBufferDataUnsafe(UniformBuffer lightSetup, int offset);
}