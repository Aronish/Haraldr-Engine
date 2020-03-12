#version 460 core

#define MAX_LIGHTS 2
const float AMBIENT_STRENGTH = 0.1f;
const float DIFFUSE_STRENGTH = 1.0f;
const float SPECULAR_STRENGTH = 0.8f;
//TODO: Add intensity variables

struct Material
{
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    float specularExponent;
    float opacity;
};

struct PointLight
{
    vec3 position;
    vec3 color;
    float constant;
    float linear;
    float quadratic;
};

in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform Material material = { { 0.3f, 0.3, 0.3f }, { 0.8f, 0.2f, 0.3f }, { 0.8f, 0.2f, 0.3f }, 32.0f, 1.0f };
uniform vec3 viewPosition;

uniform PointLight pointLights[MAX_LIGHTS];
uniform float numPointLights;

layout(location = 0) uniform sampler2D diffuseTexture;

out vec4 o_Color;

vec3 pointLight(PointLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    float distance = length(light.position - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    //AMBIENT
    vec3 ambient = AMBIENT_STRENGTH * light.color * material.ambientColor;
    //DIFFUSE
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff * material.diffuseColor;
    //SPECULAR
    float spec = pow(max(dot(normal, halfWayDirection), 0.0f), material.specularExponent); // Blinn-Phong
    vec3 specular = SPECULAR_STRENGTH * light.color * spec * material.specularColor;

    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

void main()
{
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 normal = normalize(v_Normal);
    vec3 result;

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += pointLight(pointLights[i], normal, viewDirection);
    }

    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, material.opacity);
}
//strength * lightColor * lightComponent * componentColor