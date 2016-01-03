uniform sampler2D u_texture;
uniform vec2 u_viewport;

uniform sampler2D u_textodraw;
uniform vec2 u_textodraw_uv1;
uniform vec2 u_textodraw_uv2;
uniform vec2 u_draw_xy;
uniform vec2 u_draw_wh;
uniform mat3 u_draw_rot;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(void){
    vec2 uv = gl_FragCoord.xy/u_viewport;
    vec4 basecol = texture2D(u_texture, uv);
    vec4 drawcol = vec4(0.0);
    
    //vec3 tmp = vec3(uv.u, uv.v, 0.0) * u_draw_rot;
    vec3 newuv = vec3(uv, 0.0) + vec3(vec2((uv - u_draw_xy)/u_draw_wh), 0.0) * u_draw_rot;
    if (newuv.x >= 0.0 && newuv.y >= 0.0 && newuv.x <= 1.0 && newuv.y <= 1.0)
        drawcol = texture2D(u_textodraw, u_textodraw_uv1 + newuv.xy * u_textodraw_uv2);
    gl_FragColor = mix(basecol, drawcol, drawcol.a);
}