#version 460 core

layout(location = 0) in vec2 a_Vertices;
layout(location = 1) in vec2 a_Texcoords;

uniform mat4 matrix;
uniform mat4 view;
uniform mat4 projection;

out vec2 v_Texcoords;

void main(){
    v_Texcoords = a_Texcoords;
    gl_Position = projection * view * matrix * vec4(a_Vertices, 1.0f, 1.0f);
}