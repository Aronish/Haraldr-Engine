#shader vert
#version 460 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec2 a_TextureCoordinate;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

out vec3 v_Normal;
out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;

void main()
{
    mat3 normalMatrix = mat3(model);
    v_Normal = normalMatrix * a_Normal;
    v_TextureCoordinate = a_TextureCoordinate;
    v_FragmentPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#define MAX_POINT_LIGHTS 15
#define MAX_SPOTLIGHTS 5
#define MAX_DIRECTIONAL_LIGHTS 1

const float AMBIENT_STRENGTH = 1.0f;

struct MaterialProperties
{
    vec3 diffuseColor;
    float diffuseStrength;
    float specularStrength;
    float specularExponent;
    float opacity;
};

/////LIGHT TYPES//////////////////////////////
//Squeezing allowed!
struct PointLight
{                       //std140        std430
    vec4 position;      //16    0   1   12
    vec4 color;         //16    16  0   12
    float constant;     //4     28      4
    float linear;       //4     32      4
    float quadratic;    //4     36      4
//std140: Total 40 Pad 8 Size 48
};

struct DirectionalLight
{                   //std140            std430
    vec4 direction; //16    0   1       12
    vec4 color;     //16    16          12
//std140: Total 32 Pad 0 Size 32
};

struct Spotlight
{                       //std140        std430
    vec4 position;      //16    0   1   12
    vec4 direction;     //16    16  1   12
    vec4 color;         //16    32  0   12
    float innerCutOff;  //4     44      4
    float outerCutOff;  //4     48      4
//std140: Total 52 Pad 12 Size 64
};

/////INPUT AND UNIFORMS/////
in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

layout(std140, binding = 1) uniform lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    float numPointLights;
    float numSpotlights;
    float numDirectionalLights;
};

uniform vec3 viewPosition;
/////MATERIAL/////////////////////////////////////////
layout(location = 0) uniform sampler2D diffuseTexture;
uniform MaterialProperties materialProperties;
/////OUTPUT//////
out vec4 o_Color;

float when_gt(float x, float y)
{
    return max(sign(x - y), 0.0);
}

vec3 calculatePointLight(PointLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position.xyz - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color.rgb * materialProperties.diffuseColor * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = materialProperties.diffuseStrength * light.color.rgb * diff * materialProperties.diffuseColor * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, materialProperties.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = materialProperties.specularStrength * light.color.rgb * spec;
    //Attenuation
    float distance = length(light.position.xyz - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

vec3 calculateSpotlight(Spotlight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position.xyz - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    float theta = dot(lightDirection, normalize(-light.direction.xyz));
    float epsilon = light.innerCutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0f, 1.0f);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color.rgb * materialProperties.diffuseColor * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = materialProperties.diffuseStrength * light.color.rgb * diff * materialProperties.diffuseColor * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, materialProperties.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = materialProperties.specularStrength * light.color.rgb * spec;
    //Cutoff
    diffuse *= intensity;
    specular *= intensity;
    return (diffuse + specular);
}

vec3 calculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(-light.direction.xyz);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = AMBIENT_STRENGTH * light.color.rgb * materialProperties.diffuseColor * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = materialProperties.diffuseStrength * light.color.rgb * diff * materialProperties.diffuseColor * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, materialProperties.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = materialProperties.specularStrength * light.color.rgb * spec;

    return (ambient + diffuse + specular);
}

void main()
{
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 normal = normalize(v_Normal);
    vec3 result;

    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight(pointLights[i], normal, viewDirection);
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight(spotlights[i], normal, viewDirection);
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight(directionalLights[i], normal, viewDirection);
    }

    o_Color = vec4(result, materialProperties.opacity);
}
//strength * lightColor * lightComponent * materialColor