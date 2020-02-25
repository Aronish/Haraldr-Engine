#version 460 core

layout(location = 0) in vec3 a_Position;

uniform mat4 model;

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

void main()
{
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}