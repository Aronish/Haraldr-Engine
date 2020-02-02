#version 460 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec2 a_TextureCoordinate;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out GS_DATA
{
    vec3 normal;
} gs_data;

void main()
{
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
    mat3 normalMatrix = mat3(transpose(inverse(view * model)));
    gs_data.normal = normalize(vec3(projection * vec4(normalMatrix * a_Normal, 0.0f)));
}