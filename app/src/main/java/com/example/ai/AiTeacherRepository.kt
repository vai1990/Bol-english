package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiTeacherRepository"

class AiTeacherRepository {

    private val teacherReplySchema = Schema.obj(
        properties = mapOf(
            "englishSpeech" to Schema.string(
                description = "Warm, joyful, simple English spoken reply by Meera Teacher (1 to 3 short child-friendly sentences, plain text with no markdown or emojis)."
            ),
            "marathiSupport" to Schema.string(
                description = "Short encouraging Marathi explanation or translation in Devanagari script (1 to 2 simple sentences, plain text with no markdown)."
            ),
            "practicePhrase" to Schema.string(
                description = "One short English word or simple phrase from this turn for the child to practice repeating aloud, e.g. 'I like red apples'.",
                nullable = true
            ),
            "quickReplies" to Schema.array(
                items = Schema.string(),
                description = "2 to 3 very short, child-friendly English reply options the child can tap or say next."
            )
        ),
        optionalProperties = listOf("practicePhrase")
    )

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
                responseMimeType = "application/json"
                responseSchema = teacherReplySchema
            },
            systemInstruction = content {
                text(
                    """
                    You are Meera Teacher (मीरा टीचर), a warm, cheerful, patient female English teacher inside a kids learning app for young Marathi-speaking children (ages 4 to 10).
                    Your mission is to teach English through joyful spoken conversation!

                    Rules you MUST follow on every single reply:
                    1. Speak in simple, encouraging, age-appropriate English in `englishSpeech` (1 to 3 short sentences max so it sounds natural when read aloud by Text-to-Speech).
                    2. Always provide a warm Marathi meaning, tip, or praise in Devanagari script in `marathiSupport` so young children understand effortlessly.
                    3. If the child speaks or asks in Marathi (or Hinglish/broken English), praise their effort warmly, gently teach the correct simple English phrase, and invite them to say it back!
                    4. Always end your `englishSpeech` with ONE easy, playful follow-up question or repeat-after-me prompt to keep the conversation flowing.
                    5. Never use markdown symbols (*, **, #, bullet lists) or emojis inside `englishSpeech` because it is read directly by a Text-to-Speech voice engine.
                    6. Keep everything 100% safe, positive, and fun for children.
                    """.trimIndent()
                )
            }
        )
    }

    private var chatSession: Chat? = null

    fun startNewSession(kidName: String) {
        chatSession = model.startChat(
            history = listOf(
                content("user") {
                    text("Hi Meera Teacher! My name is $kidName and I am learning to speak English.")
                },
                content("model") {
                    text(
                        JSONObject().apply {
                            put(
                                "englishSpeech",
                                "Hello $kidName! I am Meera Teacher. I am so happy to talk with you! What is your favorite animal?"
                            )
                            put(
                                "marathiSupport",
                                "नमस्ते $kidName! मी मीरा टीचर आहे. आज आपण मजेत इंग्रजी बोलूया. तुझा आवडता प्राणी कोणता आहे?"
                            )
                            put("practicePhrase", "My favorite animal is a dog")
                            put(
                                "quickReplies",
                                org.json.JSONArray(
                                    listOf("I like dogs!", "I like lions!", "Tell me a story")
                                )
                            )
                        }.toString()
                    )
                }
            )
        )
    }

    suspend fun sendChildMessage(
        userMessage: String,
        kidName: String
    ): TeacherReplyPayload {
        val activeChat = chatSession ?: run {
            startNewSession(kidName)
            requireNotNull(chatSession)
        }

        try {
            val response = withTimeout(60_000L) {
                activeChat.sendMessage(userMessage)
            }
            val rawJson = response.text
                ?: throw IllegalStateException("AI Teacher returned an empty response.")
            return parseTeacherReplyJson(rawJson)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI Teacher request timed out", e)
            throw e
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI Teacher generation failed", e)
            throw e
        }
    }

    companion object {
        internal fun parseTeacherReplyJson(rawJson: String): TeacherReplyPayload {
            val json = JSONObject(rawJson)
            val english = json.optString("englishSpeech", "").trim()
            if (english.isEmpty()) {
                throw IllegalStateException("AI Teacher response missing englishSpeech.")
            }
            val marathi = json.optString("marathiSupport", "").trim()
            val practice = if (json.isNull("practicePhrase")) {
                null
            } else {
                json.optString("practicePhrase", "").trim().ifEmpty { null }
            }
            val repliesArray = json.optJSONArray("quickReplies")
            val replies = mutableListOf<String>()
            if (repliesArray != null) {
                for (i in 0 until repliesArray.length()) {
                    val item = repliesArray.optString(i, "").trim()
                    if (item.isNotEmpty()) {
                        replies.add(item)
                    }
                }
            }
            return TeacherReplyPayload(
                englishSpeech = english,
                marathiSupport = marathi,
                practicePhrase = practice,
                quickReplies = replies.take(3)
            )
        }
    }
}
