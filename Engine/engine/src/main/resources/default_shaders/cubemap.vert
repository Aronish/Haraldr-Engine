#version 460 core
layout (location = 0) in vec3 a_Position;

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

out vec3 v_LocalPosition;

void main()
{
    v_LocalPosition = a_Position;

    mat4 rotView = mat4(mat3(view)); // remove translation from the view matrix
    vec4 clipPos = projection * rotView * vec4(v_LocalPosition, 1.f);

    gl_Position = clipPos.xyww;
}