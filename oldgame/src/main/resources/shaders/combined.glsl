#shader vert
#version 460 core

layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec2 a_TextureCoordinates;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 v_TextureCoordinates;

void main()
{
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = projection * view * model * vec4(a_Position, 0.0f, 1.0f);
}

#shader frag
#version 460 core

in vec2 v_TextureCoordinates;

uniform sampler2D sampler;
uniform vec4 color;

out vec4 o_FragmentColor;

void main()
{
    o_FragmentColor = texture(sampler, v_TextureCoordinates) * color;
}