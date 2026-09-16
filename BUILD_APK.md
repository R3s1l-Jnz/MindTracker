# APK bauen

Das Projekt enthält eine GitHub-Actions-Workflow-Datei unter `.github/workflows/build-apk.yml`.

Sie baut automatisch eine Debug-APK und lädt sie als Artifact `MindTrack-debug-apk` hoch.

## GitHub
1. Projekt in ein GitHub-Repository hochladen.
2. Unter **Actions** den Workflow **Build Android APK** öffnen.
3. **Run workflow** auswählen (oder auf `main`/`master` pushen).
4. Nach erfolgreichem Lauf das Artifact **MindTrack-debug-apk** herunterladen.
5. Darin liegt `app-debug.apk`.

Die APK ist eine Debug-APK und damit direkt zum Testen geeignet, aber nicht für den Play Store signiert.
