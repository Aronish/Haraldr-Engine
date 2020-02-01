#version 460 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_TextureCoordinate;
layout(location = 2) in vec3 a_Normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 v_TextureCoordinate;
out vec3 v_Normal;
out vec3 v_FragmentPosition;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    v_Normal = a_Normal;
    v_FragmentPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}