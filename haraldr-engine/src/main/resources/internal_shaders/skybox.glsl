#shader vert
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

    mat4 rotView = mat4(mat3(view)); // Remove translation from the view matrix
    vec4 clipPos = projection * rotView * vec4(v_LocalPosition, 1.0f);

    gl_Position = clipPos.xyww;
}

#shader frag
#version 460 core

in vec3 v_LocalPosition;

layout (binding = 0) uniform samplerCube environmentMap;

out vec4 o_Color;

void main()
{
    vec3 envColor = texture(environmentMap, v_LocalPosition).rgb;
    o_Color = vec4(envColor, 1.0f);
}