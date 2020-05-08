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

layout (binding = 0) uniform samplerCube environmentMap;

out vec4 o_Color;

const float PI = 3.14159265359f;

void main()
{
    vec3 normal = normalize(v_LocalPosition);
    vec3 irradiance = vec3(0.0f);

    vec3 up    = vec3(0.0f, 1.0f, 0.0f);
    vec3 right = cross(up, normal);
    up         = cross(normal, right);

    float sampleDelta = 0.025f;
    float nrSamples = 0.0f;
    for(float phi = 0.0f; phi < 2.0f * PI; phi += sampleDelta)
    {
        for(float theta = 0.0f; theta < 0.5f * PI; theta += sampleDelta)
        {
            // Spherical to cartesian (in tangent space)
            vec3 tangentSample = vec3(sin(theta) * cos(phi),  sin(theta) * sin(phi), cos(theta));
            // Tangent space to world
            vec3 sampleVec = tangentSample.x * right + tangentSample.y * up + tangentSample.z * normal;

            irradiance += texture(environmentMap, sampleVec).rgb * cos(theta) * sin(theta);
            nrSamples++;
        }
    }
    irradiance = PI * irradiance * (1.0f / nrSamples);

    o_Color = vec4(irradiance, 1.0f);
}