#version 330 core

layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 texcoords;

uniform mat4 matrix;

out vec2 TC;

void main(){
    TC = texcoords;
    gl_Position = matrix * vec4(pos, 1.0f, 1.0f);
}