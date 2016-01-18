attribute vec4 a_position;
attribute vec2 a_texCoords;
uniform mat4 u_worldView;

varying vec2 v_position;
varying vec2 v_texCoords;


void main(){
    v_texCoords = a_texCoords;
    gl_Position =  u_worldView * a_position;
    v_position = gl_Position.xy; 
    //gl_Position = vec4(a_position.x, a_position.y, 0.0, 1.0);
}