#version 460 core

in vec3 v_LocalPosition;
layout (location = 0) uniform sampler2D equirectangularMap;
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
    vec2 uv = SampleSphericalMap(normalize(v_LocalPosition)); // make sure to normalize v_LocalPosition
    vec3 color = texture(equirectangularMap, uv).rgb;
    //Gamma correction
    color = color / (color + vec3(1.f));
    color = pow(color, vec3(1.f/2.2f));

    o_Color = vec4(color, 1.f);
}