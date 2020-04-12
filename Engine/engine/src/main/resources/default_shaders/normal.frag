#version 460 core

#define MAX_POINT_LIGHTS 20

const float AMBIENT_STRENGTH = 0.2f;
const float DIFFUSE_STRENGTH = 1.0f;
const float SPECULAR_STRENGTH = 1.0f;
const float SPECULAR_EXPONENT = 400.0f;
const float OPACITY = 1.0f;
//TODO: Add intensity variables

/////LIGHT TYPES//////////////////////////////
//Squeezing allowed!
struct PointLight
{                       //std140        std430
    vec3 position;      //16    0   1   12
    vec3 color;         //16    16  0   12
    float constant;     //4     28      4
    float linear;       //4     32      4
    float quadratic;    //4     36      4
//std140: Total 40 Pad 8 Size 48
//std430: Total: 36 Alignment: vec3 Pad: 0 Size: 36
};

struct DirectionalLight
{                   //std140            std430
    vec3 direction; //16    0   1       12
    vec3 color;     //16    16          12
//std140: Total 32 Pad 0 Size 32
//std430: Total 24 Alignment: vec3 Pad: 0 Size: 24
};

struct Spotlight
{                       //std140        std430
    vec3 position;      //16    0   1   12
    vec3 direction;     //16    16  1   12
    vec3 color;         //16    32  0   12
    float innerCutOff;  //4     44      4
    float outerCutOff;  //4     48      4
//std140: Total 52 Pad 12 Size 64
//std430: Total 44 Alignment: vec3 Pad: 4 Size: 48
};

/////INPUT AND UNIFORMS//////////////
/*
in TEST
{
    vec3 positions[MAX_POINT_LIGHTS];
} pointLightPositions;
*/
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;
in vec3 v_ViewPosition;

layout(std140, binding = 1) buffer lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS];
} pointLights;

layout (std140, binding = 2) buffer interfaceBlock
{
    vec3 positions[MAX_POINT_LIGHTS];
} pointLightPositions;

/////TEXTURES////////////////////////////////////////
layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D normalMap;
/////OUTPUT//////
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
    vec3 ambient = AMBIENT_STRENGTH * light.color;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, SPECULAR_EXPONENT) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = SPECULAR_STRENGTH * light.color * spec;

    return (ambient + diffuse + specular);
}

vec3 calculatePointLight(PointLight light, vec3 lightPosition, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(lightPosition - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, SPECULAR_EXPONENT) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = SPECULAR_STRENGTH * light.color * spec;
    //Attenuation
    float distance = length(lightPosition - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

vec3 calculateSpotlight(Spotlight light, vec3 normal, vec3 viewDirection)
{
    //vec3 lightDirection = normalize(-light.direction);
    vec3 lightDirection = normalize(light.position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    float theta = dot(lightDirection, normalize(-light.direction));
    float epsilon = light.innerCutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0f, 1.0f);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color * diff;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, SPECULAR_EXPONENT) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = SPECULAR_STRENGTH * light.color * spec;
    //Cutoff
    //TODO: Maybe add ambient back with attenuation fixed.
    diffuse *= intensity;
    specular *= intensity;
    return (diffuse + specular);
}

void main()
{
    vec3 normal = normalize(texture(normalMap, v_TextureCoordinate).rgb * 2.0f - 1.0f); // Read normals
    vec3 viewDirection = normalize(v_ViewPosition - v_FragmentPosition);
    vec3 result = vec3(0.0f);

    //Calculate light contribution
    for (uint i = 0; i < pointLights.pointLights.length(); ++i)
    {
        result += calculatePointLight(pointLights.pointLights[i], pointLightPositions.positions[i], normal, viewDirection);
    }

    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, OPACITY);
}
//strength * lightColor * lightComponent