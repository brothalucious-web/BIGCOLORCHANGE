#version 150

uniform sampler2D InSampler;
uniform float Transition;
uniform float ColorCount;
uniform float SkyHue;
uniform float TerrainHue;
uniform float EntitiesHue;
uniform float UiHue;
in vec2 texCoord;
out vec4 fragColor;

vec3 rgbToHsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + 1.0e-10)), d / (q.x + 1.0e-10), q.x);
}
vec3 hsvToRgb(vec3 c) {
    vec3 p = abs(fract(c.xxx + vec3(0.0, 2.0 / 3.0, 1.0 / 3.0)) * 6.0 - 3.0);
    return c.z * mix(vec3(1.0), clamp(p - 1.0, 0.0, 1.0), c.y);
}
vec3 gray(vec3 c) { float l = dot(c, vec3(0.2126, 0.7152, 0.0722)); return vec3(l); }
vec3 colorize(vec3 c, float hue) {
    vec3 hsv = rgbToHsv(c); hsv.x = fract(hue); hsv.y = max(hsv.y, 0.72);
    return hsvToRgb(hsv);
}
void main() {
    vec4 sampled = texture(InSampler, texCoord);
    vec3 mono = gray(sampled.rgb);
    float upper = 1.0 - smoothstep(0.28, 0.62, texCoord.y);
    float worldArea = smoothstep(0.18, 0.55, texCoord.y) * (1.0 - smoothstep(0.82, 0.98, texCoord.y));
    float edgeX = max(1.0 - smoothstep(0.02, 0.14, texCoord.x), smoothstep(0.86, 0.98, texCoord.x));
    float edgeY = max(1.0 - smoothstep(0.02, 0.13, texCoord.y), smoothstep(0.86, 0.98, texCoord.y));
    float hud = max(edgeX, edgeY);
    float skyOn = step(0.5, ColorCount);
    float terrainOn = step(1.5, ColorCount);
    float entitiesOn = step(2.5, ColorCount);
    float uiOn = step(3.5, ColorCount);
    vec3 result = mono;
    result = mix(result, colorize(mono, SkyHue), upper * skyOn * Transition);
    result = mix(result, colorize(mono, TerrainHue), worldArea * terrainOn * Transition);
    result = mix(result, colorize(mono, EntitiesHue), worldArea * entitiesOn * Transition);
    result = mix(result, colorize(mono, UiHue), hud * uiOn * Transition);
    fragColor = vec4(result, sampled.a);
}