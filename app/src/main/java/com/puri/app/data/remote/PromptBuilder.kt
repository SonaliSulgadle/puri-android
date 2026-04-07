package com.puri.app.data.remote

import com.puri.app.domain.model.AppLanguage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptBuilder @Inject constructor() {

    companion object {
        private const val RESPONSE_FORMAT = """
Respond ONLY in this exact format, no deviation:
WHAT: [one clear sentence identifying the item or answering the question]
DESCRIPTION: [one sentence of useful context]
STEPS:
1. [action title] | [one sentence description]
2. [action title] | [one sentence description]
(add steps as needed, maximum 6)
WARNING: [one sentence if genuinely important, or write NONE]
TIP: [one Korea-specific insight a foreigner would not know, or write NONE]
CONFIDENCE: [HIGH if you are certain, LOW if image is unclear or you are unsure]
CATEGORY: [exactly one of: TRASH, APPLIANCE, TRANSPORT, FOOD, GENERAL]
"""

        private const val SYSTEM_INSTRUCTIONS = """
You are Puri, a daily life assistant for foreigners living in South Korea.
Your job is to explain Korean appliances, signs, trash rules, transport 
systems, and daily life situations quickly and practically.

Core rules you must never break:
1. If the image is blurry, too dark, partially visible, or unidentifiable, 
   set CONFIDENCE to LOW and leave WHAT as "I could not identify this clearly."
2. If the question or image involves medication, medical symptoms, 
   electrical wiring inside walls, or gas pipe installation, respond with:
   WHAT: This requires professional help.
   CONFIDENCE: LOW
   And add to WARNING: Please contact 119 (emergency) or a professional.
3. Never guess when uncertain. Accuracy over completeness.
4. Keep every field concise — users need answers in seconds, not paragraphs.
5. Use simple English. No technical jargon unless you immediately explain it.
"""
    }

    fun buildImagePrompt(
        additionalContext: String? = null,
        language: AppLanguage = AppLanguage.ENGLISH
    ): String {
        val languageInstruction = when (language) {
            AppLanguage.ENGLISH -> "Respond in English."
            AppLanguage.KOREAN -> "Respond in Korean (한국어로 답변해주세요)."
        }

        val contextInstruction = if (!additionalContext.isNullOrBlank()) {
            "Additional context from the user: \"$additionalContext\""
        } else ""

        return """
$SYSTEM_INSTRUCTIONS

$languageInstruction

The user has shared a photo taken in South Korea.
Identify what this is and explain exactly what the user should DO with it.
Focus on actions, not descriptions.
$contextInstruction

$RESPONSE_FORMAT

Example:
WHAT: LDPE plastic recycling bin
DESCRIPTION: Designated for soft plastics like bags and squeeze bottles.
STEPS:
1. Remove label | Peel off any stickers or paper labels
2. Rinse | Remove all food residue with water
3. Flatten | Compress the container to save space
4. Dispose | Place in the bin marked 플라스틱 (plastic)
WARNING: Do not mix with food waste — this results in a ₩300,000 fine in Seoul
TIP: Korean recycling is strictly enforced. When in doubt, check the number inside the recycling triangle
CONFIDENCE: HIGH
CATEGORY: TRASH
""".trimIndent()
    }

    fun buildTextPrompt(
        query: String,
        language: AppLanguage = AppLanguage.ENGLISH
    ): String {
        val languageInstruction = when (language) {
            AppLanguage.ENGLISH -> "Respond in English."
            AppLanguage.KOREAN -> "Respond in Korean (한국어로 답변해주세요)."
        }

        return """
$SYSTEM_INSTRUCTIONS

$languageInstruction

The user has typed this question about daily life in South Korea:
"$query"

Answer as if the user just arrived in Korea and has no prior knowledge.
Be direct. Lead with the most important step first.

$RESPONSE_FORMAT
""".trimIndent()
    }
}