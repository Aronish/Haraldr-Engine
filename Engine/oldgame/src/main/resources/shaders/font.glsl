#shader vert
#version 460 core

layout(location = 0) in vec2 a_Vertex;
layout(location = 1) in vec2 a_TexCoord;
layout(location = 2) in vec3 a_Color;
layout(location = 3) in mat4 a_Matrix;

uniform mat4 projection;

out vec2 v_TexCoord;
out vec3 v_Color;

void main(){
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;
    gl_Position = projection * a_Matrix * vec4(a_Vertex, 0.0f, 1.0f);
}

#shader frag
#version 460 core

in vec2 v_TexCoord;
in vec3 v_Color;

uniform sampler2D sampler;

out vec4 o_color;

void main(){
    /*float alpha;
    float sampledValue = texture(sampler, v_TexCoord).x;
    if (sampledValue == 1.0f) {
        alpha = 1.0f;
    }else{
        alpha = 0.0f;
    }*/
    o_color = vec4(v_Color, texture(sampler, v_TexCoord).x);
}