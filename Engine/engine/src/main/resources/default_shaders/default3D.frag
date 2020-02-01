#version 460 core

in vec3 v_Normal;
in vec3 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform vec3 diffuseColor; //Object color
uniform float specularExponent;
uniform float opacity;

uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform vec3 viewPosition;

out vec4 o_Color;

void main()
{
    float ambientStrength = 0.1f;
    vec3 ambient = ambientStrength * lightColor;

    vec3 normal = normalize(v_Normal);
    vec3 lightDirection = normalize(lightPosition - v_FragmentPosition);

    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = diff * lightColor;

    float specularStrength = 0.5f;
    vec3 viewDirection = normalize(viewPosition - v_FragmentPosition);
    vec3 reflectDirection = reflect(-lightDirection, normal);

    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0f), specularExponent);
    vec3 specular = specularStrength * spec * lightColor;

    vec3 result = (ambient + diffuse + specular) * diffuseColor;
    o_Color = vec4(result, opacity);
}