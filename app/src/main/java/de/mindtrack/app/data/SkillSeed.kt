package de.mindtrack.app.data

object SkillSeed {
    val contextTags = listOf(
        "Arbeit", "Gespräch", "Hunger", "Reizüberflutung", "Bewegung", "Skill", "Schlaf", "Konflikt"
    ).map { ContextTagEntity(it) }

    val skills = listOf(
        // DBT – Achtsamkeit
        skill("wise_mind", "Wise Mind", "DBT · Achtsamkeit", "Kurz innehalten und sowohl Gefühle als auch Fakten berücksichtigen, bevor du handelst.", low = true, medium = true, box = true, order = 10),
        skill("observe", "Beobachten", "DBT · Achtsamkeit", "Wahrnehmen, was gerade innen und außen passiert, ohne sofort darauf zu reagieren.", low = true, medium = true, order = 11),
        skill("describe", "Beschreiben", "DBT · Achtsamkeit", "Erleben möglichst konkret in Worte fassen, statt es sofort zu bewerten.", low = true, medium = true, order = 12),
        skill("participate", "Teilnehmen", "DBT · Achtsamkeit", "Die Aufmerksamkeit bewusst in die aktuelle Tätigkeit zurückbringen.", low = true, medium = true, order = 13),
        skill("nonjudgmental", "Nicht wertend", "DBT · Achtsamkeit", "Beobachtungen von Urteilen trennen und wertende Gedanken als Gedanken erkennen.", low = true, medium = true, order = 14),
        skill("one_mindful", "Eins nach dem anderen", "DBT · Achtsamkeit", "Für einen Moment nur eine Sache tun und Ablenkungen später wieder aufnehmen.", low = true, medium = true, order = 15),
        skill("effective", "Wirksam handeln", "DBT · Achtsamkeit", "Die Handlung wählen, die dem aktuellen Ziel dient – nicht nur dem ersten Impuls.", low = true, medium = true, order = 16),

        // DBT – Stresstoleranz
        skill("stop", "STOP", "DBT · Stresstoleranz", "Handlung kurz unterbrechen, Abstand schaffen, beobachten und den nächsten Schritt bewusst wählen.", recommended = true, medium = true, high = true, box = true, order = 20),
        skill("tip", "TIP / intensive Körperregulation", "DBT · Stresstoleranz", "Bei hoher Aktivierung mit kurzen körperbezogenen Regulationsreizen arbeiten; passend und sicher dosieren.", high = true, breakdown = true, box = true, order = 21),
        skill("paced_breathing", "Langsamer ausatmen", "DBT · Stresstoleranz", "Atmung verlangsamen und die Ausatmung etwas länger als die Einatmung werden lassen.", medium = true, high = true, box = true, order = 22),
        skill("paired_relaxation", "Anspannen & lockern", "DBT · Stresstoleranz", "Muskelgruppen kurz anspannen und anschließend bewusst wieder lösen.", medium = true, high = true, order = 23),
        skill("self_soothe", "Über die Sinne beruhigen", "DBT · Stresstoleranz", "Gezielt einen angenehmen und sicheren Sinnesreiz nutzen: Klang, Geruch, Berührung, Geschmack oder Bild.", medium = true, high = true, order = 24),
        skill("accepts", "Kurz ablenken (ACCEPTS)", "DBT · Stresstoleranz", "Für eine begrenzte Zeit Aufmerksamkeit weg vom akuten Stressor lenken, ohne das Problem dauerhaft zu vermeiden.", medium = true, high = true, order = 25),
        skill("improve", "Moment erleichtern (IMPROVE)", "DBT · Stresstoleranz", "Den aktuellen Moment mit einer kleinen hilfreichen Veränderung etwas erträglicher machen.", medium = true, high = true, order = 26),
        skill("pros_cons", "Pro & Contra", "DBT · Stresstoleranz", "Vor einer impulsiven Handlung kurz Folgen des Handelns und Nicht-Handelns gegenüberstellen.", medium = true, high = true, order = 27),
        skill("radical_acceptance", "Radikale Akzeptanz", "DBT · Stresstoleranz", "Anerkennen, dass die Situation gerade so ist, ohne sie gutheißen oder dauerhaft hinnehmen zu müssen.", medium = true, high = true, order = 28),
        skill("willingness", "Bereitschaft statt Verhärtung", "DBT · Stresstoleranz", "Den nächsten machbaren Schritt wählen, auch wenn die Situation unangenehm bleibt.", medium = true, high = true, order = 29),
        skill("half_smile", "Halbes Lächeln & offene Hände", "DBT · Stresstoleranz", "Körperhaltung bewusst etwas öffnen und beobachten, ob sich die innere Gegenwehr verändert.", low = true, medium = true, order = 30),

        // DBT – Emotionsregulation
        skill("check_facts", "Fakten prüfen", "DBT · Emotionsregulation", "Auslöser, Interpretation und überprüfbare Fakten voneinander trennen.", low = true, medium = true, order = 40),
        skill("opposite_action", "Entgegengesetzt handeln", "DBT · Emotionsregulation", "Wenn eine Emotion nicht zu den Fakten oder Zielen passt, eine passende Gegenhandlung ausprobieren.", low = true, medium = true, order = 41),
        skill("problem_solving", "Problem lösen", "DBT · Emotionsregulation", "Ein veränderbares Problem konkret benennen, Optionen sammeln und einen kleinen nächsten Schritt wählen.", low = true, medium = true, order = 42),
        skill("build_mastery", "Kompetenzerleben aufbauen", "DBT · Emotionsregulation", "Eine kleine bewältigbare Aufgabe abschließen, die ein Gefühl von Können oder Fortschritt gibt.", low = true, order = 43),
        skill("positive_events", "Positive Momente einplanen", "DBT · Emotionsregulation", "Regelmäßig kleine Aktivitäten einplanen, die angenehm, sinnvoll oder verbindend sind.", low = true, order = 44),
        skill("cope_ahead", "Vorbereiten / Cope Ahead", "DBT · Emotionsregulation", "Eine erwartbar schwierige Situation kurz durchgehen und vorher festlegen, welche Skills du einsetzen willst.", low = true, medium = true, order = 45),
        skill("body_basics", "Körperliche Grundlagen", "DBT · Emotionsregulation", "Schlaf, Essen, Bewegung und körperliche Belastungen mitbeobachten, weil sie emotionale Verwundbarkeit beeinflussen können.", low = true, medium = true, order = 46),

        // DBT – Zwischenmenschlich
        skill("dear_man", "DEAR MAN", "DBT · Zwischenmenschlich", "Ein Anliegen klar, konkret und zielorientiert ansprechen und bei der Sache bleiben.", low = true, medium = true, order = 50),
        skill("give", "GIVE", "DBT · Zwischenmenschlich", "Bei Gesprächen bewusst Beziehung und Respekt im Blick behalten, ohne das eigene Anliegen zu verlieren.", low = true, medium = true, order = 51),
        skill("fast", "FAST", "DBT · Zwischenmenschlich", "Eigene Werte und Selbstrespekt bei einer Bitte, Grenze oder Entscheidung mit berücksichtigen.", low = true, medium = true, order = 52),
        skill("validation", "Validieren", "DBT · Zwischenmenschlich", "Erleben nachvollziehbar spiegeln, ohne automatisch jeder Interpretation oder Forderung zuzustimmen.", low = true, medium = true, order = 53),

        // Regulation / alltagstaugliche Ergänzungen
        general("grounding_54321", "5-4-3-2-1 Grounding", "Regulation · Grounding", "Auf konkrete Sinneseindrücke in der Umgebung fokussieren, um Aufmerksamkeit ins Hier und Jetzt zu holen.", medium = true, high = true, box = true, order = 60),
        general("orienting", "Orientieren", "Regulation · Grounding", "Langsam im Raum umsehen und einige neutrale, konkrete Dinge benennen, die du gerade siehst und hörst.", medium = true, high = true, order = 61),
        general("reduce_stimuli", "Reize reduzieren", "ADHS / Alltag", "Licht, Geräusche, Benachrichtigungen oder andere Reize für eine Weile gezielt reduzieren.", low = true, medium = true, high = true, box = true, order = 62),
        general("movement_break", "Kurze Bewegungsunterbrechung", "ADHS / Alltag", "Für ein paar Minuten aufstehen, gehen oder den Körper bewegen und danach neu einchecken.", low = true, medium = true, order = 63),

        // ADHS – Organisation / Verhalten
        adhd("externalize", "Aufgaben aus dem Kopf holen", "Alles Offene an einem sichtbaren Ort sammeln, statt es im Arbeitsgedächtnis halten zu müssen.", low = true, medium = true, order = 70),
        adhd("small_steps", "Aufgabe verkleinern", "Den nächsten Schritt so klein und konkret machen, dass klar ist, womit du anfangen kannst.", low = true, medium = true, order = 71),
        adhd("visual_timer", "Sichtbarer Timer", "Einen Timer nutzen, damit Zeit und Ende einer Fokusphase sichtbar werden.", low = true, medium = true, order = 72),
        adhd("five_minute_start", "5-Minuten-Start", "Nur einen sehr kurzen Start vereinbaren; danach bewusst neu entscheiden, ob du weitermachst.", low = true, medium = true, order = 73),
        adhd("body_double", "Body Doubling", "Eine andere Person parallel anwesend haben, während du eine Aufgabe beginnst oder erledigst.", low = true, medium = true, order = 74),
        adhd("single_inbox", "Ein Sammelort", "Notizen, Aufgaben und spontane Einfälle zunächst an genau einem verlässlichen Ort erfassen.", low = true, order = 75),
        adhd("visual_cue", "Sichtbare Erinnerung", "Eine Handlung über einen sichtbaren Hinweis, Alarm oder festen Platz in der Umgebung auslösen.", low = true, order = 76),
        adhd("transition_buffer", "Übergangspuffer", "Zwischen Terminen oder Aufgaben bewusst wenige Minuten für Wechsel, Material und Orientierung lassen.", low = true, medium = true, order = 77),
        adhd("limited_choices", "Auswahl verkleinern", "Bei Überforderung die Zahl der Optionen bewusst auf zwei oder drei sinnvolle Möglichkeiten begrenzen.", medium = true, high = true, order = 78),
        adhd("written_next_step", "Nächsten Schritt sichtbar machen", "Eine kurze schriftliche Handlungsanweisung so platzieren, dass sie beim Start direkt sichtbar ist.", low = true, medium = true, order = 79),
        adhd("focus_breaks", "Kurze Fokusblöcke + Pausen", "Längere Aufgaben in überschaubare Arbeitsabschnitte mit geplanten Pausen teilen.", low = true, medium = true, order = 80),
        adhd("routine_anchor", "An Routine ankoppeln", "Eine neue Handlung direkt an etwas koppeln, das ohnehin regelmäßig passiert.", low = true, order = 81)
    )

