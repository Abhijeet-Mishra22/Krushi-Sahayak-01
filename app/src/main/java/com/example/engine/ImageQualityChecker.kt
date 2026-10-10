package com.example.engine

import android.graphics.Bitmap
import android.graphics.Color
import com.example.model.ImageQualityResult
import kotlin.math.abs

object ImageQualityChecker {

    /**
     * Analyzes the bitmap for clarity, brightness, and botanical/plant characteristics.
     */
    fun analyze(bitmap: Bitmap): ImageQualityResult {
        // Downsample to a standardized sample grid for fast, battery-efficient analysis
        val targetWidth = 160
        val targetHeight = (bitmap.height * (160f / bitmap.width)).toInt().coerceAtLeast(120)
        val scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        var totalLuminance = 0.0
        var plantPixelCount = 0
        var totalEdgeDiff = 0.0
        val totalPixels = targetWidth * targetHeight

        val pixels = IntArray(totalPixels)
        scaled.getPixels(pixels, 0, targetWidth, 0, 0, targetWidth, targetHeight)

        for (y in 0 until targetHeight) {
            for (x in 0 until targetWidth) {
                val index = y * targetWidth + x
                val pixel = pixels[index]

                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                // Luminance calculation
                val lum = 0.299 * r + 0.587 * g + 0.114 * b
                totalLuminance += lum

                // Edge sharpness via horizontal adjacent pixel delta
                if (x < targetWidth - 1) {
                    val nextPixel = pixels[index + 1]
                    val nextLum = 0.299 * Color.red(nextPixel) + 0.587 * Color.green(nextPixel) + 0.114 * Color.blue(nextPixel)
                    totalEdgeDiff += abs(lum - nextLum)
                }

                // Plant chlorophyll / vegetative tint detection
                // Healthy or diseased leaves show either green dominance (g > r and g > b)
                // or characteristic chlorotic/yellow/brown necrotic tissue (r > b and g > b)
                val isGreenPlant = (g > r * 0.95f) && (g > b * 1.05f)
                val isYellowNecroticLeaf = (r > 60 && g > 60 && (r + g) > (b * 1.8f))
                if (isGreenPlant || isYellowNecroticLeaf) {
                    plantPixelCount++
                }
            }
        }

        val avgLuminance = totalLuminance / totalPixels
        val avgEdgeSharpness = totalEdgeDiff / totalPixels
        val plantPixelRatio = plantPixelCount.toDouble() / totalPixels

        // Quality Rules
        val isTooDark = avgLuminance < 35.0
        val isTooBright = avgLuminance > 235.0
        val isBlurry = avgEdgeSharpness < 4.2
        val hasPlant = plantPixelRatio >= 0.12

        return when {
            isTooDark -> ImageQualityResult(
                isClear = false,
                blurScore = avgEdgeSharpness,
                brightnessScore = avgLuminance,
                hasPlantCharacteristics = hasPlant,
                errorMessageEn = "The photo is too dark. Please take a photo in clear daylight.",
                errorMessageHi = "फोटो बहुत अंधेरी है। कृपया दिन के पर्याप्त उजाले में फोटो लें।",
                errorMessageGu = "ફોટોમાં ખૂબ અંધારું છે. કૃપા કરીને દિવસના અજવાળામાં ફોટો પાડો."
            )
            isTooBright -> ImageQualityResult(
                isClear = false,
                blurScore = avgEdgeSharpness,
                brightnessScore = avgLuminance,
                hasPlantCharacteristics = hasPlant,
                errorMessageEn = "The photo is overexposed or too bright. Avoid direct flash reflection.",
                errorMessageHi = "फोटो में बहुत अधिक चमक है। सीधी तेज धूप या फ्लैश से बचें।",
                errorMessageGu = "ફોટો ખૂબ ઝાંખો કે સફેદ પડી ગયો છે. સીધા ફ્લેશ વગર ફોટો પાડો."
            )
            isBlurry -> ImageQualityResult(
                isClear = false,
                blurScore = avgEdgeSharpness,
                brightnessScore = avgLuminance,
                hasPlantCharacteristics = hasPlant,
                errorMessageEn = "The photo is blurry. Please hold the phone steady and focus on the affected leaf.",
                errorMessageHi = "फोटो धुंधली (ब्लर) आई है। मोबाइल को स्थिर रखकर पत्ते पर फोकस करें।",
                errorMessageGu = "ફોટો ધૂંધળો (બ્લર) આવ્યો છે. ફોન સ્થિર રાખી રોગવાળા પાન પર ફોકસ કરો."
            )
            !hasPlant -> ImageQualityResult(
                isClear = false,
                blurScore = avgEdgeSharpness,
                brightnessScore = avgLuminance,
                hasPlantCharacteristics = false,
                errorMessageEn = "Plant characteristics not detected. Please take a close-up photo of the crop leaf or stem.",
                errorMessageHi = "पौधे या पत्ते की पहचान नहीं हुई। कृपया फसल के पत्ते या तने की नजदीकी फोटो लें।",
                errorMessageGu = "છોડ કે પાંદડું ઓળખાયું નથી. કૃપા કરીને પાક કે પાંદડાનો નજીકથી ફોટો પાડો."
            )
            else -> ImageQualityResult(
                isClear = true,
                blurScore = avgEdgeSharpness,
                brightnessScore = avgLuminance,
                hasPlantCharacteristics = true
            )
        }
    }
}
