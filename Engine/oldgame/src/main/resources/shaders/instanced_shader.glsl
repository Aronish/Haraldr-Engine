#shader vert
#version 460 core

layout(location = 0) in vec2 a_Vertices;
layout(location = 1) in vec2 a_Texcoords;
layout(location = 2) in mat4 a_Matrix;

uniform mat4 view;
uniform mat4 projection;

out vec2 v_Texcoords;

void main()
{
    v_Texcoords = a_Texcoords;
    gl_Position = projection * view * a_Matrix * vec4(a_Vertices, 1.0f, 1.0f);
}

#shader frag
#version 460 core

in vec2 v_Texcoords;

uniform sampler2D sampler;

out vec4 o_color;

void main()
{
    o_color = texture(sampler, v_Texcoords);
}