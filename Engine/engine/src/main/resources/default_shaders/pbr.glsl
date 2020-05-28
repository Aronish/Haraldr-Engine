#shader vert
#version 460 core

#define MAX_DIRECTIONAL_LIGHTS 1
#define MAX_POINT_LIGHTS 15
#define MAX_SPOTLIGHTS 5

/////LIGHT TYPES//////////////////////////////
//Squeezing allowed!
struct PointLight
{
    vec4 position;
    vec4 color;
    float constant;
    float linear;
    float quadratic;
    //std140: Total 40 Pad 8 Size 48
};

struct DirectionalLight
{
    vec4 direction;
    vec4 color;
    //std140: Total 32 Pad 0 Size 32
};

struct Spotlight
{
    vec4 position;
    vec4 direction;
    vec4 color;
    float innerCutOff;
    float outerCutOff;
    //std140: Total 52 Pad 12 Size 64
};

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TextureCoordinate;
layout (location = 3) in vec3 a_Tangent;

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
};

/////OUTPUT/////////////
out tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
};

out vec3 v_Normal_Tangent;
out vec3 v_Normal;
out vec3 v_ViewPosition;
out vec3 v_WorldPosition;
out vec2 v_TextureCoordinate;

out mat3 v_TangentToWorld;

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

    v_Normal_Tangent    = TBN * normal;
    v_Normal            = normal;
    v_ViewPosition      = TBN * viewPosition;
    v_WorldPosition     = TBN * vec3((model * vec4(a_Position, 1.0f))); // Need to be translated as well.
    v_TextureCoordinate = a_TextureCoordinate;                          // Not important for lighting, don't put in tangent space.
    v_TangentToWorld = transpose(TBN);

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#define MAX_DIRECTIONAL_LIGHTS 1
#define MAX_POINT_LIGHTS 15
#define MAX_SPOTLIGHTS 5

/////LIGHT TYPES//////////////////////////////
//Squeezing allowed!
struct PointLight
{
    vec4 position;
    vec4 color;
    float constant;
    float linear;
    float quadratic;
//std140: Total 40 Pad 8 Size 48
};

struct DirectionalLight
{
    vec4 direction;
    vec4 color;
//std140: Total 32 Pad 0 Size 32
};

struct Spotlight
{
    vec4 position;
    vec4 direction;
    vec4 color;
    float innerCutOff;
    float outerCutOff;
//std140: Total 52 Pad 12 Size 64
};

in vec3 v_Normal_Tangent;
in vec3 v_Normal;
in vec3 v_ViewPosition;
in vec3 v_WorldPosition;
in vec2 v_TextureCoordinate;

in mat3 v_TangentToWorld;

in tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
};

layout(std140, binding = 1) uniform lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    float numPointLights;
    float numSpotlights;
    float numDirectionalLights;
};

uniform vec3 u_Albedo;
uniform float u_Metallic;
uniform float u_Roughness;

layout (binding = 0) uniform sampler2D albedoMap;
layout (binding = 1) uniform sampler2D normalMap;
layout (binding = 2) uniform sampler2D metallicMap;
layout (binding = 3) uniform sampler2D roughnessMap;
layout (binding = 4) uniform sampler2D displacementMap;
uniform float u_UseParallaxMapping;

layout (binding = 5) uniform samplerCube diffuseIrradianceMap;
layout (binding = 6) uniform samplerCube prefilteredMap;
layout (binding = 7) uniform sampler2D brdfLUT;

out vec4 o_Color;

const float PI = 3.14159265359f;

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0f - F0) * pow(1.0f - cosTheta, 5.0f);
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0f - roughness), F0) - F0) * pow(1.0f - cosTheta, 5.0f);
}

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a      = roughness * roughness;
    float a2     = a * a;
    float NdotH  = max(dot(N, H), 0.0f);
    float NdotH2 = NdotH * NdotH;
    float num   = a2;
    float denom = (NdotH2 * (a2 - 1.0f) + 1.0f);
    denom = PI * denom * denom;
    return num / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0f);
    float k = (r * r) / 8.0f;
    float num   = NdotV;
    float denom = NdotV * (1.0f - k) + k;
    return num / denom;
}
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0f);
    float NdotL = max(dot(N, L), 0.0f);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);
    return ggx1 * ggx2;
}

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
    float currentDisplacementMapValue = 1.0f - texture(displacementMap, currentTextureCoordinate).r;

    while (currentLayerDepth < currentDisplacementMapValue)
    {
        currentTextureCoordinate -= deltaTextureCoordinate;
        currentDisplacementMapValue = 1.0f - texture(displacementMap, currentTextureCoordinate).r;
        currentLayerDepth += layerDepth;
    }

    vec2 prevTextureCoordinate = currentTextureCoordinate + deltaTextureCoordinate;
    float displacementAfter = currentDisplacementMapValue - currentLayerDepth;
    float displacementBefore = 1.0f - texture(displacementMap, prevTextureCoordinate).r - currentLayerDepth + layerDepth;
    float weight = displacementAfter / (displacementAfter - displacementBefore);
    vec2 finalTextureCoordinate = prevTextureCoordinate * weight + currentTextureCoordinate * (1.0f - weight);

    return finalTextureCoordinate;
}

