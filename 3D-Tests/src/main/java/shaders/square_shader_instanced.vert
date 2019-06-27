#version 460 core

layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 texcoords;

uniform mat4 matrices[256];

out vec2 TC;

void main(){
    TC = texcoords;
    gl_Position = matrices[gl_InstanceID] * vec4(pos, 1.0f, 1.0f);
}