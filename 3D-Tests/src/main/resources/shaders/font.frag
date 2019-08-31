#version 460 core

in vec2 v_TexCoord;
in vec3 v_Color;

uniform sampler2D sampler;

out vec4 o_color;

void main(){
    o_color = vec4(v_Color, texture(sampler, v_TexCoord).x);
}