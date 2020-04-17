#version 460 core

in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform vec3 viewPosition;
layout (binding = 0) uniform samplerCube environmentMap;
layout (binding = 1) uniform sampler2D diffuseTexture;
layout (binding = 2) uniform sampler2D reflectionMap;

out vec4 o_Color;

void main()
{
    vec3 I = normalize(v_FragmentPosition - viewPosition);
    vec3 R = reflect(I, normalize(v_Normal));

    vec3 color = texture(environmentMap, R).rgb * texture(reflectionMap, v_TextureCoordinate).rgb + texture(diffuseTexture, v_TextureCoordinate).rgb;
    o_Color = vec4(color, 1.f);
}