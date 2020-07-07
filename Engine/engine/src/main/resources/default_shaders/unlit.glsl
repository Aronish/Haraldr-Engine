#shader vert
#version 460 core

layout  (location = 0) in vec3 a_Position;
layout  (location = 2) in vec2 a_TextureCoordinate;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

out vec2 v_TextureCoordinate;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#switches

#ifdef TEXTURED
layout (binding = 0) uniform sampler2D map_0_Texture;
#else
uniform vec3 u_Color;
#endif
uniform float u_Opacity;

in vec2 v_TextureCoordinate;

out vec4 o_Color;

void main()
{
#ifdef TEXTURED
    o_Color = vec4(texture(map_0_Texture, v_TextureCoordinate).rgb, u_Opacity);
#else
    o_Color = vec4(u_Color, u_Opacity);
#endif
}
