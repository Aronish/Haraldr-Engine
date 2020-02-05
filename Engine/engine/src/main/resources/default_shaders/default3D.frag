#version 460 core
//TODO: Make uniform buffer?
struct Material
{
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    float specularExponent;
    float opacity;
};

in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform Material material;

uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform vec3 viewPosition;

uniform sampler2D diffuseTexture;
uniform sampler2D normalMap;

out vec4 o_Color;

const float ambientStrength = 0.1f;
const float diffuseStrength = 1.0f;
const float specularStrength = 1.0f;

void main()
{
    //vec3 normal = normalize(v_Normal);
    vec3 normal = normalize(texture(normalMap, v_TextureCoordinate).rgb * 2.0f - 1.0f);
    vec3 lightDirection = normalize(lightPosition - v_FragmentPosition);
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 reflectDirection = reflect(-lightDirection, normal);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //AMBIENT
    vec3 ambient = ambientStrength * lightColor * material.ambientColor;
    //DIFFUSE
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = diffuseStrength * lightColor * (diff * material.diffuseColor);
    //SPECULAR
    //float spec = pow(max(dot(viewDirection, reflectDirection), 0.0f), material.specularExponent); // Phong
    float spec = pow(max(dot(normal, halfWayDirection), 0.0f), material.specularExponent); // Blinn-Phong
    vec3 specular = specularStrength * lightColor * (spec * material.specularColor);

    vec3 result = (ambient + diffuse + specular);
    o_Color = texture(diffuseTexture, v_TextureCoordinate) * vec4(result, material.opacity);
}
//strength * lightColor * (lightComponent * componentColor)