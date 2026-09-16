# MindTrack – Android MVP 0.3.2

Android-first prototype for fast tracking of **energy** and **tension**, plus a personal skills toolbox and discovery notebook.

## New in MVP 0.3.2

- Skill selections now have a high-contrast selected state: checkmark, thicker border, stronger fill, and bolder text.
- Added a home-screen widget with direct `B / 1–5` capture for energy and tension plus all four `+/−` actions.
- Widget taps save immediately and then open Quick Capture for optional context.
- Added in-app widget pinning on supported launchers (Android 8+).
- Quick Settings tile launch is more robust: on secure lock screens it requests unlock instead of silently failing on OEMs that block direct activity launch.
- Version bumped to `0.3.2`.

## Included since MVP 0.3

- **45 prefilled skills / strategies** grouped by type
  - DBT · Achtsamkeit
  - DBT · Stresstoleranz
  - DBT · Emotionsregulation
  - DBT · Zwischenmenschlich
  - Regulation · Grounding
  - ADHS / Alltag
  - ADHS / Struktur
- `STOP` is marked as a neutral **Start recommendation** in the library. It is not marked as "helps you" until the user decides that.
- Per-skill personal markers:
  - `Ausprobiert`
  - `Hilft`
  - useful at `Niedrig`, `Mittel`, `Hoch`, or `B`
  - add/remove from **Meine Box**
- Personal toolbox can be filtered by tension level.
- **Skill chains** can be started from the currently filtered toolbox.
- Every completed skill-chain step is recorded in the shared timeline.
- **Entdeckungen** notebook:
  - free-text observation
  - affects energy and/or tension
  - optional `+` / `−` direction
  - optional personal context tag
  - personal context tags can be promoted into future context pickers
- Context tags from Discoveries automatically appear in the normal app's context sheet.
- Personal context tags also appear in Quick Capture **when the device is unlocked**. When launched above a locked device, Quick Capture deliberately shows only generic context tags.
- Database migration from MVP 0.2 to 0.3 keeps existing tracking events.

## Existing fast-capture features

- Jetpack Compose UI
- Room local database; `allowBackup=false`
- Energy and tension levels: `B / 1 / 2 / 3 / 4 / 5`
- `B` is a dedicated `BREAKDOWN` event, never numeric zero
- Four directional events: Energy +/− and Tension +/−
- Context is optional and attached after the event has already been saved
- Shared local timeline
- Quick Capture activity
- Home-screen widget: direct level and `+/−` capture
- Android Quick Settings Tile: `MindTrack Check-in`
- Android 13+ native "Add Quick Settings tile" request

## Data model

Room database version 2 contains:

- `tracking_events`
- `skills`
- `skill_usage`
- `discoveries`
- `context_tags`

The 1 → 2 Room migration creates the new tables without deleting the existing tracking timeline.

## Skill library note

The library intentionally distinguishes between:

- **DBT skills** that belong to the established DBT skills-training framework; and
- **ADHD / everyday strategies** that are practical behavioral or environmental supports.

The app does **not** claim that each individual listed skill has separately proven effectiveness for every person. DBT evidence applies to the structured treatment/skills framework, and ADHD guidance supports structured psychological interventions and environmental modifications. Personal `Ausprobiert` and `Hilft` markers are kept separate from these labels.

Reference points used while curating the starter library:

- Behavioral Tech Institute – DBT skills domains: https://behavioraltech.org/training-catalog/
- NICE – Borderline personality disorder (CG78): https://www.nice.org.uk/guidance/cg78
- NICE – ADHD diagnosis and management (NG87): https://www.nice.org.uk/guidance/ng87
- CDC – ADHD in adults: https://www.cdc.gov/adhd/about/adhd-in-adults.html

Descriptions in this project are short original summaries, not copied workbook instructions.

## Privacy behavior

The project is local-first and cloud sync is not implemented. Android backup is disabled in the manifest.

Quick Capture is designed for lock-screen use and does not expose history, old notes, pattern summaries, or personal context tags while the device is locked. Device/OEM lock-screen behavior can still vary.

## Build configuration

- Package: `de.mindtrack.app`
- minSdk: 26
- targetSdk: 36
- compileSdk: 36
- Java: 17
- Kotlin: 2.3.21
- Android Gradle Plugin: 8.13.2
- Compose BOM: 2026.04.01
- Room: 2.8.5

## Open in Android Studio

1. Extract the ZIP.
2. Open the `MindTrackAndroid` folder in Android Studio.
3. Let Android Studio run Gradle sync.
4. Use Gradle 8.13 for AGP 8.13.x (the included GitHub Actions workflow already does this).
5. Install Android SDK 36 if requested.
6. Run on an emulator or physical Android device.

## Useful test cases

1. Existing 0.2 install → update to 0.3 → old timeline remains.
2. Open Skills → Bibliothek → categories are grouped.
3. Mark a skill `Ausprobiert`, `Hilft`, and one or more tension ranges.
4. Add it to `Meine Box` → filter the box by tension range.
5. Start a chain → mark a step done → a skill-use entry appears in Verlauf.
6. Create an Entdeckung with context tag `Einkaufen` and enable Schnell-Kontext.
7. Record a new event → `Einkaufen` appears in the normal context sheet.
8. Open Quick Capture while unlocked → personal tags are available.
9. Open Quick Capture over the lock screen → only generic tags are shown.
10. Tap `B` → it remains Breakdown, not level 0.
11. Add the MindTrack widget → tap a level → the event is saved and Quick Capture opens for optional context.
12. Toggle `Ausprobiert`, `Hilft`, or a tension level → selected chips show a visible checkmark and thick outline.

## Still intentionally out of scope

- Scheduled notification check-ins
- Charts and pattern analysis
- Import/export
- Optional encryption layer beyond Android app sandbox / device encryption
- Cloud sync
- Therapist-facing export

## 0.3.2 UI polish

- Neues adaptives Batterie-App-Icon inklusive Android-13-Monochrom-Variante.
- Bottom-Navigation sitzt jetzt bündig am unteren Systemrand; das doppelte Navigation-Bar-Inset wurde entfernt.
- Dezente Trennlinie und ruhige Surface-Farbe für klarere Abgrenzung zum Inhalt.
