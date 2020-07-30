#define MAX_DIRECTIONAL_LIGHTS 1
#define MAX_POINT_LIGHTS 15
#define MAX_SPOTLIGHTS 5

struct PointLight
{
    vec4 position;
    vec4 color;
    float constant;
    float linear;
    float quadratic;
//std140: Total 40 Pad 8 Size 48
};

struct DirectionalLight
{
    vec4 direction;
    vec4 color;
//std140: Total 32 Pad 0 Size 32
};

struct Spotlight
{
    vec4 position;
    vec4 direction;
    vec4 color;
    float innerCutoff;
    float outerCutoff;
//std140: Total 52 Pad 12 Size 64
};

layout(std140, binding = 1) uniform lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    float numPointLights;
    float numSpotlights;
    float numDirectionalLights;
};