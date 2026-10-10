package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.DiagnosisResult
import com.example.model.HealthStatus
import com.example.model.Language
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    /**
     * Generates a high quality, well-formatted A4 PDF report for a crop diagnosis
     * and triggers Android's system share/view sheet.
     */
    fun exportAndShareReport(
        context: Context,
        diagnosis: DiagnosisResult,
        language: Language
    ): File? {
        val document = PdfDocument()
        val pageWidth = 595 // A4 standard width in points
        val pageHeight = 842 // A4 standard height in points
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        try {
            drawReportContent(canvas, pageWidth, pageHeight, diagnosis, language)
            document.finishPage(page)

            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val sanitizedCrop = diagnosis.crop.nameEn.replace(" ", "_")
            val fileName = "KrushiSahayak_${sanitizedCrop}_${System.currentTimeMillis()}.pdf"
            val file = File(reportsDir, fileName)

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }

            sharePdfFile(context, file, diagnosis, language)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
    }

    private fun drawReportContent(
        canvas: Canvas,
        pageWidth: Int,
        pageHeight: Int,
        diagnosis: DiagnosisResult,
        language: Language
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var y = 36f

        // 1. TOP HEADER BANNER
        paint.color = Color.rgb(27, 94, 32) // Forest green #1B5E20
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 72f, paint)

        // Accent gold/amber line
        paint.color = Color.rgb(255, 179, 0) // Amber #FFB300
        canvas.drawRect(0f, 72f, pageWidth.toFloat(), 76f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 17f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("KRUSHI SAHAYAK - CROP HEALTH DIAGNOSIS REPORT", 24f, 34f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val subtitle = when (language) {
            Language.GUJARATI -> "કૃષિ સહાયક - સ્માર્ટ પાક રોગ નિદાન અને સંકલિત જીવાત વ્યવસ્થાપન રિપોર્ટ"
            Language.HINDI -> "कृषि सहायक - स्मार्ट फसल रोग निदान एवं समेकित कीट प्रबंधन रिपोर्ट"
            Language.ENGLISH -> "Comprehensive Agricultural Crop Health & IPM Treatment Advisory"
        }
        canvas.drawText(subtitle, 24f, 52f, paint)

        y = 96f

        // 2. METADATA ROW (Date, Report ID, Mode)
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 8.5f
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(diagnosis.timestamp))
        val modeStr = if (diagnosis.isOnlineAnalysis) "Mode: Online AI Multimodal" else "Mode: Offline Agricultural Knowledge Base"
        canvas.drawText("Report ID: KS-${diagnosis.id % 1000000}  |  Date: $dateStr  |  $modeStr", 24f, y, paint)

        y += 16f

        // 3. HERO DIAGNOSIS CARD
        val cardRect = RectF(24f, y, pageWidth - 24f, y + 84f)
        paint.color = when (diagnosis.healthStatus) {
            HealthStatus.HEALTHY -> Color.rgb(232, 245, 233)
            HealthStatus.NOT_A_CROP -> Color.rgb(254, 242, 242)
            else -> Color.rgb(255, 243, 224)
        }
        canvas.drawRoundRect(cardRect, 10f, 10f, paint)

        // Card Border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.2f
        paint.color = when (diagnosis.healthStatus) {
            HealthStatus.HEALTHY -> Color.rgb(76, 175, 80)
            HealthStatus.NOT_A_CROP -> Color.rgb(239, 68, 68)
            else -> Color.rgb(255, 152, 0)
        }
        canvas.drawRoundRect(cardRect, 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        // Card Content
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val cropTitle = "${diagnosis.crop.getLocalizedName(language)} (${diagnosis.crop.nameEn}) - ${diagnosis.crop.scientificName}"
        canvas.drawText(cropTitle, 36f, y + 24f, paint)

        paint.color = when (diagnosis.healthStatus) {
            HealthStatus.HEALTHY -> Color.rgb(46, 125, 50)
            HealthStatus.NOT_A_CROP -> Color.rgb(185, 28, 28)
            else -> Color.rgb(194, 65, 12)
        }
        paint.textSize = 12f
        val problemText = "Condition: ${diagnosis.problemName}  [${diagnosis.healthStatus.getDisplayName(language)}]"
        canvas.drawText(problemText, 36f, y + 46f, paint)

        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val confText = "Confidence: ${diagnosis.confidenceLevel.getDisplayName(language)} (${diagnosis.confidencePercent}%)  |  Plant Part: ${diagnosis.plantPart.getDisplayName(language)}"
        canvas.drawText(confText, 36f, y + 68f, paint)

        y += 100f

        // Helper to draw clean sections
        fun drawSection(title: String, content: String, icon: String = "•"): Float {
            if (content.isBlank()) return y

            // Section Header
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 10.5f
            paint.color = Color.rgb(27, 94, 32)
            canvas.drawText("$icon $title", 24f, y, paint)

            y += 13f

            // Section Body Text (word wrapped)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 8.5f
            paint.color = Color.rgb(30, 41, 59)

            val maxWidth = (pageWidth - 48).toFloat()
            val words = content.replace("\n", " ").split(" ")
            var line = StringBuilder()

            for (word in words) {
                val testLine = if (line.isEmpty()) word else "${line} $word"
                if (paint.measureText(testLine) > maxWidth) {
                    canvas.drawText(line.toString(), 28f, y, paint)
                    y += 11f
                    line = StringBuilder(word)
                } else {
                    line = StringBuilder(testLine)
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line.toString(), 28f, y, paint)
                y += 11f
            }

            y += 8f
            return y
        }

        // Draw Structured Sections
        drawSection(
            title = when (language) {
                Language.GUJARATI -> "શું થઈ રહ્યું છે? (લક્ષણો)"
                Language.HINDI -> "क्या हो रहा है? (लक्षण)"
                Language.ENGLISH -> "What is happening? (Symptoms)"
            },
            content = diagnosis.whatIsHappening,
            icon = "🔍"
        )

        drawSection(
            title = when (language) {
                Language.GUJARATI -> "આ શા માટે થાય છે? (કારણો અને વાતાવરણ)"
                Language.HINDI -> "यह क्यों होता है? (कारण एवं मौसम)"
                Language.ENGLISH -> "Why it happens? (Causes & Weather Factors)"
            },
            content = diagnosis.whyItHappens,
            icon = "🌦️"
        )

        drawSection(
            title = when (language) {
                Language.GUJARATI -> "ખેતરમાં શું જોવું? (ચકાસણી)"
                Language.HINDI -> "खेत में क्या देखें? (जांच)"
                Language.ENGLISH -> "What to look for? (Field Scouting)"
            },
            content = diagnosis.whatToLookFor,
            icon = "👁️"
        )

        drawSection(
            title = when (language) {
                Language.GUJARATI -> "બચાવ અને ખેતી પદ્ધતિઓ (રોગ નિવારણ)"
                Language.HINDI -> "बचाव एवं कृषि पद्धतियां (रोकथाम)"
                Language.ENGLISH -> "How to prevent? (Cultural Agronomic Prevention)"
            },
            content = diagnosis.howToPrevent,
            icon = "🛡️"
        )

        if (diagnosis.nonChemicalControl.isNotBlank()) {
            drawSection(
                title = when (language) {
                    Language.GUJARATI -> "દેશી અને જૈવિક ઉપાયો (IPM / લીમડાનું તેલ)"
                    Language.HINDI -> "देसी एवं जैविक उपाय (IPM / नीम तेल)"
                    Language.ENGLISH -> "Non-chemical / Biological / IPM Controls"
                },
                content = diagnosis.nonChemicalControl,
                icon = "🌱"
            )
        }

        // PESTICIDE RECOMMENDATION (If any)
        if (diagnosis.verifiedPesticide != null) {
            val p = diagnosis.verifiedPesticide
            val pestHeader = when (language) {
                Language.GUJARATI -> "ભલામણ કરેલ રસાયણિક દવા (CIBRC પ્રમાણિત)"
                Language.HINDI -> "अनुशंसित रासायनिक दवा (CIBRC प्रमाणित)"
                Language.ENGLISH -> "Verified Chemical Control (CIBRC Standard)"
            }
            val pestContent = buildString {
                append("Active Ingredient: ${p.activeIngredient} (${p.formulation})\n")
                append("Dosage & Mixing Rate: ${p.getLocalizedApplicationRate(language)}\n")
                append("Pre-Harvest Interval (PHI): ${p.preHarvestIntervalDays} days | Authority: ${p.verifiedAuthority}")
            }
            drawSection(title = pestHeader, content = pestContent, icon = "💊")
        }

        // FARMER'S FIELD NOTES (If provided during camera step)
        if (!diagnosis.notes.isNullOrBlank()) {
            drawSection(
                title = when (language) {
                    Language.GUJARATI -> "ખેડૂતે નોંધેલા લક્ષણો / વિગતો"
                    Language.HINDI -> "किसान द्वारा दर्ज किए गए लक्षण / विवरण"
                    Language.ENGLISH -> "Farmer's Field Notes & Observations"
                },
                content = diagnosis.notes,
                icon = "✏️"
            )
        }

        // 4. FOOTER NOTE & WATERMARK
        val footerY = pageHeight - 34f
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawLine(24f, footerY - 12f, pageWidth - 24f, footerY - 12f, paint)

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 7.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        val disclaimer = when (language) {
            Language.GUJARATI -> "કૃષિ સહાયક - આ રિપોર્ટ વૈજ્ઞાનિક માહિતી અને AI મોડેલ આધારિત છે. સ્થાનિક કૃષિ વિજ્ઞાન કેન્દ્ર (KVK) ની સલાહ લેવી."
            Language.HINDI -> "कृषि सहायक - यह रिपोर्ट वैज्ञानिक जानकारी एवं AI आधारित है। स्थानीय कृषि विज्ञान केंद्र (KVK) से भी परामर्श लें।"
            Language.ENGLISH -> "Krushi Sahayak - Generated for farmer guidance based on ICAR/CIBRC standards. Consult local KVK agronomists for field verification."
        }
        canvas.drawText(disclaimer, 24f, footerY, paint)
    }

    private fun sharePdfFile(
        context: Context,
        file: File,
        diagnosis: DiagnosisResult,
        language: Language
    ) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val subject = "${diagnosis.crop.getLocalizedName(language)} - ${diagnosis.problemName} Report (Krushi Sahayak)"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(
                Intent.EXTRA_TEXT,
                "Crop Health Report for ${diagnosis.crop.getLocalizedName(language)}: ${diagnosis.problemName}. Generated with Krushi Sahayak."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooserIntent = Intent.createChooser(
            shareIntent,
            when (language) {
                Language.GUJARATI -> "પાક રોગ રિપોર્ટ શેર કરો (Share Report)"
                Language.HINDI -> "फसल रिपोर्ट शेयर करें (Share Report)"
                Language.ENGLISH -> "Share Crop Health Report"
            }
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }
}
