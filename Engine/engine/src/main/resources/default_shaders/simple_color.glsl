#shader vert
#version 460 core

layout(location = 0) in vec3 a_Position;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

void main()
{
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

uniform vec3 u_Color;

out vec4 o_Color;

void main()
{
    o_Color = vec4(u_Color, 1.0f);
}
