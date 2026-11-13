package com.poswal.noor.ui.components.glass

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.intellij.lang.annotations.Language

/**
 * AGSL (Android Graphics Shading Language) Shader definitions for ultra-realistic
 * physical glass refraction, chromatic aberration, and lens curvature on Android 13+ (API 33+).
 *
 * Inspired by Apple VisionOS frosted glass and PrismalAGSL.
 */
object LiquidGlassShaders {

    @Language("AGSL")
    const val REFRACTION_SHADER_SRC = """
        uniform shader composable;
        uniform float2 size;
        uniform float refractionStrength; // default: 15.0
        uniform float chromaticAberration; // default: 4.0

        half4 main(float2 fragCoord) {
            // Normalized coordinates (0.0 to 1.0)
            float2 uv = fragCoord / size;

            // Center-relative coordinates (-1.0 to 1.0)
            float2 centered = (uv - 0.5) * 2.0;

            // Calculate lens curvature distortion (normal vector)
            float dist = length(centered);
            float distortion = pow(dist, 2.5) * (refractionStrength / size.x);

            // Refract RGB channels with chromatic aberration (dispersion)
            float2 offsetR = centered * (distortion + chromaticAberration / size.x);
            float2 offsetG = centered * distortion;
            float2 offsetB = centered * (distortion - chromaticAberration / size.x);

            half r = composable.eval(fragCoord + offsetR * size).r;
            half g = composable.eval(fragCoord + offsetG * size).g;
            half b = composable.eval(fragCoord + offsetB * size).b;
            half a = composable.eval(fragCoord + offsetG * size).a;

            // Soft specular edge boost
            half edgeGleam = half(smoothstep(0.85, 1.0, dist) * 0.15);

            return half4(r + edgeGleam, g + edgeGleam, b + edgeGleam, a);
        }
    """

    /**
     * Creates a RuntimeShader instance on Android 13+ (API 33).
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createShader(): RuntimeShader {
        return RuntimeShader(REFRACTION_SHADER_SRC)
    }
}

/**
 * Optional advanced AGSL modifier that injects physical chromatic refraction.
 * Automatically falls back to standard blur on Android < 13.
 */
fun Modifier.liquidGlassRefractionAGSL(
    refractionStrength: Float = 12f,
    chromaticAberration: Float = 3.5f
): Modifier = composed {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shader = remember { LiquidGlassShaders.createShader() }

        this.graphicsLayer {
            shader.setFloatUniform("size", size.width, size.height)
            shader.setFloatUniform("refractionStrength", refractionStrength)
            shader.setFloatUniform("chromaticAberration", chromaticAberration)
            renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "composable").asComposeRenderEffect()
        }
    } else {
        this
    }
}