    private fun skill(
        id: String,
        name: String,
        category: String,
        description: String,
        recommended: Boolean = false,
        low: Boolean = false,
        medium: Boolean = false,
        high: Boolean = false,
        breakdown: Boolean = false,
        box: Boolean = false,
        order: Int
    ) = SkillEntity(
        id = id,
        name = name,
        category = category,
        description = description,
        evidenceLabel = "DBT-Kernskill",
        recommended = recommended,
        tensionLow = low,
        tensionMedium = medium,
        tensionHigh = high,
        tensionBreakdown = breakdown,
        inToolbox = box,
        sortOrder = order
    )

    private fun adhd(
        id: String,
        name: String,
        description: String,
        low: Boolean = false,
        medium: Boolean = false,
        high: Boolean = false,
        order: Int
    ) = SkillEntity(
        id = id,
        name = name,
        category = "ADHS / Struktur",
        description = description,
        evidenceLabel = "ADHS · praktische Strategie",
        tensionLow = low,
        tensionMedium = medium,
        tensionHigh = high,
        sortOrder = order
    )

    private fun general(
        id: String,
        name: String,
        category: String,
        description: String,
        low: Boolean = false,
        medium: Boolean = false,
        high: Boolean = false,
        box: Boolean = false,
        order: Int
    ) = SkillEntity(
        id = id,
        name = name,
        category = category,
        description = description,
        evidenceLabel = "Alltag · Regulationsstrategie",
        tensionLow = low,
        tensionMedium = medium,
        tensionHigh = high,
        inToolbox = box,
        sortOrder = order
    )
}
