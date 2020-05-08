#shader vert
#version 460 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 3) in vec3 a_Tangent;

uniform mat4 model = mat4(1.0f);

layout (std140, binding = 0) uniform matrices
{
    mat4 view;
    mat4 projection;
};

out GS_DATA
{
    vec3 normal;
    vec3 tangent;
} gs_data;

void main()
{
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
    mat3 normalMatrix = mat3(view * model);
    gs_data.normal = normalize(vec3(projection * vec4(normalMatrix * a_Normal, 0.0f)));
    //gs_data.tangent = normalize(vec3(projection * vec4(normalMatrix * a_Tangent, 0.0f)));
}

#shader geom
#version 460 core

layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

in GS_DATA
{
    vec3 normal;
    vec3 tangent;
} gs_data[];

const float MAGNITUDE = 1f;

void generateNormal(int index)
{
    gl_Position = gl_in[index].gl_Position;
    EmitVertex();
    gl_Position = gl_in[index].gl_Position + vec4(gs_data[index].normal, 0.0f) * MAGNITUDE;
    EmitVertex();
    EndPrimitive();
}

void generateTangent(int index)
{
    gl_Position = gl_in[index].gl_Position;
    EmitVertex();
    gl_Position = gl_in[index].gl_Position + vec4(gs_data[index].tangent, 0.0f) * MAGNITUDE;
    EmitVertex();
    EndPrimitive();
}

void main()
{
    generateNormal(0);
    generateNormal(1);
    generateNormal(2);
}

#shader frag
#version 460 core

out vec4 o_Color;

void main()
{
    o_Color = vec4(1.0f, 1.0f, 0.0f, 1.0f);
}