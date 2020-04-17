#version 460 core

#define MAX_POINT_LIGHTS 15
#define MAX_SPOTLIGHTS 5
#define MAX_DIRECTIONAL_LIGHTS 1

/////LIGHT TYPES//////////////////////////////
//Squeezing allowed!
struct PointLight
{                       //std140        std430
    vec4 position;      //16    0   1   12
    vec4 color;         //16    16  0   12
    float constant;     //4     28      4
    float linear;       //4     32      4
    float quadratic;    //4     36      4
//std140: Total 40 Pad 8 Size 48
};

struct DirectionalLight
{                   //std140            std430
    vec4 direction; //16    0   1       12
    vec4 color;     //16    16          12
    //std140: Total 32 Pad 0 Size 32
};

struct Spotlight
{                       //std140        std430
    vec4 position;      //16    0   1   12
    vec4 direction;     //16    16  1   12
    vec4 color;         //16    32  0   12
    float innerCutOff;  //4     44      4
    float outerCutOff;  //4     48      4
    //std140: Total 52 Pad 12 Size 64
};

/////INPUT AND UNIFORMS/////////////////
layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec2 a_TextureCoordinate;
layout(location = 3) in vec3 a_Tangent;

uniform vec3 viewPosition;
uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

layout(std140, binding = 1) uniform lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    float numPointLights;
    float numSpotlights;
    float numDirectionalLights;
    float ambientStrength;
};

/////OUTPUT//////////////////
out tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
    vec3 spotlightPositions[MAX_SPOTLIGHTS];
    vec3 spotlightDirections[MAX_SPOTLIGHTS];
    vec3 directionalLightDirections[MAX_DIRECTIONAL_LIGHTS];
};
out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;
out vec3 v_ViewPosition;

void main()
{
    mat3 normalMatrix = mat3(model); // (Note: Tangent space vectors don't care about translation)
    vec3 normal     = normalize(normalMatrix * a_Normal);
    vec3 tangent    = normalize(normalMatrix * a_Tangent);
    tangent = normalize(tangent - dot(tangent, normal) * normal);       // Fixes potential weird edges
    vec3 bitangent  = normalize(cross(normal, tangent));                // Is already in camera space.
    mat3 TBN        = transpose(mat3(tangent, bitangent, normal));

    //Lights to tangent space
    for (uint i = 0; i < numPointLights; ++i)
    {
        pointLightPositions[i] = TBN * pointLights[i].position.xyz;
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        spotlightPositions[i] = TBN * spotlights[i].position.xyz;
        spotlightDirections[i] = TBN * spotlights[i].direction.xyz;
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        directionalLightDirections[i] = TBN * directionalLights[i].direction.xyz;
    }

    v_TextureCoordinate = a_TextureCoordinate;                          // Not important for lighting, don't put in tangent space.
    v_FragmentPosition  = TBN * vec3((model * vec4(a_Position, 1.0f))); // Need to be translated as well.
    v_ViewPosition      = TBN * viewPosition;

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}