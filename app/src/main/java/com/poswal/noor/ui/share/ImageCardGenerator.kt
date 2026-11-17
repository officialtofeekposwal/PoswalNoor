package com.poswal.noor.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.poswal.noor.R
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.translationFor
import java.io.File
import java.io.FileOutputStream

object ImageCardGenerator {

    fun shareImageCard(context: Context, item: ContentItemEntity, language: AppLanguage) {
        val bitmap = renderCardBitmap(context, item, language)
        val file = saveBitmapToCache(context, bitmap)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Reflection Card"))
    }

    private fun renderCardBitmap(context: Context, item: ContentItemEntity, language: AppLanguage): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Emerald Gradient Background
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(Color.parseColor("#064E3B"), Color.parseColor("#022C22")),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Ornate Border Frame
        val borderPaint = Paint().apply {
            color = Color.parseColor("#F59E0B") // NoorGold
            alpha = 70
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        val innerBorder = RectF(60f, 60f, width - 60f, height - 60f)
        canvas.drawRoundRect(innerBorder, 32f, 32f, borderPaint)

        // 3. Header Branding
        val headerPaint = TextPaint().apply {
            color = Color.parseColor("#F59E0B")
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("POSWAL NOOR • نور", width / 2f, 150f, headerPaint)

        val subheaderPaint = TextPaint().apply {
            color = Color.parseColor("#D1FAE5")
            textSize = 24f
            alpha = 180
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(item.reference.uppercase(), width / 2f, 195f, subheaderPaint)

        // 4. Arabic Typography (Amiri font)
        val arabicTypeface = try {
            ResourcesCompat.getFont(context, R.font.amiri_bold)
        } catch (e: Exception) {
            null
        }

        val arabicPaint = TextPaint().apply {
            color = Color.parseColor("#F8FAFC")
            textSize = 48f
            typeface = arabicTypeface
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }

        val arabicLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(item.arabic, 0, item.arabic.length, arabicPaint, width - 240)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(16f, 1.2f)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                item.arabic, arabicPaint, width - 240,
                Layout.Alignment.ALIGN_NORMAL, 1.2f, 16f, false
            )
        }

        canvas.save()
        canvas.translate(120f, 320f)
        arabicLayout.draw(canvas)
        canvas.restore()

        val translationY = 360f + arabicLayout.height

        // 5. Divider Line
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#F59E0B")
            alpha = 60
            strokeWidth = 2f
        }
        canvas.drawLine(width / 2f - 80f, translationY, width / 2f + 80f, translationY, dividerPaint)

        // 6. Localized Translation Typography
        val translationText = item.translationFor(language) ?: item.translationEn
        val translationPaint = TextPaint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 34f
            isAntiAlias = true
        }

        val translationLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(translationText, 0, translationText.length, translationPaint, width - 240)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(12f, 1.15f)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                translationText, translationPaint, width - 240,
                Layout.Alignment.ALIGN_CENTER, 1.15f, 12f, false
            )
        }

        canvas.save()
        canvas.translate(120f, translationY + 60f)
        translationLayout.draw(canvas)
        canvas.restore()

        // 7. Footer
        val footerPaint = TextPaint().apply {
            color = Color.parseColor("#10B981")
            textSize = 22f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("100% Offline • Authentic Scripture", width / 2f, height - 100f, footerPaint)

        return bitmap
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
        val cacheFolder = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(cacheFolder, "reflection_card.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}
