#version 460 core

layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

in GS_DATA
{
    vec3 normal;
} gs_data[];

const float MAGNITUDE = 0.4f;

void generateLine(int index)
{
    gl_Position = gl_in[index].gl_Position;
    EmitVertex();
    gl_Position = gl_in[index].gl_Position + vec4(gs_data[index].normal, 0.0f) * MAGNITUDE;
    EmitVertex();
    EndPrimitive();
}

void main()
{
    generateLine(0);
    generateLine(1);
    generateLine(2);
}