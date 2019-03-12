#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texcoords;

uniform mat4 MP;

out vec2 TC;

void main(){
    TC = texcoords;
    gl_Position = MP * vec4(pos, 1.0f);
}