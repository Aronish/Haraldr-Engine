#version 330 core

layout(location = 0) in vec2 pos;

uniform mat4 matrix;

void main(){
    gl_Position = matrix * vec4(pos, 1.0f, 1.0f);
}