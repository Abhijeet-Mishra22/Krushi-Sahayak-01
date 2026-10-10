package com.example.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _activeUtterance = MutableStateFlow<String?>(null)
    val activeUtterance: StateFlow<String?> = _activeUtterance.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setSpeechRate(0.92f) // Slightly slower rate for outdoor clarity
            tts?.setPitch(1.0f)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _activeUtterance.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _activeUtterance.value = null
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _activeUtterance.value = null
                }
            })
        }
    }

    fun speak(text: String, language: Language, sectionTitle: String? = null) {
        if (!isInitialized || tts == null) return

        stop()

        val locale = when (language) {
            Language.GUJARATI -> Locale.forLanguageTag("gu-IN")
            Language.HINDI -> Locale.forLanguageTag("hi-IN")
            Language.ENGLISH -> Locale.forLanguageTag("en-IN")
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to Hindi or default English if Gujarati voice data is not present on device
            if (language == Language.GUJARATI) {
                tts?.setLanguage(Locale.forLanguageTag("hi-IN"))
            } else {
                tts?.setLanguage(Locale.US)
            }
        }

        val spokenText = if (sectionTitle != null) "$sectionTitle. $text" else text
        val utteranceId = "kisan_${System.currentTimeMillis()}"
        _isSpeaking.value = true
        _activeUtterance.value = sectionTitle ?: "full_diagnosis"
        tts?.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _activeUtterance.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
