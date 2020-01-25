#version 460 core

in vec2 v_Test;
in vec4 v_Color;

out vec4 o_Color;

void main()
{
    //o_Color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
    //o_Color = vec4(v_Test, 0.0f, 1.0f);
    o_Color = v_Color;
}