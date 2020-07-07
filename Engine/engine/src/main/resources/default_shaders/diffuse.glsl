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
    vec3 viewPosition_W;
};

out vec3 v_Normal_W;
out vec3 v_Position_W;
out vec2 v_TextureCoordinate;

void main()
{
    mat3 normalMatrix   = mat3(model);
    v_Normal_W          = normalMatrix * a_Normal;
    v_Position_W        = (model * vec4(a_Position, 1.0f)).xyz;
    v_TextureCoordinate = a_TextureCoordinate;
    
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#switches
#include "light_setup.glsl"

const float AMBIENT_STRENGTH = 0.01f;

in vec3 v_Normal_W;
in vec3 v_Position_W;
in vec2 v_TextureCoordinate;

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

#ifdef TEXTURED
layout(location = 0) uniform sampler2D map_0_Diffuse_Texture;
#else
uniform vec3 u_Diffuse_Color;
#endif
uniform float u_Diffuse_Strength;
uniform float u_Specular_Strength;
uniform float u_Specular_Exponent;
uniform float u_Opacity;

out vec4 o_Color;

#include "basic_lighting.glsl"

void main()
{
    vec3 viewDirection = normalize(viewPosition_W - v_Position_W);
    vec3 normal = normalize(v_Normal_W);
    vec3 result;

#ifdef TEXTURED
    vec3 diffuseTextureColor = texture(map_0_Diffuse_Texture, v_TextureCoordinate).rgb;
    vec3 ambientColor = AMBIENT_STRENGTH  * diffuseTextureColor;
    vec3 diffuseColor = u_Diffuse_Strength * diffuseTextureColor;
#else
    vec3 ambientColor = AMBIENT_STRENGTH * u_Diffuse_Color;
    vec3 diffuseColor = u_Diffuse_Strength * u_Diffuse_Color;
#endif

    //Calculate light contribution
    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight
        (
            pointLights[i].position.xyz, pointLights[i].color.rgb, pointLights[i].constant, pointLights[i].linear, pointLights[i].quadratic,
            ambientColor, diffuseColor, u_Specular_Strength, u_Specular_Exponent,
            normal, viewDirection, v_Position_W
        );
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight
        (
            spotlights[i].position.xyz, spotlights[i].direction.xyz, spotlights[i].color.rgb, spotlights[i].innerCutoff, spotlights[i].outerCutoff,
            ambientColor, diffuseColor, u_Specular_Strength, u_Specular_Exponent,
            normal, viewDirection, v_Position_W
        );
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight
        (
            directionalLights[i].direction.xyz, directionalLights[i].color.rgb,
            ambientColor, diffuseColor, u_Specular_Strength, u_Specular_Exponent,
            normal, viewDirection, v_Position_W
        );
    }

    o_Color = vec4(result, u_Opacity);
}