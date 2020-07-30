package haraldr.graphics.lighting;

import haraldr.graphics.UniformBuffer;

public interface SceneLightCompat
{
    void updateBufferData(UniformBuffer lightSetup, int offset);
    void updateBufferDataUnsafe(UniformBuffer lightSetup, int offset);
}