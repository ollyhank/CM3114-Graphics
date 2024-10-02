#version 330 core

in  vec4 color;
in  vec2 texCoord;
out vec4 fColor;


uniform sampler2D tex;

void main()
{
    // define the colour as the texture being sample at the given coordinates
    fColor = color * texture(tex, texCoord);
}