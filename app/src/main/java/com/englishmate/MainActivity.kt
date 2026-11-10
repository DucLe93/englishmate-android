package com.englishmate

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

private data class Word(val english: String, val vietnamese: String, val example: String)
private val words = listOf(
    Word("Achieve", "Đạt được", "You can achieve your goals with practice."),
    Word("Improve", "Cải thiện", "I want to improve my English."),
    Word("Confident", "Tự tin", "She feels confident when speaking."),
    Word("Opportunity", "Cơ hội", "This is a great opportunity to learn."),
    Word("Habit", "Thói quen", "Reading every day is a good habit.")
)

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
        setContent { EnglishMateApp(onSpeak = { speak(it) }) }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "englishmate-utterance")
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
private fun EnglishMateApp(onSpeak: (String) -> Unit) {
    var tab by remember { mutableStateOf(0) }
    var cardIndex by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    var quizIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<String?>(null) }
    var answered by remember { mutableStateOf(false) }
    val word = words[cardIndex % words.size]
    val quizWord = words[quizIndex % words.size]
    val choices = remember(quizIndex) {
        listOf(quizWord.vietnamese) + words.filter { it != quizWord }.shuffled().take(3).map { it.vietnamese }
    }.shuffled()

    MaterialTheme(colorScheme = lightColorScheme(primary = MaterialTheme.colorScheme.primary)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text("EnglishMate", style = MaterialTheme.typography.titleLarge) })
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Text("📚") }, label = { Text("Từ vựng") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Text("🧠") }, label = { Text("Trắc nghiệm") })
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(if (tab == 0) "Học từ vựng mỗi ngày" else "Kiểm tra kiến thức", style = MaterialTheme.typography.headlineSmall)
                if (tab == 0) {
                    Text("Thẻ ${cardIndex + 1} / ${words.size}", color = MaterialTheme.colorScheme.secondary)
                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(word.english, style = MaterialTheme.typography.headlineLarge)
                            IconButton(onClick = { onSpeak(word.english) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Nghe phát âm")
                            }
                            Spacer(Modifier.height(16.dp))
                            if (showAnswer) {
                                Text(word.vietnamese, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                                Text(word.example, style = MaterialTheme.typography.bodyMedium)
                            } else {
                                Text("Hãy thử đoán nghĩa tiếng Việt", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Button(onClick = { showAnswer = !showAnswer }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (showAnswer) "Ẩn nghĩa" else "Hiện nghĩa")
                    }
                    Button(
                        onClick = { cardIndex = (cardIndex + 1) % words.size; showAnswer = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Từ tiếp theo") }
                } else {
                    Text("Điểm: $score", style = MaterialTheme.typography.titleMedium)
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Từ tiếng Anh này có nghĩa là gì?", style = MaterialTheme.typography.titleMedium)
                            Text(quizWord.english, style = MaterialTheme.typography.headlineMedium)
                            IconButton(onClick = { onSpeak(quizWord.english) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Nghe phát âm")
                            }
                        }
                    }
                    choices.forEach { choice ->
                        OutlinedButton(
                            onClick = { if (!answered) { selected = choice; answered = true; if (choice == quizWord.vietnamese) score++ } },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !answered
                        ) { Text(choice) }
                    }
                    if (answered) {
                        Text(if (selected == quizWord.vietnamese) "Chính xác! 🎉" else "Đáp án: ${quizWord.vietnamese}")
                    }
                    Button(
                        onClick = { quizIndex = (quizIndex + 1) % words.size; selected = null; answered = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Câu tiếp theo") }
                }
            }
        }
    }
}
