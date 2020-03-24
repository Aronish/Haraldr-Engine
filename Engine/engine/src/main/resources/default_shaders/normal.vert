#version 460 core

#define MAX_DIRECTIONAL_LIGHTS 1
#define MAX_POINT_LIGHTS 3
#define MAX_SPOTLIGHTS 3

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
layout(location = 2) in vec3 a_Tangent;
layout(location = 3) in vec2 a_TextureCoordinate;

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

out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;

out LIGHTING
{
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    float numDirectionalLights;
    float numPointLights;
    float numSpotlights;
} lighting;

void main()
{
    lighting.directionalLights = directionalLights;
    lighting.pointLights = pointLights;
    lighting.spotlights = spotlights;
    lighting.numDirectionalLights = numDirectionalLights;
    lighting.numPointLights = numPointLights;
    lighting.numSpotlights = numSpotlights;

    v_TextureCoordinate = a_TextureCoordinate;
    v_FragmentPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}