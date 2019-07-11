#version 460 core

in vec2 TC;

uniform sampler2D sampler;

out vec4 color;

void main(){
    color = texture(sampler, TC);
}