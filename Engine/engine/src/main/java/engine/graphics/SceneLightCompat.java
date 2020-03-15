package engine.graphics;

import engine.math.Vector3f;

public interface SceneLightCompat
{
    void updateBufferData(UniformBuffer lightSetup, int offset);
    void updateBufferDataUnsafe(UniformBuffer lightSetup, int offset);
    Vector3f lightSetupMask = new Vector3f();
}