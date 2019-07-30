#version 460 core

layout(location = 0) in vec2 a_Vertices;
layout(location = 1) in vec2 a_Texcoords;

uniform mat4 matrices[256];

out vec2 v_Texcoords;

void main(){
   v_Texcoords = a_Texcoords;
   gl_Position = matrices[gl_InstanceID] * vec4(a_Vertices, 1.0f, 1.0f);
}