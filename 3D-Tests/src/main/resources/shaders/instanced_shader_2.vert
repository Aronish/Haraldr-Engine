#version 460 core

layout(location = 0) in vec2 a_Vertices;
layout(location = 1) in vec2 a_Texcoords;
layout(location = 2) in mat4 a_Matrix;

out vec2 v_Texcoords;

void main(){
    v_Texcoords = a_Texcoords;
    gl_Position = a_Matrix * vec4(a_Vertices, 1.0f, 1.0f);
}