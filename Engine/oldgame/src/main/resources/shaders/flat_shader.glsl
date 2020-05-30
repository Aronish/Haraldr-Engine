#shader vert
#version 460 core

layout(location = 0) in vec2 a_Vertices;

uniform mat4 matrix;
uniform mat4 projection;

void main()
{
    gl_Position = projection * matrix * vec4(a_Vertices, 0.0f, 1.0f);
}

#shader frag
#version 460 core

uniform vec4 color;

out vec4 o_Color;

void main()
{
    o_Color = color;
}