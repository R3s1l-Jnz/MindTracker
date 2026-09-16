package de.mindtrack.app.quickcapture

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.mindtrack.app.MindTrackApplication
import de.mindtrack.app.data.TrackingDimension
import de.mindtrack.app.data.TrackingRepository
import de.mindtrack.app.ui.theme.MindTrackTheme
import de.mindtrack.app.ui.theme.TrackerColors
import kotlinx.coroutines.launch

private val FallbackQuickContextTags = listOf("Arbeit", "Gespräch", "Hunger", "Reizüberflutung", "Bewegung", "Skill")

class QuickCaptureActivity : ComponentActivity() {
    companion object {
        const val EXTRA_DIMENSION = "quick_dimension"
        const val EXTRA_EVENT_TYPE = "quick_event_type"
        const val EXTRA_LEVEL = "quick_level"
        const val EVENT_LEVEL = "level"
        const val EVENT_BREAKDOWN = "breakdown"
        const val EVENT_INCREASE = "increase"
        const val EVENT_DECREASE = "decrease"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
        enableEdgeToEdge()

        val repository = (application as MindTrackApplication).repository
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val allowPersonalTags = !keyguardManager.isDeviceLocked
        val initialPreset = QuickCapturePreset.fromIntent(intent)

        setContent {
            MindTrackTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuickCaptureScreen(
                        repository = repository,
                        allowPersonalTags = allowPersonalTags,
                        initialPreset = initialPreset,
                        onClose = { finish() }
                    )
                }
            }
        }
    }
}

private data class QuickCapturePreset(
    val dimension: TrackingDimension,
    val eventType: String,
    val level: Int? = null
) {
    companion object {
        fun fromIntent(intent: android.content.Intent): QuickCapturePreset? {
            val dimension = when (intent.getStringExtra(QuickCaptureActivity.EXTRA_DIMENSION)) {
                TrackingDimension.ENERGY.name -> TrackingDimension.ENERGY
                TrackingDimension.TENSION.name -> TrackingDimension.TENSION
                else -> return null
            }
            val eventType = intent.getStringExtra(QuickCaptureActivity.EXTRA_EVENT_TYPE) ?: return null
            val level = intent.getIntExtra(QuickCaptureActivity.EXTRA_LEVEL, -1).takeIf { it in 1..5 }
            return QuickCapturePreset(dimension, eventType, level)
        }
    }
}

