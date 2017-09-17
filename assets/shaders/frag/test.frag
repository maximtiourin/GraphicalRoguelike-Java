#version 120

//uniforms
uniform sampler2D texture;
uniform vec3 color;
uniform float radius;
uniform vec2 player;
uniform vec2 tile;
  
void main() {
	float alpha = 1.0;

    //Determine distance from player to tile pixel;
    //float distanceBetween = distance(player, tile);
    //alpha = (distanceBetween / radius);
    //alpha = min(alpha, 1.0);
    //alpha = max(alpha, 0.0);
    
    //vec4 testColor = texture2D(texture, gl_TexCoord[0].st);
    
    //End
    //gl_FragColor = (1.0 - alpha) * testColor + alpha * color;
    
    gl_FragColor = gl_Color;
}