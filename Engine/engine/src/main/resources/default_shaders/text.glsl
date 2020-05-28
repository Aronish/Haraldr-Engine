#shader vert
#version 460 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_TextureCoordinate;

uniform mat4 model;
uniform mat4 projection;

out vec2 v_TextureCoordinate;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    gl_Position = projection * model * vec4(a_Position, 0.0f, 1.0f);
}

#shader frag
#version 460 core

in vec2 v_TextureCoordinate;

uniform vec4 u_Color;
layout (binding = 0) uniform sampler2D fontAtlas;

out vec4 o_Color;

void main()
{
    o_Color = u_Color * vec4(texture(fontAtlas, v_TextureCoordinate).r);
}