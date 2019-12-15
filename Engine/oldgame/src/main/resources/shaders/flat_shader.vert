#version 460 core

layout(location = 0) in vec2 a_Vertices;

uniform mat4 matrix;
uniform mat4 projection;

void main()
{
    gl_Position = projection * matrix * vec4(a_Vertices, 0.0f, 1.0f);
}