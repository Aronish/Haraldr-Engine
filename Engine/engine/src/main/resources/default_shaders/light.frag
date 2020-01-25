#version 460 core

uniform vec3 lightColor;

out vec4 o_Color;

void main()
{
    o_Color = vec4(lightColor, 1.0f);
}