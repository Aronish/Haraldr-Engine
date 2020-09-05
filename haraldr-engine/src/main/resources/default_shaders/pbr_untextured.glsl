#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

out vec3 v_Normal_W;
out vec3 v_Position_W;

void main()
{
    mat3 normalMatrix   = mat3(model);
    vec3 normal         = normalize(normalMatrix * a_Normal);

    v_Normal_W          = normal;
    v_Position_W        = vec3((model * vec4(a_Position, 1.0f)));

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#include "light_setup.glsl"

in vec3 v_Normal_W;
in vec3 v_Position_W;

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

uniform vec3 u_Color;
uniform float u_Metalness = 0.0f;
uniform float u_Roughness = 0.0f;
uniform float u_Opacity = 1.0f;

layout (binding = 0) uniform samplerCube c_DiffuseIrradianceMap;
layout (binding = 1) uniform samplerCube c_PrefilteredMap;
layout (binding = 2) uniform sampler2D BRDFLUT;

out vec4 o_Color;

#include "cook_torrance.glsl"

void main()
{
    vec3 V = normalize(viewPosition_W - v_Position_W);
    vec3 normal = normalize(v_Normal_W);

    vec3 F0 = vec3(0.04f);
    F0 = mix(F0, u_Color, u_Metalness);

    vec3 Lo = vec3(0.0f);
    //////////Direct lighting////////////////
    for (uint i = 0; i < numPointLights; ++i)
    {
        vec3 L = normalize(pointLights[i].position.xyz - v_Position_W);
        vec3 H = normalize(V + L);
        float distance = length(pointLights[i].position.xyz - v_Position_W);
        float attenuation = 1.0f / (distance * distance);
        vec3 radiance = pointLights[i].color.rgb * attenuation;
        /////BRDF Components///////////////////////////////
        float NDF = DistributionGGX(normal, H, u_Roughness);
        float G = GeometrySmith(normal, V, L, u_Roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0f), F0);
        /////Cook-Torrance///////////
        vec3 numerator = NDF * G * F;
        float denominator = 4.0f * max(dot(normal, V), 0.0f) * max(dot(normal, L), 0.0f);
        vec3 specular = numerator / max(denominator, 0.001f);
        /////Diffuse-Specular Fraction/////
        vec3 kD = vec3(1.0f) - F; //(F = kS)
        kD *= 1.0f - u_Metalness;

        float NdotL = max(dot(normal, L), 0.0f);
        Lo += (kD * u_Color / PI + specular) * radiance * NdotL;
    }
    //////////Indirect Lighting//////////////////////////////////////////////////
    /////IBL Ambient/////////////////////////////////////////////////////////////
    vec3 kS = fresnelSchlickRoughness(max(dot(normal, V), 0.0f), F0, u_Roughness);
    vec3 kD = (1.0f - kS) * (1.0f - u_Metalness);
    vec3 irradiance = texture(c_DiffuseIrradianceMap, normal).rgb;
    vec3 diffuse = irradiance * u_Color;
    /////IBL Specular/////////////////////
    const float MAX_REFLECTION_LOD = 4.0f;
    vec3 R = reflect(-V, normal);
    vec3 prefilteredColor = textureLod(c_PrefilteredMap, R, u_Roughness * MAX_REFLECTION_LOD).rgb;

    vec3 F = fresnelSchlickRoughness(max(dot(normal, V), 0.0f), F0, u_Roughness);
    vec2 envBRDF = texture(BRDFLUT, vec2(max(dot(normal, V), 0.0f), u_Roughness)).rg;
    vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);

    vec3 ambient = kD * diffuse + specular; // All this * ao later

    o_Color = vec4(ambient + Lo, u_Opacity);
}