@Composable
private fun QuickCaptureScreen(
    repository: TrackingRepository,
    allowPersonalTags: Boolean,
    initialPreset: QuickCapturePreset?,
    onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var savedEventId by remember { mutableStateOf<Long?>(null) }
    var savedLabel by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var note by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var presetHandled by remember { mutableStateOf(false) }
    val storedTags by repository.contextTags.collectAsState(initial = emptyList())
    val availableTags = if (allowPersonalTags) storedTags.map { it.name }.ifEmpty { FallbackQuickContextTags } else FallbackQuickContextTags

    fun afterSaved(id: Long, label: String) {
        savedEventId = id
        savedLabel = label
        selectedTags = emptySet()
        note = ""
        saving = false
    }

    fun recordLevel(dimension: TrackingDimension, level: Int) {
        if (saving) return
        saving = true
        scope.launch {
            val id = repository.recordLevel(dimension, level)
            afterSaved(id, "${dimension.label()} $level")
        }
    }

    fun recordBreakdown(dimension: TrackingDimension) {
        if (saving) return
        saving = true
        scope.launch {
            val id = repository.recordBreakdown(dimension)
            afterSaved(id, "${dimension.label()} · Breakdown")
        }
    }

    fun recordChange(dimension: TrackingDimension, increase: Boolean) {
        if (saving) return
        saving = true
        scope.launch {
            val id = repository.recordChange(dimension, increase)
            afterSaved(id, "${dimension.label()} ${if (increase) "+" else "−"}")
        }
    }

    LaunchedEffect(initialPreset) {
        val preset = initialPreset
        if (preset != null && !presetHandled) {
            presetHandled = true
            saving = true
            when (preset.eventType) {
                QuickCaptureActivity.EVENT_LEVEL -> preset.level?.let { level ->
                    val id = repository.recordLevel(preset.dimension, level)
                    afterSaved(id, "${preset.dimension.label()} $level")
                }
                QuickCaptureActivity.EVENT_BREAKDOWN -> {
                    val id = repository.recordBreakdown(preset.dimension)
                    afterSaved(id, "${preset.dimension.label()} · Breakdown")
                }
                QuickCaptureActivity.EVENT_INCREASE -> {
                    val id = repository.recordChange(preset.dimension, true)
                    afterSaved(id, "${preset.dimension.label()} +")
                }
                QuickCaptureActivity.EVENT_DECREASE -> {
                    val id = repository.recordChange(preset.dimension, false)
                    afterSaved(id, "${preset.dimension.label()} −")
                }
                else -> saving = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Quick Capture", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    if (savedLabel == null) "Ein Tap speichert sofort." else "✓ $savedLabel gespeichert",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Outlined.Close, contentDescription = "Schließen")
            }
        }

        CompactCaptureCard(
            dimension = TrackingDimension.ENERGY,
            onLevel = { recordLevel(TrackingDimension.ENERGY, it) },
            onBreakdown = { recordBreakdown(TrackingDimension.ENERGY) },
            onIncrease = { recordChange(TrackingDimension.ENERGY, true) },
            onDecrease = { recordChange(TrackingDimension.ENERGY, false) }
        )

        CompactCaptureCard(
            dimension = TrackingDimension.TENSION,
            onLevel = { recordLevel(TrackingDimension.TENSION, it) },
            onBreakdown = { recordBreakdown(TrackingDimension.TENSION) },
            onIncrease = { recordChange(TrackingDimension.TENSION, true) },
            onDecrease = { recordChange(TrackingDimension.TENSION, false) }
        )

        savedEventId?.let { eventId ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Kontext?", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Optional – der Eintrag ist bereits gespeichert.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableTags.forEach { tag ->
                            QuickContextChip(
                                tag = tag,
                                selected = tag in selectedTags,
                                onClick = {
                                    selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
                                }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kurze Notiz") },
                        minLines = 1,
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onClose) { Text("Ohne Kontext fertig") }
                        Spacer(Modifier.width(6.dp))
                        Button(
                            onClick = {
                                val context = (selectedTags.toList() + note.trim().takeIf { it.isNotEmpty() })
                                    .filterNotNull()
                                    .joinToString(" · ")
                                    .ifBlank { null }
                                scope.launch {
                                    repository.attachContext(eventId, context)
                                    onClose()
                                }
                            }
                        ) {
                            Text("Speichern")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCaptureCard(
    dimension: TrackingDimension,
    onLevel: (Int) -> Unit,
    onBreakdown: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (dimension == TrackingDimension.ENERGY) {
                            Icons.Outlined.BatteryChargingFull
                        } else {
                            Icons.Outlined.LocalFireDepartment
                        },
                        contentDescription = null
                    )
                    Text(dimension.label(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }

                Text(
                    "B · 1–5",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                QuickLevelButton(
                    label = "B",
                    background = TrackerColors.Breakdown,
                    border = TrackerColors.BreakdownAccent,
                    textColor = TrackerColors.BreakdownAccent,
                    onClick = onBreakdown,
                    modifier = Modifier.weight(1f)
                )
                (1..5).forEach { level ->
                    val color = if (dimension == TrackingDimension.ENERGY) {
                        TrackerColors.energy(level)
                    } else {
                        TrackerColors.tension(level)
                    }
                    QuickLevelButton(
                        label = level.toString(),
                        background = color.copy(alpha = 0.18f),
                        border = color,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        onClick = { onLevel(level) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickChangeButton(
                    label = if (dimension == TrackingDimension.ENERGY) "🔋 +" else "🔥 +",
                    accent = if (dimension == TrackingDimension.ENERGY) TrackerColors.Green else TrackerColors.Red,
                    onClick = onIncrease,
                    modifier = Modifier.weight(1f)
                )
                QuickChangeButton(
                    label = if (dimension == TrackingDimension.ENERGY) "🔋 −" else "🔥 −",
                    accent = if (dimension == TrackingDimension.ENERGY) TrackerColors.Red else TrackerColors.Green,
                    onClick = onDecrease,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickLevelButton(
    label: String,
    background: Color,
    border: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
private fun QuickChangeButton(
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuickContextChip(
    tag: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = { Text(tag, maxLines = 1) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
        )
    )
}

private fun TrackingDimension.label(): String = when (this) {
    TrackingDimension.ENERGY -> "Energie"
    TrackingDimension.TENSION -> "Anspannung"
}
