package de.mindtrack.app.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.mindtrack.app.data.DiscoveryEntity
import de.mindtrack.app.data.SkillEntity
import de.mindtrack.app.data.TrackingDimension
import de.mindtrack.app.data.TrackingEventEntity
import de.mindtrack.app.data.TrackingEventType
import de.mindtrack.app.quickcapture.QuickCaptureActivity
import de.mindtrack.app.quicksettings.QuickSettingsTileHelper
import de.mindtrack.app.ui.components.BatteryIndicator
import de.mindtrack.app.ui.components.TensionBars
import de.mindtrack.app.ui.theme.TrackerColors
import de.mindtrack.app.widget.MindTrackWidgetHelper
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class MainTab { TODAY, SKILLS, DISCOVERIES, TIMELINE }
private enum class SkillTab { TOOLBOX, LIBRARY }
private enum class TensionFilter { ALL, LOW, MEDIUM, HIGH, BREAKDOWN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindTrackScreen(viewModel: TrackerViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(MainTab.TODAY) }
    var contextEventId by remember { mutableStateOf<Long?>(null) }
    var contextLabel by remember { mutableStateOf("") }
    var tileMessage by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 0.dp
                ) {
                NavigationBarItem(
                    selected = tab == MainTab.TODAY,
                    onClick = { tab = MainTab.TODAY },
                    icon = { Icon(Icons.Outlined.Today, contentDescription = null) },
                    label = { Text("Heute") }
                )
                NavigationBarItem(
                    selected = tab == MainTab.SKILLS,
                    onClick = { tab = MainTab.SKILLS },
                    icon = { Icon(Icons.Outlined.Build, contentDescription = null) },
                    label = { Text("Skills") }
                )
                NavigationBarItem(
                    selected = tab == MainTab.DISCOVERIES,
                    onClick = { tab = MainTab.DISCOVERIES },
                    icon = { Icon(Icons.Outlined.Lightbulb, contentDescription = null) },
                    label = { Text("Entdecken") }
                )
                NavigationBarItem(
                    selected = tab == MainTab.TIMELINE,
                    onClick = { tab = MainTab.TIMELINE },
                    icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                    label = { Text("Verlauf") }
                )
                }
            }
        }
    ) { padding ->
        when (tab) {
            MainTab.TODAY -> TodayScreen(
                state = state,
                contentPadding = padding,
                onLevel = { dimension, level ->
                    viewModel.recordLevel(dimension, level) { id ->
                        contextEventId = id
                        contextLabel = "${dimension.germanName()} $level"
                    }
                },
                onBreakdown = { dimension ->
                    viewModel.recordBreakdown(dimension) { id ->
                        contextEventId = id
                        contextLabel = "${dimension.germanName()} · Breakdown"
                    }
                },
                onChange = { dimension, increase ->
                    viewModel.recordChange(dimension, increase) { id ->
                        contextEventId = id
                        contextLabel = "${dimension.germanName()} ${if (increase) "+" else "−"}"
                    }
                },
                onUseSkill = viewModel::recordSkillUse,
                tileMessage = tileMessage,
                onOpenQuickCapture = {
                    context.startActivity(Intent(context, QuickCaptureActivity::class.java))
                },
                onAddQuickTile = {
                    val activity = context as? Activity
                    if (activity != null) {
                        QuickSettingsTileHelper.requestAddTile(activity) { tileMessage = it }
                    } else {
                        tileMessage = "Füge „MindTrack Check-in“ über Bearbeiten in den Schnelleinstellungen hinzu."
                    }
                },
                onAddWidget = {
                    val activity = context as? Activity
                    tileMessage = if (activity != null) {
                        MindTrackWidgetHelper.requestPin(activity)
                    } else {
                        "Öffne die Widget-Auswahl deines Launchers und füge MindTrack hinzu."
                    }
                }
            )

            MainTab.SKILLS -> SkillsScreen(
                state = state,
                contentPadding = padding,
                onTried = viewModel::setSkillTried,
                onHelpful = viewModel::setSkillHelpful,
                onToolbox = viewModel::setSkillInToolbox,
                onToggleLevel = viewModel::toggleSkillLevel,
                onUseSkill = viewModel::recordSkillUse
            )

            MainTab.DISCOVERIES -> DiscoveriesScreen(
                discoveries = state.discoveries,
                contentPadding = padding,
                onSave = viewModel::addDiscovery
            )

            MainTab.TIMELINE -> TimelineScreen(
                state = state,
                contentPadding = padding
            )
        }
    }

    contextEventId?.let { eventId ->
        ContextSheet(
            label = contextLabel,
            tags = state.contextTags.map { it.name },
            onDismiss = { contextEventId = null },
            onSave = { value ->
                viewModel.attachContext(eventId, value)
                contextEventId = null
            }
        )
    }
}

