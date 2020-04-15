#version 460 core
layout (location = 0) in vec3 a_Position;

uniform mat4 model = mat4(1f);
uniform mat4 captureView;
uniform mat4 captureProjection;

out vec3 v_LocalPosition;

void main()
{
    v_LocalPosition = a_Position;
    gl_Position = captureProjection * captureView * model * vec4(a_Position, 1f);
}