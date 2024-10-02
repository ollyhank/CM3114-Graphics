#version 330 core

layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec3 vNormal;
layout(location = 2) in vec2 vTexCoord;

out vec4 color;
out vec2 texCoord;

uniform mat4 ModelView, NormalTransform, Projection;
uniform vec4 LightPosition, AmbientProduct, DiffuseProduct, SpecularProduct;
uniform float Shininess;

void main()
{
    // Transform vertex normal into eye coordinates
    vec3 N = normalize((NormalTransform * vec4(vNormal, 0)).xyz);

    // Transform vertex position into eye coordinates
    vec3 P = (ModelView * vPosition).xyz;
    // Here light position is defined in eye coordinates
    // If Light position is defined in world coordinates,
    // the next line is used instead of the above
    vec3 L = normalize((ModelView * (LightPosition - vPosition)).xyz);
    vec3 E = normalize(-P);
    vec3 R = normalize(-reflect(L, N));

    // constant color defined
    vec4 ambient = AmbientProduct;

    float Kd = max(dot(L, N), 0.0);
    vec4 diffuse = Kd * DiffuseProduct;

    // For original Phong model
    float Ks = pow(max(dot(E, R), 0.0), Shininess);

    vec4 specular = Ks * SpecularProduct;
    if( dot(L, N) < 0.0 ) {
        // specular reflection is only applied to the lit side
        specular = vec4(0.0, 0.0, 0.0, 1.0);
    }

    // change vertex position to world coordinates
    gl_Position = Projection*ModelView*vPosition;

    // sends to fragment shader
    texCoord = vTexCoord;

    color = ambient + diffuse + specular;
    // makes cube opaque
    color.a = 1.0;
}