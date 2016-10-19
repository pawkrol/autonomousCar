#version 400 core

in vec3 position;
in vec2 texCoords;

out vec2 p_texCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
    vec4 worldMatrix = transformationMatrix * vec4(position, 1.0);

    gl_Position = projectionMatrix * viewMatrix * worldMatrix;
    p_texCoords = texCoords;
}