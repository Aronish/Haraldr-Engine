#shader vert
#version 460 core

#include "light_setup.glsl"

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TextureCoordinate;
layout (location = 3) in vec3 a_Tangent;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

/////OUTPUT/////////////
out tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
};

out vec3 v_Normal_W;
out vec3 v_Normal_T;
out vec3 v_ViewPosition_T;
out vec3 v_WorldPosition_W;
out vec3 v_WorldPosition_T;
out vec2 v_TextureCoordinate;

void main()
{
    mat3 normalMatrix   = mat3(model); // (Note: Tangent space vectors don't care about translation)
    vec3 normal         = normalize(normalMatrix * a_Normal);
    vec3 tangent        = normalize(normalMatrix * a_Tangent);
    tangent             = normalize(tangent - dot(tangent, normal) * normal);       // Fixes potential weird edges
    vec3 bitangent      = cross(normal, tangent);                                   // Is already in world space.
    mat3 TBN            = transpose(mat3(tangent, bitangent, normal));

    //Lights to tangent space
    for (uint i = 0; i < numPointLights; ++i)
    {
        pointLightPositions[i] = TBN * pointLights[i].position.xyz;
    }

    v_Normal_W              = normal;
    v_Normal_T              = TBN * normal;
    v_ViewPosition_T        = TBN * viewPosition_W;
    v_WorldPosition_W       = vec3((model * vec4(a_Position, 1.0f)));
    v_WorldPosition_T       = TBN * vec3((model * vec4(a_Position, 1.0f))); // Need to be translated as well.
    v_TextureCoordinate     = a_TextureCoordinate;                          // Not important for lighting, don't put in tangent space.

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#switches
#include "light_setup.glsl"

in vec3 v_Normal_W;
in vec3 v_Normal_T;
in vec3 v_ViewPosition_T;
in vec3 v_WorldPosition_W;
in vec3 v_WorldPosition_T;
in vec2 v_TextureCoordinate;

in tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
};

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

layout (binding = 0) uniform samplerCube c_DiffuseIrradianceMap;
layout (binding = 1) uniform samplerCube c_PrefilteredMap;
layout (binding = 2) uniform sampler2D BRDFLUT;

layout (binding = 3) uniform sampler2D map_Albedo;
layout (binding = 4) uniform sampler2D map_Normal;
layout (binding = 5) uniform sampler2D map_Metalness;
layout (binding = 6) uniform sampler2D map_Roughness;
#ifdef PARALLAX_MAP
layout (binding = 7) uniform sampler2D map_Displacement;
#endif
#ifdef AO_MAP
layout (binding = 8) uniform sampler2D map_Ambient_Occlusion;
#endif

uniform float u_Opacity = 1.0f;

out vec4 o_Color;

#include "cook_torrance.glsl"

#ifdef PARALLAX_MAP
vec2 ParallaxMapping(vec2 textureCoordinate, vec3 viewDirection)
{
    const float heightScale = 0.05f;
    const float minLayers = 8.0f;
    const float maxLayers = 32.0f;
    float numLayers = mix(maxLayers, minLayers, max(dot(vec3(0.0f, 0.0f, 1.0f), viewDirection), 0.0f));
    float layerDepth = 1.0f / numLayers;
    float currentLayerDepth = 0.0f;

    vec2 P = viewDirection.xy * heightScale;
    vec2 deltaTextureCoordinate = P / numLayers;

    vec2 currentTextureCoordinate = textureCoordinate;
    float currentDisplacementMapValue = 1.0f - texture(map_Displacement, currentTextureCoordinate).r;

    while (currentLayerDepth < currentDisplacementMapValue)
    {
        currentTextureCoordinate -= deltaTextureCoordinate;
        currentDisplacementMapValue = 1.0f - texture(map_Displacement, currentTextureCoordinate).r;
        currentLayerDepth += layerDepth;
    }

    vec2 prevTextureCoordinate = currentTextureCoordinate + deltaTextureCoordinate;
    float displacementAfter = currentDisplacementMapValue - currentLayerDepth;
    float displacementBefore = 1.0f - texture(map_Displacement, prevTextureCoordinate).r - currentLayerDepth + layerDepth;
    float weight = displacementAfter / (displacementAfter - displacementBefore);
    vec2 finalTextureCoordinate = prevTextureCoordinate * weight + currentTextureCoordinate * (1.0f - weight);

    return finalTextureCoordinate;
}
#endif

