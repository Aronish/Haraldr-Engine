#shader vert
#version 460 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TextureCoordinate;
layout (location = 3) in float a_TextureIndex;

uniform mat4 projection;

out vec4 v_Color;
out vec2 v_TextureCoordinate;
flat out float v_TextureIndex;

void main()
{
    v_Color = a_Color;
    v_TextureCoordinate = a_TextureCoordinate;
    v_TextureIndex = a_TextureIndex;
    gl_Position = projection * vec4(a_Position, 0.0f, 1.0f);
}

#shader frag
#version 460 core

in vec4 v_Color;
in vec2 v_TextureCoordinate;
flat in float v_TextureIndex;

layout (binding = 0) uniform sampler2D textures[32];

out vec4 o_Color;

void main()
{
    o_Color = vec4(texture(textures[int(v_TextureIndex)], v_TextureCoordinate).rgb, 1.0f) * v_Color;
}