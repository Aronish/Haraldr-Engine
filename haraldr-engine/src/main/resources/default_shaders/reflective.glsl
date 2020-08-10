#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TextureCoordinate;
layout (location = 3) in vec3 a_Tangent;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

out vec3 v_Normal_W;
out vec3 v_Position_W;
out vec2 v_TextureCoordinate;

void main()
{
    mat3 normalMatrix   = mat3(model);
    v_Normal_W          = normalMatrix * a_Normal;
    v_Position_W        = (model * vec4(a_Position, 1.0f)).xyz;
    v_TextureCoordinate = a_TextureCoordinate;

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

#switches

in vec3 v_Normal_W;
in vec3 v_Position_W;
in vec2 v_TextureCoordinate;

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

layout (binding = 0) uniform samplerCube c_EnvironmentMap;
#ifdef TEXTURED
layout (binding = 1) uniform sampler2D map_Diffuse_Texture;
layout (binding = 2) uniform sampler2D map_Reflection_Map;
#else
uniform vec3 u_Color;
#endif
uniform float u_Opacity = 1.0f;

out vec4 o_Color;

void main()
{
    vec3 I = normalize(v_Position_W - viewPosition_W);
    vec3 R = reflect(I, normalize(v_Normal_W));
#ifdef TEXTURED
    vec3 color = texture(c_EnvironmentMap, R).rgb * texture(map_Reflection_Map, v_TextureCoordinate).rgb + texture(map_Diffuse_Texture, v_TextureCoordinate).rgb;
#else
    vec3 color = texture(c_EnvironmentMap, R).rgb * u_Color;
#endif
    o_Color = vec4(color, u_Opacity);
}