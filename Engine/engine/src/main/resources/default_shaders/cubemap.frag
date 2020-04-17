#version 460 core

in vec3 v_LocalPosition;

layout (location = 0) uniform samplerCube environmentMap;

out vec4 o_Color;

void main()
{
    vec3 envColor = texture(environmentMap, v_LocalPosition).rgb;
    o_Color = vec4(envColor, 1.f);
}