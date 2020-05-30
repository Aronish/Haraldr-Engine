#shader vert
#version 460 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec2 a_TextureCoordinate;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

out vec3 v_Normal;
out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;

void main()
{
    mat3 normalMatrix = mat3(model);
    v_Normal = normalMatrix * a_Normal;
    v_TextureCoordinate = a_TextureCoordinate;
    v_FragmentPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

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

/////INPUT AND UNIFORMS/////
in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform vec3 viewPosition;
/////MATERIAL/////////////////////////////////////////
layout(location = 0) uniform sampler2D diffuseTexture;
uniform MaterialProperties materialProperties;
/////OUTPUT//////
out vec4 o_Color;

#include "basic_lighting.glsl"

void main()
{
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 normal = normalize(v_Normal);
    vec3 result;

    vec3 diffuseTextureColor = texture(diffuseTexture, v_TextureCoordinate).rgb;
    vec3 ambientColor = AMBIENT_STRENGTH * materialProperties.diffuseColor * diffuseTextureColor;
    vec3 diffuseColor = materialProperties.diffuseStrength * materialProperties.diffuseColor * diffuseTextureColor;

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight
        (
            pointLights[i].position.xyz, pointLights[i].color.rgb, pointLights[i].constant, pointLights[i].linear, pointLights[i].quadratic,
            ambientColor, diffuseColor, materialProperties.specularStrength, materialProperties.specularExponent,
            normal, viewDirection
        );
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight
        (
            spotlights[i].position.xyz, spotlights[i].direction.xyz, spotlights[i].color.rgb, spotlights[i].innerCutoff, spotlights[i].outerCutoff,
            ambientColor, diffuseColor, materialProperties.specularStrength, materialProperties.specularExponent,
            normal, viewDirection
        );
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight
        (
            directionalLights[i].direction.xyz, directionalLights[i].color.rgb,
            ambientColor, diffuseColor, materialProperties.specularStrength, materialProperties.specularExponent,
            normal, viewDirection
        );
    }

    o_Color = vec4(result, materialProperties.opacity);
}