#version 460 core

in vec2 v_TextureCoordinates;

uniform sampler2D sampler;
uniform vec4 color;

out vec4 o_Color;

void main()
{
    o_Color = texture(sampler, v_TextureCoordinates) * color;
}