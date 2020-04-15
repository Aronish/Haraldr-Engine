#version 460 core

in vec3 v_LocalPosition;

layout (location = 0) uniform samplerCube environmentMap;

out vec4 o_Color;

void main()
{
    vec3 envColor = texture(environmentMap, v_LocalPosition).rgb;

    //envColor = envColor / (envColor + vec3(1f));
    //envColor = pow(envColor, vec3(1f/2.2f));

    o_Color = vec4(envColor, 1f);
}