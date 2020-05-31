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

out vec3 v_Normal_W;
out vec3 v_WorldPosition_W;
out vec2 v_TextureCoordinate;

void main()
{
    mat3 normalMatrix = mat3(model);
    v_Normal_W = normalMatrix * a_Normal;
    v_WorldPosition_W = (model * vec4(a_Position, 1.0f)).xyz;
    v_TextureCoordinate = a_TextureCoordinate;
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
in vec3 v_Normal_W;
in vec3 v_WorldPosition_W;
in vec2 v_TextureCoordinate;

uniform vec3 u_ViewPosition_W;
/////MATERIAL/////////////////////////////////////////
layout(location = 0) uniform sampler2D diffuseTexture;
uniform MaterialProperties u_MaterialProperties;
/////OUTPUT//////
out vec4 o_Color;

#include "basic_lighting.glsl"

void main()
{
    vec3 viewDirection = normalize(u_ViewPosition_W - v_WorldPosition_W);
    vec3 normal = normalize(v_Normal_W);
    vec3 result;

    vec3 diffuseTextureColor = texture(diffuseTexture, v_TextureCoordinate).rgb;
    vec3 ambientColor = AMBIENT_STRENGTH * u_MaterialProperties.diffuseColor * diffuseTextureColor;
    vec3 diffuseColor = u_MaterialProperties.diffuseStrength * u_MaterialProperties.diffuseColor * diffuseTextureColor;

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight
        (
            pointLights[i].position.xyz, pointLights[i].color.rgb, pointLights[i].constant, pointLights[i].linear, pointLights[i].quadratic,
            ambientColor, diffuseColor, u_MaterialProperties.specularStrength, u_MaterialProperties.specularExponent,
            normal, viewDirection, v_WorldPosition_W
        );
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight
        (
            spotlights[i].position.xyz, spotlights[i].direction.xyz, spotlights[i].color.rgb, spotlights[i].innerCutoff, spotlights[i].outerCutoff,
            ambientColor, diffuseColor, u_MaterialProperties.specularStrength, u_MaterialProperties.specularExponent,
            normal, viewDirection, v_WorldPosition_W
        );
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight
        (
            directionalLights[i].direction.xyz, directionalLights[i].color.rgb,
            ambientColor, diffuseColor, u_MaterialProperties.specularStrength, u_MaterialProperties.specularExponent,
            normal, viewDirection, v_WorldPosition_W
        );
    }

    o_Color = vec4(result, u_MaterialProperties.opacity);
}