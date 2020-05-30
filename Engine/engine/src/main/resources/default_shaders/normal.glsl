#shader vert
#version 460 core

#include "light_setup.glsl"

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

#shader frag
#version 460 core

#include "light_setup.glsl"

const float AMBIENT_STRENGTH = 0.01f;
const float DIFFUSE_STRENGTH = 1.0f;      //Just 1f I guess

struct MaterialProperties
{
    float specularStrength;
    float specularExponent;
    float opacity;
};

/////INPUT AND UNIFORMS//////////////
in tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
    vec3 spotlightPositions[MAX_SPOTLIGHTS];
    vec3 spotlightDirections[MAX_SPOTLIGHTS];
    vec3 directionalLightDirections[MAX_DIRECTIONAL_LIGHTS];
};
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;
in vec3 v_ViewPosition;

/////MATERIAL////////////////////////////////////////
layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D normalMap;
uniform MaterialProperties materialProperties;
/////OUTPUT//////
out vec4 o_Color;

#include "basic_lighting.glsl"

void main()
{
    vec3 normal = normalize(texture(normalMap, v_TextureCoordinate).rgb * 2.0f - 1.0f); // Read normals
    vec3 viewDirection = normalize(v_ViewPosition - v_FragmentPosition);
    vec3 result = vec3(0.0f);

    vec3 diffuseTextureColor = texture(diffuseTexture, v_TextureCoordinate).rgb;
    vec3 ambientColor = AMBIENT_STRENGTH * diffuseTextureColor;
    vec3 diffuseColor = DIFFUSE_STRENGTH * diffuseTextureColor;

    //Calculate light contribution
    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight
        (
            pointLightPositions[i], pointLights[i].color.rgb, pointLights[i].constant, pointLights[i].linear, pointLights[i].quadratic,
            ambientColor, diffuseColor, materialProperties.specularStrength, materialProperties.specularExponent,
            normal, viewDirection
        );
    }
    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight
        (
            spotlightPositions[i], spotlightDirections[i], spotlights[i].color.rgb, spotlights[i].innerCutoff, spotlights[i].outerCutoff,
            ambientColor, diffuseColor, materialProperties.specularStrength, materialProperties.specularExponent,
            normal, viewDirection
        );
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight
        (
            directionalLightDirections[i], directionalLights[i].color.rgb,
            ambientColor, diffuseColor, materialProperties.specularStrength, materialProperties.specularExponent,
            normal, viewDirection
        );
    }

    o_Color = vec4(result, materialProperties.opacity);
}