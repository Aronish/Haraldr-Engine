#version 460 core

layout(location = 0) in vec2 a_Vertex;
layout(location = 1) in vec2 a_TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 v_TexCoord;

void main(){
    v_TexCoord = a_TexCoord;
    gl_Position = projection * view * model * vec4(a_Vertex, 0.0f, 1.0f);
}