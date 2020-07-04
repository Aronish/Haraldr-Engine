#shader frag
#version 460 core

#include "light_setup.glsl"

const float AMBIENT_STRENGTH = 0.01f;

struct MaterialProperties
{
    vec3 diffuseColor;
    float diffuseStrength;
    float specularStrength;
    float specularExponent;
    float opacity;
};

in vec3 v_Normal_W;
in vec3 v_Position_W;
in vec2 v_TextureCoordinate;

uniform vec3 u_ViewPosition_W;
uniform MaterialProperties u_MaterialProperties;

out vec4 o_Color;

#include "basic_lighting.glsl"

void main()
{
    vec3 viewDirection = normalize(u_ViewPosition_W - v_Position_W);
    vec3 normal = normalize(v_Normal_W);
    vec3 result;

    vec3 ambientColor = AMBIENT_STRENGTH * u_MaterialProperties.diffuseColor;
    vec3 diffuseColor = u_MaterialProperties.diffuseStrength * u_MaterialProperties.diffuseColor;

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight
        (
            pointLights[i].position.xyz, pointLights[i].color.rgb, pointLights[i].constant, pointLights[i].linear, pointLights[i].quadratic,
            ambientColor, diffuseColor, u_MaterialProperties.specularStrength, u_MaterialProperties.specularExponent,
            normal, viewDirection, v_Position_W
        );
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight
        (
            spotlights[i].position.xyz, spotlights[i].direction.xyz, spotlights[i].color.rgb, spotlights[i].innerCutoff, spotlights[i].outerCutoff,
            ambientColor, diffuseColor, u_MaterialProperties.specularStrength, u_MaterialProperties.specularExponent,
            normal, viewDirection, v_Position_W
        );
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight
        (
            directionalLights[i].direction.xyz, directionalLights[i].color.rgb,
            ambientColor, diffuseColor, u_MaterialProperties.specularStrength, u_MaterialProperties.specularExponent,
            normal, viewDirection, v_Position_W
        );
    }

    o_Color = vec4(result, u_MaterialProperties.opacity);
}