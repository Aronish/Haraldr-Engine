#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;

uniform mat4 model = mat4(1.0f);
uniform mat4 mappingView;
uniform mat4 mappingProjection;

out vec3 v_LocalPosition;

void main()
{
    v_LocalPosition = a_Position;
    gl_Position = mappingProjection * mappingView * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

in vec3 v_LocalPosition;

layout (binding = 0) uniform samplerCube environmentMap;

uniform float u_Roughness;

out vec4 o_Color;

const float PI = 3.14159265359f;

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a         = roughness * roughness;
    float a2        = a * a;
    float NdotH     = max(dot(N, H), 0.0f);
    float NdotH2    = NdotH * NdotH;
    float num       = a2;
    float denom     = (NdotH2 * (a2 - 1.0f) + 1.0f);
    denom = PI * denom * denom;
    return num / denom;
}

float RadicalInverse_VdC(uint bits)
{
    bits = (bits << 16u) | (bits >> 16u);
    bits = ((bits & 0x55555555u) << 1u) | ((bits & 0xAAAAAAAAu) >> 1u);
    bits = ((bits & 0x33333333u) << 2u) | ((bits & 0xCCCCCCCCu) >> 2u);
    bits = ((bits & 0x0F0F0F0Fu) << 4u) | ((bits & 0xF0F0F0F0u) >> 4u);
    bits = ((bits & 0x00FF00FFu) << 8u) | ((bits & 0xFF00FF00u) >> 8u);
    return float(bits) * 2.3283064365386963e-10; // / 0x100000000
}

vec2 Hammersley(uint i, uint N)
{
    return vec2(float(i) / float(N), RadicalInverse_VdC(i));
}

vec3 ImportanceSampleGGX(vec2 Xi, vec3 N, float roughness)
{
    float a = roughness * roughness;

    float phi = 2.0f * PI * Xi.x;
    float cosTheta = sqrt((1.0f - Xi.y) / (1.0f + (a * a - 1.0f) * Xi.y));
    float sinTheta = sqrt(1.0f - cosTheta*cosTheta);

    // from spherical coordinates to cartesian coordinates
    vec3 H;
    H.x = cos(phi) * sinTheta;
    H.y = sin(phi) * sinTheta;
    H.z = cosTheta;

    // from tangent-space vector to world-space sample vector
    vec3 up        = abs(N.z) < 0.999f ? vec3(0.0f, 0.0f, 1.0f) : vec3(1.0f, 0.0f, 0.0f);
    vec3 tangent   = normalize(cross(up, N));
    vec3 bitangent = cross(N, tangent);

    vec3 sampleVec = tangent * H.x + bitangent * H.y + N * H.z;
    return normalize(sampleVec);
}

void main()
{
    vec3 N = normalize(v_LocalPosition);

    const uint SAMPLE_COUNT = 1024u;
    float totalWeight = 0.0f;
    vec3 prefilteredColor = vec3(0.0f);
    for(uint i = 0u; i < SAMPLE_COUNT; ++i)
    {
        vec2 Xi = Hammersley(i, SAMPLE_COUNT);
        vec3 H  = ImportanceSampleGGX(Xi, N, u_Roughness);
        vec3 L  = normalize(2.0f * dot(N, H) * H - N);

        float NdotL = max(dot(N, L), 0.0f);
        if(NdotL > 0.0f)
        {
            // sample from the environment's mip level based on roughness/pdf
            float D   = DistributionGGX(N, H, u_Roughness);
            float NdotH = max(dot(N, H), 0.0f);
            float HdotV = max(dot(H, N), 0.0f);
            float pdf = D * NdotH / (4.0f * HdotV) + 0.0001f;

            float resolution = 2048.0f; // resolution of source cubemap (per face) //TODO: HMM
            float saTexel  = 4.0f * PI / (6.0f * resolution * resolution);
            float saSample = 1.0f / (float(SAMPLE_COUNT) * pdf + 0.0001f);

            float mipLevel = u_Roughness == 0.0f ? 0.0f : 0.5f * log2(saSample / saTexel);

            prefilteredColor += textureLod(environmentMap, L, mipLevel).rgb * NdotL;
            totalWeight      += NdotL;
        }
    }
    prefilteredColor = prefilteredColor / totalWeight;

    o_Color = vec4(prefilteredColor, 1.0f);
}