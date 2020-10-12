#shader vert
#version 460 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_TextureCoordinate;
layout (location = 2) in vec4 a_Color;

uniform mat4 projection;

out vec2 v_TextureCoordinate;
out vec4 v_Color;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    v_Color = a_Color;
    gl_Position = projection * vec4(a_Position, 0.0f, 1.0f);
}

#shader frag
#version 460 core

in vec2 v_TextureCoordinate;
in vec4 v_Color;

layout (binding = 0) uniform sampler2D fontAtlas;

out vec4 o_Color;

void main()
{
    o_Color = v_Color * vec4(texture(fontAtlas, v_TextureCoordinate).r);
}