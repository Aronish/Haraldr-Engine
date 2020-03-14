#version 460 core

#define MAX_POINT_LIGHTS 2
const float AMBIENT_STRENGTH = 0.2f;
const float DIFFUSE_STRENGTH = 1.0f;
const float SPECULAR_STRENGTH = 0.8f;
const float SPECULAR_EXPONENT = 200.0f;
const float OPACITY = 1.0f;
//TODO: Add intensity variables

struct PointLight
{
    vec3 position;      //16    0
    vec3 color;         //16    16
    float constant;     //4     32
    float linear;       //4     36
    float quadratic;    //4     40
    //Padding: 8
};

in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform vec3 viewPosition;

uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform float numPointLights;

/*
layout(std140, binding = 1) uniform lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS]; //Size: 48 * MAX_POINT_LIGHTS
    float numPointLights;
};
*/

//(binding, NOT location)
layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D normalMap;

out vec4 o_Color;

vec3 pointLight(PointLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff;
    //Specular
    float spec = pow(max(dot(normal, halfWayDirection), 0.0f), SPECULAR_EXPONENT);
    vec3 specular = SPECULAR_STRENGTH * light.color * spec;
    //Attenuation
    float distance = length(light.position - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
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

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += pointLight(pointLights[i], normal, viewDirection);
    }

    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, OPACITY);
}
//strength * lightColor * lightComponent * componentColor