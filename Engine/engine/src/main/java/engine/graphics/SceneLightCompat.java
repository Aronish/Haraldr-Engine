package engine.graphics;

public interface SceneLightCompat
{
    void updateBufferData(UniformBuffer lightSetup, int offset);
    void updateBufferDataUnsafe(UniformBuffer lightSetup, int offset);
}