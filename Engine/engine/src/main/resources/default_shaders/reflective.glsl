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
};

out vec3 v_Normal_W;
out vec3 v_WorldPosition_W;
out vec2 v_TextureCoordinate;

out mat3 v_TBN;

void main()
{
    mat3 normalMatrix   = mat3(model);
    v_Normal_W          = normalMatrix * a_Normal;
    v_WorldPosition_W   = (model * vec4(a_Position, 1.0f)).xyz;
    v_TextureCoordinate = a_TextureCoordinate;

    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

in vec3 v_Normal_W;
in vec3 v_WorldPosition_W;
in vec2 v_TextureCoordinate;

uniform vec3 u_ViewPosition_W;

layout (binding = 0) uniform samplerCube environmentMap;
layout (binding = 1) uniform sampler2D diffuseTexture;
layout (binding = 2) uniform sampler2D reflectionMap;

out vec4 o_Color;

void main()
{
    vec3 I = normalize(v_WorldPosition_W - u_ViewPosition_W);
    vec3 R = reflect(I, normalize(v_Normal_W));

    vec3 color = texture(environmentMap, R).rgb * texture(reflectionMap, v_TextureCoordinate).rgb + texture(diffuseTexture, v_TextureCoordinate).rgb;
    o_Color = vec4(color, 1.0f);
}