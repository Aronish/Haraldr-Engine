#version 460 core

layout(location = 0) in float x;
layout(location = 1) in float y;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec4 v_Color;

void main(){
    v_Color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
    gl_Position = projection * view * model * vec4(x, y, 1.0f, 1.0f);
}