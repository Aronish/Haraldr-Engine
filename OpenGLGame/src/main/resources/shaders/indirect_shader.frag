#version 460 core

in vec2 v_TextureCoordinate;

uniform sampler2D sampler;

out vec4 o_Color;

void main()
{
    o_Color = texture(sampler, v_TextureCoordinate);
}