@Composable
private fun TodayScreen(
    state: TrackerUiState,
    contentPadding: PaddingValues,
    onLevel: (TrackingDimension, Int) -> Unit,
    onBreakdown: (TrackingDimension) -> Unit,
    onChange: (TrackingDimension, Boolean) -> Unit,
    onUseSkill: (SkillEntity) -> Unit,
    tileMessage: String?,
    onOpenQuickCapture: () -> Unit,
    onAddQuickTile: () -> Unit,
    onAddWidget: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding.withScreenPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Wie ist es gerade?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "Ein Tap speichert sofort. Kontext kannst du direkt danach ergänzen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            LevelCard(
                dimension = TrackingDimension.ENERGY,
                currentLevel = state.energyLevel,
                breakdown = state.energyBreakdown,
                onLevel = { onLevel(TrackingDimension.ENERGY, it) },
                onBreakdown = { onBreakdown(TrackingDimension.ENERGY) }
            )
        }

        item {
            LevelCard(
                dimension = TrackingDimension.TENSION,
                currentLevel = state.tensionLevel,
                breakdown = state.tensionBreakdown,
                onLevel = { onLevel(TrackingDimension.TENSION, it) },
                onBreakdown = { onBreakdown(TrackingDimension.TENSION) }
            )
        }

        item {
            Text("Veränderung", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            ChangeGrid(onChange = onChange)
        }

        item {
            val suggestions = state.skills
                .filter { it.inToolbox && it.matchesCurrentTension(state) }
                .sortedWith(compareByDescending<SkillEntity> { it.helpful }.thenByDescending { it.recommended }.thenBy { it.sortOrder })
                .take(4)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Passende Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Aus deiner Box passend zum aktuell gespeicherten Anspannungsbereich.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (suggestions.isEmpty()) {
                        Text("Noch keine passenden Skills in deiner Box.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        suggestions.forEach { skill ->
                            OutlinedButton(onClick = { onUseSkill(skill) }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Outlined.Bolt, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(skill.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Android Schnellzugriff", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Das Widget ist der primäre Schnellzugriff; die Quick-Settings-Kachel bleibt als Zusatzweg.")
                    Button(onClick = onAddWidget, modifier = Modifier.fillMaxWidth()) { Text("Widget hinzufügen") }
                    OutlinedButton(onClick = onOpenQuickCapture, modifier = Modifier.fillMaxWidth()) { Text("Quick Capture testen") }
                    OutlinedButton(onClick = onAddQuickTile, modifier = Modifier.fillMaxWidth()) { Text("Quick-Settings-Kachel hinzufügen") }
                    tileMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    dimension: TrackingDimension,
    currentLevel: Int?,
    breakdown: Boolean,
    onLevel: (Int) -> Unit,
    onBreakdown: () -> Unit
) {
    val currentColor = when {
        breakdown -> TrackerColors.BreakdownAccent
        currentLevel == null -> MaterialTheme.colorScheme.onSurfaceVariant
        dimension == TrackingDimension.ENERGY -> TrackerColors.energy(currentLevel)
        else -> TrackerColors.tension(currentLevel)
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (dimension == TrackingDimension.ENERGY) Icons.Outlined.BatteryChargingFull else Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = currentColor
                    )
                    Column {
                        Text(dimension.germanName(), fontWeight = FontWeight.SemiBold)
                        Text(
                            if (breakdown) "Breakdown" else currentLevel?.toString() ?: "Noch kein Wert",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (dimension == TrackingDimension.ENERGY) {
                    BatteryIndicator(currentLevel, currentColor)
                } else {
                    TensionBars(currentLevel, currentColor)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                LevelButton(
                    label = "B",
                    selected = breakdown,
                    background = TrackerColors.Breakdown,
                    border = TrackerColors.BreakdownAccent,
                    text = TrackerColors.BreakdownAccent,
                    onClick = onBreakdown,
                    modifier = Modifier.weight(1f)
                )
                (1..5).forEach { level ->
                    val color = if (dimension == TrackingDimension.ENERGY) TrackerColors.energy(level) else TrackerColors.tension(level)
                    LevelButton(
                        label = level.toString(),
                        selected = !breakdown && currentLevel == level,
                        background = color.copy(alpha = 0.18f),
                        border = color,
                        text = MaterialTheme.colorScheme.onSurface,
                        onClick = { onLevel(level) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelButton(
    label: String,
    selected: Boolean,
    background: Color,
    border: Color,
    text: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(if (selected) 3.dp else 1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(label, color = text, fontWeight = FontWeight.Bold) }
}

@Composable
private fun ChangeGrid(onChange: (TrackingDimension, Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChangeButton("Energie +", Icons.Outlined.ArrowUpward, TrackerColors.Green, { onChange(TrackingDimension.ENERGY, true) }, Modifier.weight(1f))
            ChangeButton("Energie −", Icons.Outlined.ArrowDownward, TrackerColors.Red, { onChange(TrackingDimension.ENERGY, false) }, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChangeButton("Anspannung +", Icons.Outlined.Add, TrackerColors.Red, { onChange(TrackingDimension.TENSION, true) }, Modifier.weight(1f))
            ChangeButton("Anspannung −", Icons.Outlined.Remove, TrackerColors.Green, { onChange(TrackingDimension.TENSION, false) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ChangeButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SkillsScreen(
    state: TrackerUiState,
    contentPadding: PaddingValues,
    onTried: (SkillEntity, Boolean) -> Unit,
    onHelpful: (SkillEntity, Boolean) -> Unit,
    onToolbox: (SkillEntity, Boolean) -> Unit,
    onToggleLevel: (SkillEntity, String) -> Unit,
    onUseSkill: (SkillEntity) -> Unit
) {
    var tab by remember { mutableStateOf(SkillTab.TOOLBOX) }
    var tensionFilter by remember { mutableStateOf(TensionFilter.ALL) }
    var category by remember { mutableStateOf("Alle") }
    var chain by remember { mutableStateOf<List<SkillEntity>>(emptyList()) }
    var chainIndex by remember { mutableIntStateOf(-1) }

    val toolbox = state.skills.filter { it.inToolbox }
    val filteredToolbox = toolbox.filter { it.matchesFilter(tensionFilter) }.sortedBy { it.sortOrder }
    val categories = listOf("Alle") + state.skills.map { it.category }.distinct()
    val library = state.skills.filter { category == "Alle" || it.category == category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding.withScreenPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Skills", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "Bibliothek zum Erkunden, persönliche Box für den schnellen Zugriff.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StrongChoiceChip("Meine Box", tab == SkillTab.TOOLBOX) { tab = SkillTab.TOOLBOX }
                StrongChoiceChip("Bibliothek", tab == SkillTab.LIBRARY) { tab = SkillTab.LIBRARY }
            }
        }

        if (tab == SkillTab.TOOLBOX) {
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TensionFilter.entries.forEach { filter ->
                        StrongChoiceChip(
                            label = filter.label(),
                            selected = tensionFilter == filter,
                            onClick = { tensionFilter = filter }
                        )
                    }
                }
            }

            if (filteredToolbox.isEmpty()) {
                item { Text("Für diesen Bereich sind noch keine Skills in deiner Box.") }
            } else {
                items(filteredToolbox, key = { it.id }) { skill ->
                    ToolboxCard(skill, onUse = { onUseSkill(skill) })
                }
                item {
                    Button(
                        onClick = {
                            chain = filteredToolbox
                            chainIndex = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Kette aus diesen Skills starten") }
                }
            }

            if (chainIndex >= 0 && chainIndex < chain.size) {
                item {
                    val current = chain[chainIndex]
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Skill-Kette ${chainIndex + 1}/${chain.size}", style = MaterialTheme.typography.labelLarge)
                            Text(current.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(current.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    onUseSkill(current)
                                    chainIndex = if (chainIndex + 1 < chain.size) chainIndex + 1 else -1
                                }) {
                                    Icon(Icons.Outlined.Check, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Gemacht")
                                }
                                TextButton(onClick = {
                                    chainIndex = if (chainIndex + 1 < chain.size) chainIndex + 1 else -1
                                }) { Text("Überspringen") }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { item ->
                        StrongChoiceChip(item, category == item) { category = item }
                    }
                }
            }

            val grouped = if (category == "Alle") {
                library.groupBy { it.category }.toList()
            } else {
                listOf(category to library)
            }
            grouped.forEach { (groupName, groupSkills) ->
                item {
                    Text(
                        groupName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(groupSkills, key = { it.id }) { skill ->
                    SkillLibraryCard(
                        skill = skill,
                        onTried = { onTried(skill, !skill.tried) },
                        onHelpful = { onHelpful(skill, !skill.helpful) },
                        onToolbox = { onToolbox(skill, !skill.inToolbox) },
                        onToggleLevel = { onToggleLevel(skill, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolboxCard(skill: SkillEntity, onUse: () -> Unit) {
    Card {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(skill.name, fontWeight = FontWeight.SemiBold)
                    Text(skill.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (skill.helpful) Text("✓ Hilft", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
            }
            Text(skill.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                skill.levelLabels().forEach { label -> TinyPill(label) }
            }
            OutlinedButton(onClick = onUse, modifier = Modifier.fillMaxWidth()) { Text("Jetzt nutzen") }
        }
    }
}

@Composable
private fun SkillLibraryCard(
    skill: SkillEntity,
    onTried: () -> Unit,
    onHelpful: () -> Unit,
    onToolbox: () -> Unit,
    onToggleLevel: (String) -> Unit
) {
    Card {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(skill.name, fontWeight = FontWeight.SemiBold)
                        if (skill.recommended) TinyPill("★ Startempfehlung")
                    }
                    Text(skill.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(skill.evidenceLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text(skill.description, style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StrongChoiceChip("Ausprobiert", skill.tried, onTried)
                StrongChoiceChip("Hilft", skill.helpful, onHelpful)
            }
            Text("Passend bei Anspannung", style = MaterialTheme.typography.labelMedium)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StrongChoiceChip("Niedrig", skill.tensionLow) { onToggleLevel("low") }
                StrongChoiceChip("Mittel", skill.tensionMedium) { onToggleLevel("medium") }
                StrongChoiceChip("Hoch", skill.tensionHigh) { onToggleLevel("high") }
                StrongChoiceChip("B", skill.tensionBreakdown) { onToggleLevel("breakdown") }
            }
            Button(onClick = onToolbox, modifier = Modifier.fillMaxWidth()) {
                Text(if (skill.inToolbox) "Aus meiner Box entfernen" else "+ In meine Box")
            }
        }
    }
}

@Composable
private fun StrongChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.55f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selected) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(17.dp)
            )
        }
        Text(
            label,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun TinyPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) { Text(text, style = MaterialTheme.typography.labelSmall) }
}

@Composable
private fun DiscoveriesScreen(
    discoveries: List<DiscoveryEntity>,
    contentPadding: PaddingValues,
    onSave: (String, Boolean, Boolean, Int?, Int?, String?, Boolean, () -> Unit) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var affectsEnergy by remember { mutableStateOf(false) }
    var affectsTension by remember { mutableStateOf(true) }
    var energyDirection by remember { mutableStateOf<Int?>(null) }
    var tensionDirection by remember { mutableStateOf<Int?>(-1) }
    var tag by remember { mutableStateOf("") }
    var addTag by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding.withScreenPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Entdeckungen", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text("Halte Beobachtungen fest und übernimm hilfreiche Begriffe direkt in die Kontextauswahl.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Was hast du bemerkt?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Text("Betrifft", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        FilterChip(selected = affectsEnergy, onClick = { affectsEnergy = !affectsEnergy }, label = { Text("🔋 Energie") })
                        FilterChip(selected = affectsTension, onClick = { affectsTension = !affectsTension }, label = { Text("🔥 Anspannung") })
                    }
                    if (affectsEnergy) {
                        DirectionRow("Energie", energyDirection) { energyDirection = it }
                    }
                    if (affectsTension) {
                        DirectionRow("Anspannung", tensionDirection) { tensionDirection = it }
                    }
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it },
                        label = { Text("Kontext-Tag, z. B. Einkaufen") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = addTag, onCheckedChange = { addTag = it })
                        Text("Als Schnell-Kontext übernehmen")
                    }
                    Button(
                        enabled = text.isNotBlank(),
                        onClick = {
                            onSave(text, affectsEnergy, affectsTension, energyDirection, tensionDirection, tag, addTag) {
                                text = ""
                                tag = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Entdeckung speichern") }
                }
            }
        }

        item { Text("Gespeichert", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        if (discoveries.isEmpty()) {
            item { Text("Noch keine Entdeckungen.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(discoveries, key = { it.id }) { discovery -> DiscoveryCard(discovery) }
        }
    }
}

@Composable
private fun DirectionRow(label: String, selected: Int?, onChange: (Int?) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(88.dp))
        FilterChip(selected = selected == 1, onClick = { onChange(if (selected == 1) null else 1) }, label = { Text("+") })
        FilterChip(selected = selected == -1, onClick = { onChange(if (selected == -1) null else -1) }, label = { Text("−") })
        FilterChip(selected = selected == null, onClick = { onChange(null) }, label = { Text("offen") })
    }
}

@Composable
private fun DiscoveryCard(discovery: DiscoveryEntity) {
    Card {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(discovery.text)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (discovery.affectsEnergy) TinyPill("🔋 ${directionSymbol(discovery.energyDirection)}")
                if (discovery.affectsTension) TinyPill("🔥 ${directionSymbol(discovery.tensionDirection)}")
                discovery.contextTag?.let { TinyPill("#$it") }
            }
        }
    }
}

@Composable
private fun TimelineScreen(state: TrackerUiState, contentPadding: PaddingValues) {
    data class RowItem(val timestamp: Long, val title: String, val subtitle: String?)
    val rows = buildList {
        state.events.forEach { event -> add(RowItem(event.timestamp, event.title(), event.context)) }
        state.skillUsage.forEach { usage -> add(RowItem(usage.timestamp, "Skill: ${usage.skillName}", null)) }
    }.sortedByDescending { it.timestamp }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding.withScreenPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Verlauf", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text("Zustände, Veränderungen und verwendete Skills in einer gemeinsamen Zeitlinie.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (rows.isEmpty()) {
            item { Text("Noch keine Einträge.") }
        } else {
            items(rows) { row ->
                Card {
                    Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(row.title, fontWeight = FontWeight.SemiBold)
                            row.subtitle?.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(formatTime(row.timestamp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContextSheet(
    label: String,
    tags: List<String>,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit
) {
    var selected by remember { mutableStateOf(setOf<String>()) }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Gespeichert: $label", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Kontext ist optional.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                tags.forEach { tag ->
                    AssistChip(
                        onClick = { selected = if (tag in selected) selected - tag else selected + tag },
                        label = { Text(tag) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (tag in selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    )
                }
            }
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Kurze Notiz") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val value = (selected.toList() + note.trim().takeIf { it.isNotBlank() }).filterNotNull().joinToString(" · ")
                    onSave(value.takeIf { it.isNotBlank() })
                }) { Text("Speichern") }
                TextButton(onClick = { onSave(null) }) { Text("Überspringen") }
            }
        }
    }
}

private fun PaddingValues.withScreenPadding() = PaddingValues(
    start = 16.dp,
    end = 16.dp,
    top = calculateTopPadding() + 18.dp,
    bottom = calculateBottomPadding() + 20.dp
)

private fun TrackingDimension.germanName() = if (this == TrackingDimension.ENERGY) "Energie" else "Anspannung"

private fun TrackingEventEntity.title(): String = when (type) {
    TrackingEventType.LEVEL -> "${dimension.germanName()} ${level ?: "?"}"
    TrackingEventType.INCREASE -> "${dimension.germanName()} +"
    TrackingEventType.DECREASE -> "${dimension.germanName()} −"
    TrackingEventType.BREAKDOWN -> "${dimension.germanName()} · Breakdown"
}

private fun SkillEntity.matchesCurrentTension(state: TrackerUiState): Boolean = when {
    state.tensionBreakdown -> tensionBreakdown
    (state.tensionLevel ?: 0) >= 4 -> tensionHigh
    (state.tensionLevel ?: 0) >= 2 -> tensionMedium
    else -> tensionLow
}

private fun SkillEntity.matchesFilter(filter: TensionFilter): Boolean = when (filter) {
    TensionFilter.ALL -> true
    TensionFilter.LOW -> tensionLow
    TensionFilter.MEDIUM -> tensionMedium
    TensionFilter.HIGH -> tensionHigh
    TensionFilter.BREAKDOWN -> tensionBreakdown
}

private fun TensionFilter.label() = when (this) {
    TensionFilter.ALL -> "Alle"
    TensionFilter.LOW -> "Niedrig"
    TensionFilter.MEDIUM -> "Mittel"
    TensionFilter.HIGH -> "Hoch"
    TensionFilter.BREAKDOWN -> "B"
}

private fun SkillEntity.levelLabels(): List<String> = buildList {
    if (tensionLow) add("Niedrig")
    if (tensionMedium) add("Mittel")
    if (tensionHigh) add("Hoch")
    if (tensionBreakdown) add("B")
}

private fun directionSymbol(value: Int?): String = when (value) {
    1 -> "+"
    -1 -> "−"
    else -> "±"
}

private fun formatTime(timestamp: Long): String = DateTimeFormatter.ofPattern("HH:mm")
    .withZone(ZoneId.systemDefault())
    .format(Instant.ofEpochMilli(timestamp))
