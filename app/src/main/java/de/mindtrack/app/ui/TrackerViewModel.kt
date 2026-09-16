package de.mindtrack.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.mindtrack.app.data.ContextTagEntity
import de.mindtrack.app.data.DiscoveryEntity
import de.mindtrack.app.data.SkillEntity
import de.mindtrack.app.data.SkillUsageEntity
import de.mindtrack.app.data.TrackingDimension
import de.mindtrack.app.data.TrackingEventEntity
import de.mindtrack.app.data.TrackingEventType
import de.mindtrack.app.data.TrackingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrackerUiState(
    val events: List<TrackingEventEntity> = emptyList(),
    val skills: List<SkillEntity> = emptyList(),
    val skillUsage: List<SkillUsageEntity> = emptyList(),
    val discoveries: List<DiscoveryEntity> = emptyList(),
    val contextTags: List<ContextTagEntity> = emptyList(),
    val energyLevel: Int? = null,
    val tensionLevel: Int? = null,
    val energyBreakdown: Boolean = false,
    val tensionBreakdown: Boolean = false
)

class TrackerViewModel(
    private val repository: TrackingRepository
) : ViewModel() {

    val uiState: StateFlow<TrackerUiState> = combine(
        repository.events,
        repository.skills,
        repository.skillUsage,
        repository.discoveries,
        repository.contextTags
    ) { events, skills, usages, discoveries, contextTags ->
        val energy = events.firstOrNull {
            it.dimension == TrackingDimension.ENERGY &&
                (it.type == TrackingEventType.LEVEL || it.type == TrackingEventType.BREAKDOWN)
        }
        val tension = events.firstOrNull {
            it.dimension == TrackingDimension.TENSION &&
                (it.type == TrackingEventType.LEVEL || it.type == TrackingEventType.BREAKDOWN)
        }
        TrackerUiState(
            events = events,
            skills = skills,
            skillUsage = usages,
            discoveries = discoveries,
            contextTags = contextTags,
            energyLevel = energy?.level,
            tensionLevel = tension?.level,
            energyBreakdown = energy?.type == TrackingEventType.BREAKDOWN,
            tensionBreakdown = tension?.type == TrackingEventType.BREAKDOWN
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TrackerUiState()
    )

    fun recordLevel(dimension: TrackingDimension, level: Int, onRecorded: (Long) -> Unit) {
        viewModelScope.launch { onRecorded(repository.recordLevel(dimension, level)) }
    }

    fun recordBreakdown(dimension: TrackingDimension, onRecorded: (Long) -> Unit) {
        viewModelScope.launch { onRecorded(repository.recordBreakdown(dimension)) }
    }

    fun recordChange(dimension: TrackingDimension, increase: Boolean, onRecorded: (Long) -> Unit) {
        viewModelScope.launch { onRecorded(repository.recordChange(dimension, increase)) }
    }

    fun attachContext(eventId: Long, context: String?) {
        viewModelScope.launch { repository.attachContext(eventId, context) }
    }

    fun setSkillTried(skill: SkillEntity, value: Boolean) {
        viewModelScope.launch { repository.setSkillTried(skill, value) }
    }

    fun setSkillHelpful(skill: SkillEntity, value: Boolean) {
        viewModelScope.launch { repository.setSkillHelpful(skill, value) }
    }

    fun setSkillInToolbox(skill: SkillEntity, value: Boolean) {
        viewModelScope.launch { repository.setSkillInToolbox(skill, value) }
    }

    fun toggleSkillLevel(skill: SkillEntity, level: String) {
        viewModelScope.launch {
            when (level) {
                "low" -> repository.setSkillTensionLevels(skill, low = !skill.tensionLow)
                "medium" -> repository.setSkillTensionLevels(skill, medium = !skill.tensionMedium)
                "high" -> repository.setSkillTensionLevels(skill, high = !skill.tensionHigh)
                "breakdown" -> repository.setSkillTensionLevels(skill, breakdown = !skill.tensionBreakdown)
            }
        }
    }

    fun recordSkillUse(skill: SkillEntity) {
        viewModelScope.launch { repository.recordSkillUse(skill) }
    }

    fun addDiscovery(
        text: String,
        affectsEnergy: Boolean,
        affectsTension: Boolean,
        energyDirection: Int?,
        tensionDirection: Int?,
        contextTag: String?,
        addContextTag: Boolean,
        onSaved: () -> Unit = {}
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addDiscovery(
                text = text,
                affectsEnergy = affectsEnergy,
                affectsTension = affectsTension,
                energyDirection = energyDirection,
                tensionDirection = tensionDirection,
                contextTag = contextTag,
                addContextTag = addContextTag
            )
            onSaved()
        }
    }

    companion object {
        fun factory(repository: TrackingRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TrackerViewModel(repository) as T
            }
    }
}
