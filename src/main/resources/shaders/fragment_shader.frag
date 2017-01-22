#version 130

in vec2 p_texCoords;

out vec4 out_Color;

uniform sampler2D texSampler;

void main(void){
    out_Color = texture(texSampler, p_texCoords);
}
