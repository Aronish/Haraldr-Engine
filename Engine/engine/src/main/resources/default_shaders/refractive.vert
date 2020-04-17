#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TextureCoordinate;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

out vec3 v_Normal;
out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    mat3 normalMatrix = mat3(model);
    v_Normal = normalMatrix * a_Normal;
    v_FragmentPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}