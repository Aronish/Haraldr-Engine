#shader vert
#version 460 core

layout (location = 0) in vec2 a_Position;
layout (location = 1) in vec2 a_TextureCoordinate;

uniform mat4 projection = mat4(1.0f);

out vec2 v_TextureCoordinate;

void main()
{
    v_TextureCoordinate = a_TextureCoordinate;
    gl_Position = projection * vec4(a_Position, 0.0f, 1.0f);
}

#shader frag
#version 460 core

in vec2 v_TextureCoordinate;

layout (binding = 0) uniform sampler2D colorAttachment;
uniform float u_Exposure;

out vec4 o_Color;

void main()
{
    //TODO: Fix attenuation of lighting
    float gamma = 2.2f;

    vec3 hdrColor = texture(colorAttachment, v_TextureCoordinate).rgb;
    vec3 toneMappedColor = vec3(1.0f) - exp(-hdrColor * u_Exposure);

    toneMappedColor = pow(toneMappedColor, vec3(1.0f / gamma));

    o_Color = vec4(toneMappedColor, 1.0f);
}