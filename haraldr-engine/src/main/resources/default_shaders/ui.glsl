#shader vert
#version 460 core

layout (location = 0) in vec2 a_Position;

uniform mat4 projection;

void main()
{
    gl_Position = projection * vec4(a_Position, 0.0f, 1.0f);
}

#shader frag
#version 460 core

uniform vec4 u_Color;

out vec4 o_Color;

void main()
{
    o_Color = u_Color;
}