#version 460 core

#define MAX_DIRECTIONAL_LIGHTS 1
#define MAX_POINT_LIGHTS 3
#define MAX_SPOTLIGHTS 2

//Squeezing allowed!
struct DirectionalLight
{
    vec3 direction; //16    0   1
    vec3 color;     //16    16
    //Total 32 Pad 0 Size 32
};

struct PointLight
{
    vec3 position;      //16    0   1
    vec3 color;         //16    16  0
    float constant;     //4     28
    float linear;       //4     32
    float quadratic;    //4     36
    //Total 40 Pad 8 Size 48
};

struct Spotlight
{
    vec3 position;      //16    0   1
    vec3 direction;     //16    16  1
    vec3 color;         //16    32  0
    float innerCutOff;  //4     44
    float outerCutOff;  //4     48
    //Total 52 Pad 12 Size 64
};

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec2 a_TextureCoordinate;
layout(location = 3) in vec3 a_Tangent;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

layout(std140, binding = 1) uniform lightSetup
{
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    float numDirectionalLights;
    float numPointLights;
    float numSpotlights;
};

uniform vec3 viewPosition;

out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;
out vec3 v_ViewPosition;

out LIGHTING
{
    PointLight pointLights[MAX_POINT_LIGHTS];
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
} lighting;

flat out float v_NumPointLights;
flat out float v_NumDirectionalLights;
flat out float v_NumSpotlights;

void main()
{
    vec3 normal     = normalize(vec3(model * vec4(a_Normal, 0.0f)));
    vec3 tangent    = normalize(vec3(model * vec4(a_Tangent, 0.0f)));
    tangent = normalize(tangent - dot(tangent, normal) * normal); // Fixes potential weird edges
    vec3 bitangent  = normalize(cross(normal, tangent)); // Is already in camera space.
    mat3 TBN        = transpose(mat3(tangent, bitangent, normal));

    lighting.pointLights = pointLights;
    for (uint i = 0; i < numPointLights; ++i)
    {
        lighting.pointLights[i].position = TBN * pointLights[i].position;
    }

    lighting.directionalLights = directionalLights;
    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        lighting.directionalLights[i].direction = TBN * directionalLights[i].direction;
    }

    lighting.spotlights = spotlights;
    for (uint i = 0; i < numSpotlights; ++i)
    {
        lighting.spotlights[i].position = TBN * spotlights[i].position;
        lighting.spotlights[i].direction = TBN * spotlights[i].direction;
    }

    v_NumPointLights = numPointLights;
    v_NumDirectionalLights = numDirectionalLights;
    v_NumSpotlights = numSpotlights;

    v_TextureCoordinate = a_TextureCoordinate; // Not important for lighting, don't put in tangent space.
    v_FragmentPosition  = TBN * vec3((model * vec4(a_Position, 0.0f)));
    v_ViewPosition      = TBN * viewPosition;

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}