void main()
{
    float tilingFactor = 1.0f; //TODO: Make uniform
    vec3 V = normalize(v_ViewPosition - v_WorldPosition);

    vec2 textureCoordinate = u_UseParallaxMapping == 1.0f ? ParallaxMapping(v_TextureCoordinate * tilingFactor, V) : v_TextureCoordinate * tilingFactor;
    if(u_UseParallaxMapping == 1.0f && (textureCoordinate.x > tilingFactor || textureCoordinate.y > tilingFactor || textureCoordinate.x < 0.0f || textureCoordinate.y < 0.0f)) discard;

    vec3 albedo = texture(albedoMap, textureCoordinate).rgb * u_Albedo;
    vec3 tNormal = normalize(texture(normalMap, textureCoordinate).rgb * 2.0f - 1.0f);// * v_Normal_Tangent; // Read normals //TODO: WRONG
    float metallic = texture(metallicMap, textureCoordinate).r * u_Metallic;
    float roughness = texture(roughnessMap, textureCoordinate).r * u_Roughness;

    vec3 F0 = vec3(0.04f);
    F0 = mix(F0, albedo, metallic);

    vec3 Lo = vec3(0.0f);
    //////////Direct lighting////////////////
    for (uint i = 0; i < numPointLights; ++i)
    {
        vec3 L = normalize(pointLightPositions[i] - v_WorldPosition);
        vec3 H = normalize(V + L);
        float distance = length(pointLightPositions[i] - v_WorldPosition);
        float attenuation = 1.0f / (distance * distance);
        vec3 radiance = pointLights[i].color.rgb * attenuation;
        /////BRDF Components///////////////////////////////
        float NDF = DistributionGGX(tNormal, H, roughness);
        float G = GeometrySmith(tNormal, V, L, roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0f), F0);
        /////Cook-Torrance///////////
        vec3 numerator = NDF * G * F;
        float denominator = 4.0f * max(dot(tNormal, V), 0.0f) * max(dot(tNormal, L), 0.0f);
        vec3 specular = numerator / max(denominator, 0.001f);
        /////Diffuse-Specular Fraction/////
        vec3 kD = vec3(1.0f) - F; //(F = kS)
        kD *= 1.0f - metallic;

        float NdotL = max(dot(tNormal, L), 0.0f);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }
    //////////Indirect Lighting//////////////////////////////////////////////////
    /////IBL Ambient/////////////////////////////////////////////////////////////

    vec3 sampleVector = normalize(v_Normal);
    vec3 V_World = v_TangentToWorld * V;

    vec3 kS = fresnelSchlickRoughness(max(dot(sampleVector, V_World), 0.0f), F0, roughness);
    vec3 kD = (1.0f - kS) * (1.0f - metallic);
    vec3 irradiance = texture(diffuseIrradianceMap, sampleVector).rgb;
    vec3 diffuse = irradiance * albedo;
    /////IBL Specular/////////////////////
    const float MAX_REFLECTION_LOD = 4.0f;
    vec3 R = reflect(-V_World, sampleVector);
    vec3 prefilteredColor = textureLod(prefilteredMap, R, roughness * MAX_REFLECTION_LOD).rgb;

    vec3 F = fresnelSchlickRoughness(max(dot(sampleVector, V_World), 0.0f), F0, roughness);
    vec2 envBRDF = texture(brdfLUT, vec2(max(dot(sampleVector, V_World), 0.0f), roughness)).rg;
    vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);

    vec3 ambient = kD * diffuse + specular; // All this * ao later

    o_Color = vec4(ambient + Lo, 1.0f);
}