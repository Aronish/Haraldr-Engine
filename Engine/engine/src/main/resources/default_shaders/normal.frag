#version 460 core

#define MAX_LIGHTS 2
const float AMBIENT_STRENGTH = 0.2f;
const float DIFFUSE_STRENGTH = 1.0f;
const float SPECULAR_STRENGTH = 0.8f;
const float SPECULAR_EXPONENT = 200.0f;
const float OPACITY = 1.0f;

struct PointLight
{
    vec3 position;
    vec3 color;
    float constant;
    float linear;
    float quadratic;
};

in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform PointLight pointLights[MAX_LIGHTS];
uniform float numPointLights;
uniform vec3 viewPosition;

layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D normalMap;

out vec4 o_Color;

vec3 pointLight(PointLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Attenuation
    float distance = length(light.position - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    //AMBIENT
    vec3 ambient = AMBIENT_STRENGTH * light.color;
    //DIFFUSE
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff;
    //SPECULAR
    float spec = pow(max(dot(normal, halfWayDirection), 0.0f), SPECULAR_EXPONENT); // Blinn-Phong
    vec3 specular = SPECULAR_STRENGTH * light.color * spec;

    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

void main()
{
    vec3 normal = normalize(texture(normalMap, v_TextureCoordinate).rgb * 2.0f - 1.0f); // Read normals
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 result;

    for (int i = 0; i < numPointLights; ++i)
    {
        result += pointLight(pointLights[i], normal, viewDirection);
    }

    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, OPACITY);
}
//strength * lightColor * (lightComponent * componentColor)