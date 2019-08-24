#version 460 core

in vec2 v_TexCoord;

uniform sampler2D sampler;

out vec4 o_color;

void main(){
    o_color = texture(sampler, v_TexCoord);
}