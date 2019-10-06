#version 460 core

layout(location = 0) in mat4 a_Matrix;
layout(location = 4) in vec2 a_Vertex;
layout(location = 5) in vec2 a_TextureCoordinate;

uniform mat4 view;
uniform mat4 projection;

out vec2 v_TextureCoordinate;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    gl_Position = projection * view * a_Matrix * vec4(a_Vertex, 0.0f, 1.0f);
}