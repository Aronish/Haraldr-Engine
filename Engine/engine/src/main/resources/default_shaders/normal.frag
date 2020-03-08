#version 460 core

#define MAX_LIGHTS 4
const float AMBIENT_STRENGTH = 0.1f;
const float DIFFUSE_STRENGTH = 1.0f;
const float SPECULAR_STRENGTH = 0.8f;
const float SPECULAR_EXPONENT = 200.0f;
const float OPACITY = 1.0f;

in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;
//TODO: Multiple lights
uniform vec3 lightColor[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 viewPosition;

layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D normalMap;

out vec4 o_Color;

vec3 simpleLight(vec3 lightPosition, vec3 lightColor, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(lightPosition - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //AMBIENT
    vec3 ambient = AMBIENT_STRENGTH * lightColor;
    //DIFFUSE
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * lightColor * diff;
    //SPECULAR
    float spec = pow(max(dot(normal, halfWayDirection), 0.0f), SPECULAR_EXPONENT); // Blinn-Phong
    vec3 specular = SPECULAR_STRENGTH * lightColor * spec;

    return (ambient + diffuse + specular);
}

void main()
{
    vec3 normal = normalize(texture(normalMap, v_TextureCoordinate).rgb * 2.0f - 1.0f); // Read normals
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);

    vec3 result;

    for (int i = 0; i < MAX_LIGHTS; ++i)
    {
        result += simpleLight(lightPosition[i], lightColor[i], normal, viewDirection);
    }

    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, OPACITY);
}
//strength * lightColor * (lightComponent * componentColor)