void main()
{
    float tilingFactor = 1.0f; //TODO: Make uniform
    vec3 V = normalize(v_ViewPosition_T - v_WorldPosition_T);

#ifdef PARALLAX_MAP
    vec2 textureCoordinate = ParallaxMapping(v_TextureCoordinate * tilingFactor, V);
    if(textureCoordinate.x > tilingFactor || textureCoordinate.y > tilingFactor || textureCoordinate.x < 0.0f || textureCoordinate.y < 0.0f) discard;
#else
    vec2 textureCoordinate = v_TextureCoordinate * tilingFactor;
#endif

    vec3 albedo = texture(map_Albedo, textureCoordinate).rgb;
    vec3 normal_T = normalize(texture(map_Normal, textureCoordinate).rgb * 2.0f - 1.0f);
    float metallic = texture(map_Metalness, textureCoordinate).r;
    float roughness = texture(map_Roughness, textureCoordinate).r;
#ifdef AO_MAP
    float ambientOcclusion = texture(map_Ambient_Occlusion, textureCoordinate).r;
#endif

    vec3 F0 = vec3(0.04f);
    F0 = mix(F0, albedo, metallic);

    vec3 Lo = vec3(0.0f);
    //////////Direct lighting////////////////
    for (uint i = 0; i < numPointLights; ++i)
    {
        vec3 L = normalize(pointLightPositions[i] - v_WorldPosition_T);
        vec3 H = normalize(V + L);
        float distance = length(pointLightPositions[i] - v_WorldPosition_T);
        float attenuation = 1.0f / (distance * distance);
        vec3 radiance = pointLights[i].color.rgb * attenuation;
        /////BRDF Components///////////////////////////////
        float NDF = DistributionGGX(normal_T, H, roughness);
        float G = GeometrySmith(normal_T, V, L, roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0f), F0);
        /////Cook-Torrance///////////
        vec3 numerator = NDF * G * F;
        float denominator = 4.0f * max(dot(normal_T, V), 0.0f) * max(dot(normal_T, L), 0.0f);
        vec3 specular = numerator / max(denominator, 0.001f);
        /////Diffuse-Specular Fraction/////
        vec3 kD = vec3(1.0f) - F; //(F = kS)
        kD *= 1.0f - metallic;

        float NdotL = max(dot(normal_T, L), 0.0f);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }
    //////////Indirect Lighting//////////////////////////////////////////////////
    /////IBL Ambient/////////////////////////////////////////////////////////////

    vec3 sampleVector = normalize(v_Normal_W);
    vec3 V_World = normalize(viewPosition_W - v_WorldPosition_W);

    vec3 kS = fresnelSchlickRoughness(max(dot(sampleVector, V_World), 0.0f), F0, roughness);
    vec3 kD = (1.0f - kS) * (1.0f - metallic);
    vec3 irradiance = texture(c_DiffuseIrradianceMap, sampleVector).rgb;
    vec3 diffuse = irradiance * albedo;
    /////IBL Specular/////////////////////
    const float MAX_REFLECTION_LOD = 4.0f;
    vec3 R = reflect(-V_World, sampleVector);
    vec3 prefilteredColor = textureLod(c_PrefilteredMap, R, roughness * MAX_REFLECTION_LOD).rgb;

    vec3 F = fresnelSchlickRoughness(max(dot(sampleVector, V_World), 0.0f), F0, roughness);
    vec2 envBRDF = texture(BRDFLUT, vec2(max(dot(sampleVector, V_World), 0.0f), roughness)).rg;
    vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);

#ifdef AO_MAP
    vec3 ambient = (kD * diffuse + specular) * ambientOcclusion;
#else
    vec3 ambient = kD * diffuse + specular;
#endif
    o_Color = vec4(ambient + Lo, u_Opacity);
}