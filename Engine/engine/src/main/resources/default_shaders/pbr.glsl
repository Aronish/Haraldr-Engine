#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TextureCoordinate;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

out vec3 v_Normal;
out vec3 v_WorldPosition;
out vec2 v_TextureCoordinate;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    mat3 normalMatrix = mat3(model);
    v_Normal = normalMatrix * a_Normal;
    v_WorldPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

in vec3 v_Normal;
in vec3 v_WorldPosition;
in vec2 v_TextureCoordinate;

uniform vec3 viewPosition;

uniform vec3 u_Albedo;
uniform float u_Metallic;
uniform float u_Roughness;
//uniform float ao;

uniform vec3 lightPosition;
uniform vec3 lightColor;

out vec4 o_Color;

const float PI = 3.14159265359f;

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0f - F0) * pow(1.0f - cosTheta, 5.0f);
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

layout (binding = 0) uniform sampler2D albedoMap;
layout (binding = 1) uniform sampler2D normalMap;
layout (binding = 2) uniform sampler2D metallicMap;
layout (binding = 3) uniform sampler2D roughnessMap;

//TODO: TEMP
vec3 getNormalFromMap()
{
    vec3 tangentNormal = texture(normalMap, v_TextureCoordinate).xyz * 2.0f - 1.0f;

    vec3 Q1  = dFdx(v_WorldPosition);
    vec3 Q2  = dFdy(v_WorldPosition);
    vec2 st1 = dFdx(v_TextureCoordinate);
    vec2 st2 = dFdy(v_TextureCoordinate);

    vec3 N   = normalize(v_Normal);
    vec3 T  = normalize(Q1 * st2.t - Q2 * st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);

    return normalize(TBN * tangentNormal);
}

void main()
{
    vec3 albedo = texture(albedoMap, v_TextureCoordinate).rgb;
    vec3 N = getNormalFromMap();
    float metallic = texture(metallicMap, v_TextureCoordinate).r;
    float roughness = texture(roughnessMap, v_TextureCoordinate).r;
    //vec3 N = normalize(v_Normal);
    vec3 V = normalize(viewPosition - v_WorldPosition);

    vec3 F0 = vec3(0.04f);
    F0 = mix(F0, albedo, metallic);

    vec3 Lo = vec3(0.0f);
    /////Direct lighting TODO: LOOP PER LIGHT///////////
        vec3 L = normalize(lightPosition - v_WorldPosition);
        vec3 H = normalize(V + L);
        float distance = length(lightPosition - v_WorldPosition);
        float attenuation = 1.0f / (distance * distance);
        vec3 radiance = lightColor * attenuation;
        /////BRDF Components////////////
        float NDF = DistributionGGX(N, H, roughness);
        float G = GeometrySmith(N, V, L, roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0f), F0);
        /////Cook-Torrance/////////////////
        vec3 numerator = NDF * G * F;
        float denominator = 4.0f * max(dot(N, V), 0.0f) * max(dot(N, L), 0.0f);
        vec3 specular = numerator / max(denominator, 0.001f);
        /////Diffuse-Specular Fraction/////
        vec3 kS = F;
        vec3 kD = vec3(1.0f) - kS;
        kD *= 1.0f - metallic;

        float NdotL = max(dot(N, L), 0.0f);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;

    vec3 ambient = vec3(0.03) * albedo/* * ao*/;
    vec3 color = ambient + Lo;

    o_Color = vec4(color, 1.0f);
}