package de.mindtrack.app.data

import kotlinx.coroutines.flow.Flow

class TrackingRepository(
    private val trackingDao: TrackingEventDao,
    private val skillDao: SkillDao,
    private val skillUsageDao: SkillUsageDao,
    private val discoveryDao: DiscoveryDao,
    private val contextTagDao: ContextTagDao
) {
    val events: Flow<List<TrackingEventEntity>> = trackingDao.observeAll()
    val skills: Flow<List<SkillEntity>> = skillDao.observeAll()
    val skillUsage: Flow<List<SkillUsageEntity>> = skillUsageDao.observeAll()
    val discoveries: Flow<List<DiscoveryEntity>> = discoveryDao.observeAll()
    val contextTags: Flow<List<ContextTagEntity>> = contextTagDao.observeAll()

    suspend fun initializeDefaults() {
        skillDao.insertAll(SkillSeed.skills)
        contextTagDao.insertAll(SkillSeed.contextTags)
    }

    suspend fun recordLevel(dimension: TrackingDimension, level: Int): Long {
        require(level in 1..5)
        return trackingDao.insert(
            TrackingEventEntity(
                dimension = dimension,
                type = TrackingEventType.LEVEL,
                level = level
            )
        )
    }

    suspend fun recordBreakdown(dimension: TrackingDimension): Long =
        trackingDao.insert(
            TrackingEventEntity(
                dimension = dimension,
                type = TrackingEventType.BREAKDOWN,
                level = null
            )
        )

    suspend fun recordChange(dimension: TrackingDimension, increase: Boolean): Long =
        trackingDao.insert(
            TrackingEventEntity(
                dimension = dimension,
                type = if (increase) TrackingEventType.INCREASE else TrackingEventType.DECREASE
            )
        )

    suspend fun attachContext(eventId: Long, context: String?) {
        trackingDao.updateContext(eventId, context?.takeIf { it.isNotBlank() })
    }

    suspend fun setSkillTried(skill: SkillEntity, value: Boolean) = skillDao.setTried(skill.id, value)
    suspend fun setSkillHelpful(skill: SkillEntity, value: Boolean) = skillDao.setHelpful(skill.id, value)
    suspend fun setSkillInToolbox(skill: SkillEntity, value: Boolean) = skillDao.setInToolbox(skill.id, value)

    suspend fun setSkillTensionLevels(
        skill: SkillEntity,
        low: Boolean = skill.tensionLow,
        medium: Boolean = skill.tensionMedium,
        high: Boolean = skill.tensionHigh,
        breakdown: Boolean = skill.tensionBreakdown
    ) = skillDao.setTensionLevels(skill.id, low, medium, high, breakdown)

    suspend fun recordSkillUse(skill: SkillEntity): Long = skillUsageDao.insert(
        SkillUsageEntity(skillId = skill.id, skillName = skill.name)
    )

    suspend fun addDiscovery(
        text: String,
        affectsEnergy: Boolean,
        affectsTension: Boolean,
        energyDirection: Int?,
        tensionDirection: Int?,
        contextTag: String?,
        addContextTag: Boolean
    ): Long {
        val normalizedTag = contextTag?.trim()?.takeIf { it.isNotBlank() }
        val id = discoveryDao.insert(
            DiscoveryEntity(
                text = text.trim(),
                affectsEnergy = affectsEnergy,
                affectsTension = affectsTension,
                energyDirection = energyDirection,
                tensionDirection = tensionDirection,
                contextTag = normalizedTag
            )
        )
        if (addContextTag && normalizedTag != null) {
            contextTagDao.insert(ContextTagEntity(normalizedTag, source = "discovery"))
        }
        return id
    }
}
