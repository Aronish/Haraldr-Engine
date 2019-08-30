#version 460 core

layout(location = 0) in vec2 a_Vertex;
layout(location = 1) in vec2 a_TexCoord;
layout(location = 2) in vec3 a_Color;

uniform mat4 model;
uniform mat4 projection;

out vec2 v_TexCoord;
out vec3 v_Color;

void main(){
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;
    gl_Position = projection * model * vec4(a_Vertex, 0.0f, 1.0f);
}