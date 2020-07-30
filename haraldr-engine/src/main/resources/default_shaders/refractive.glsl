#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in vec2 a_TextureCoordinate;

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
    mat3 normalMatrix = mat3(model);
    v_Normal_W = normalMatrix * a_Normal;
    v_Position_W = (model * vec4(a_Position, 1.0f)).xyz;
    v_TextureCoordinate = a_TextureCoordinate;

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

in vec3 v_Normal_W;
in vec3 v_Position_W;
in vec2 v_TextureCoordinate;

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
    vec3 viewPosition_W;
};

uniform float u_Begin_Medium = 1.0;
uniform float u_End_Medium = 1.52;

layout (binding = 0) uniform samplerCube c_EnvironmentMap;

layout (binding = 1) uniform sampler2D map_Diffuse_Texture;
layout (binding = 2) uniform sampler2D map_Refraction_Map;

out vec4 o_Color;

void main()
{
    vec3 I = normalize(v_Position_W - viewPosition_W);
    vec3 R = refract(I, normalize(v_Normal_W), u_Begin_Medium / u_End_Medium);

    vec3 color = texture(c_EnvironmentMap, R).rgb * texture(map_Refraction_Map, v_TextureCoordinate).rgb + texture(map_Diffuse_Texture, v_TextureCoordinate).rgb;
    o_Color = vec4(color, 1.f);
}