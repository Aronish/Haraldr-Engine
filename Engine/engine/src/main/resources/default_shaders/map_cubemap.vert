#version 460 core
layout (location = 0) in vec3 a_Position;

uniform mat4 model = mat4(1.f);
uniform mat4 mappingView;
uniform mat4 mappingProjection;

out vec3 v_LocalPosition;

void main()
{
    v_LocalPosition = a_Position;
    gl_Position = mappingProjection * mappingView * model * vec4(a_Position, 1.f);
}