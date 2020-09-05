#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

uniform mat4 model = mat4(1.0f);
uniform float u_Outline_Size = 0.05f;
uniform bool u_Expand_Normals = true;

layout (std140) uniform matrices
{
    mat4 view;
    mat4 projection;
};

void main()
{
    mat3 scale = mat3(float(!u_Expand_Normals) * u_Outline_Size + 1.0f);
    mat4 scaledModel = model * mat4(scale);
    gl_Position = projection * view * scaledModel * vec4(a_Position + a_Normal * u_Outline_Size * float(u_Expand_Normals), 1.0f);
}

#shader frag
#version 460 core

out vec4 o_Color;

void main()
{
    o_Color = vec4(0.8f, 0.2f, 0.3f, 1.0f);
}