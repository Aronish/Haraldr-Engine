#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;

uniform mat4 model = mat4(1.0f);
uniform mat4 mappingView;
uniform mat4 mappingProjection;

out vec3 v_LocalPosition;

void main()
{
    v_LocalPosition = a_Position;
    gl_Position = mappingProjection * mappingView * model * vec4(a_Position, 1.0f);
}

#shader frag
#version 460 core

in vec3 v_LocalPosition;

layout (binding = 0) uniform sampler2D equirectangularMap;

out vec4 o_Color;

const vec2 invAtan = vec2(0.1591f, 0.3183f);

vec2 SampleSphericalMap(vec3 v)
{
    vec2 uv = vec2(atan(v.z, v.x), asin(v.y));
    uv *= invAtan;
    uv += 0.5f;
    return uv;
}

void main()
{
    vec2 uv = SampleSphericalMap(normalize(v_LocalPosition));
    vec3 color = texture(equirectangularMap, uv).rgb;

    o_Color = vec4(color, 1.0f);
}