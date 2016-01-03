//uniform sampler2D u_texture;
uniform vec2 u_viewport;
uniform vec4 u_color;

varying vec2 v_texCoords;

void main(void){
    gl_FragColor = u_color;
}