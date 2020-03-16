#version 460 core

#define MAX_DIRECTIONAL_LIGHTS 1
#define MAX_POINT_LIGHTS 3
#define MAX_SPOTLIGHTS 3

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

//Squeezing allowed!
struct DirectionalLight
{
    vec3 direction; //16    0   1
    vec3 color;     //16    16
//Total 32 Pad 0 Size 32
};

struct PointLight
{
    vec3 position;      //16    0   1
    vec3 color;         //16    16  0
    float constant;     //4     28
    float linear;       //4     32
    float quadratic;    //4     36
//Total 40 Pad 8 Size 48
};

struct Spotlight
{
    vec3 position;      //16    0   1
    vec3 direction;     //16    16  1
    vec3 color;         //16    32  0
    float innerCutOff;  //4     44
    float outerCutOff;  //4     48
//Total 52 Pad 12 Size 64
};

in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform Material material;
uniform vec3 viewPosition;

layout(std140, binding = 1) uniform lightSetup
{
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    float numDirectionalLights;
    float numPointLights;
    float numSpotlights;
};

layout(location = 0) uniform sampler2D diffuseTexture;

out vec4 o_Color;

float when_gt(float x, float y)
{
    return max(sign(x - y), 0.0);
}

vec3 calculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(-light.direction);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color * material.ambientColor;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff * material.diffuseColor;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, material.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = SPECULAR_STRENGTH * light.color * spec * material.specularColor;

    return (ambient + diffuse + specular);
}

vec3 calculatePointLight(PointLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color * material.ambientColor;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff * material.diffuseColor;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, material.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = SPECULAR_STRENGTH * light.color * spec * material.specularColor;
    //Attenuation
    float distance = length(light.position - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

vec3 calculateSpotlight(Spotlight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    float theta = dot(lightDirection, normalize(-light.direction));
    float epsilon = light.innerCutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0f, 1.0f);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color * material.ambientColor;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff * material.diffuseColor;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, material.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = SPECULAR_STRENGTH * light.color * spec * material.specularColor;
    //Cutoff
    diffuse *= intensity;
    specular *= intensity;
    return (ambient + diffuse + specular);
}

void main()
{
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 normal = normalize(v_Normal);
    vec3 result;

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight(directionalLights[i], normal, viewDirection);
    }

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight(pointLights[i], normal, viewDirection);
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight(spotlights[i], normal, viewDirection);
    }

    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, material.opacity);
}
//strength * lightColor * lightComponent * materialColor