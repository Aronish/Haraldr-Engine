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
};

out vec3 v_Normal;
out vec2 v_TextureCoordinate;
out vec3 v_FragmentPosition;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    mat3 normalMatrix = mat3(model);
    v_Normal = normalMatrix * a_Normal;
    v_FragmentPosition = (model * vec4(a_Position, 1.0f)).xyz;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

in vec3 v_Normal;
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;

uniform vec3 viewPosition;
uniform float refractiveRatio;
layout (binding = 0) uniform samplerCube environmentMap;
layout (binding = 1) uniform sampler2D diffuseTexture;
layout (binding = 2) uniform sampler2D refractionMap;

out vec4 o_Color;

void main()
{
    vec3 I = normalize(v_FragmentPosition - viewPosition);
    vec3 R = refract(I, normalize(v_Normal), refractiveRatio);

    vec3 color = texture(environmentMap, R).rgb * texture(refractionMap, v_TextureCoordinate).rgb + texture(diffuseTexture, v_TextureCoordinate).rgb;
    o_Color = vec4(color, 1.f);
}