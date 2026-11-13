# Contributing to Poswal Noor

Thank you for your interest in contributing to **Poswal Noor**! We welcome open-source contributions that help make this Islamic companion more reliable, accessible, and beneficial.

---

## 📜 Religious & Content Guidelines (Strict)
Because this application presents sacred Islamic scripture (Quran and Hadith), all contributors must adhere to strict integrity rules:
1. **Never alter Arabic text**: Arabic text must remain authentic Uthmani script matching verified Quranic mus'haf.
2. **Never machine-translate**: Do not use automated translation services (Google Translate, DeepL, LLMs) to generate new Quran or Hadith translations. Only verified, peer-reviewed, open-licensed translations from known scholars are accepted.
3. **Always cite source & grade**: Every hadith must include the collection name, hadith number, and authenticity grade (e.g., Sahih, Hasan).
4. **No sectarian polemics**: Keep content universally beneficial, educational, and focused on core moral character, prayer, patience, and good deeds.

---

## 🛠️ Development Workflow

1. **Fork & Clone**:
   ```bash
   git clone https://github.com/<your-username>/PoswalNoor.git
   cd PoswalNoor
   ```
2. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Build & Verify**:
   Make sure the project builds and all unit tests pass before submitting a pull request:
   ```bash
   ./gradlew test
   ./gradlew assembleDebug
   ```
4. **Submit a Pull Request**:
   Describe your changes clearly, linking any relevant issues.

---

## 💡 Code Standards
- **Simplicity First**: Write clean, concise Kotlin without unnecessary dependencies.
- **RTL & Accessibility**: Ensure layout constraints use `start`/`end` (not `left`/`right`) and maintain strict contrast ratios (never place Gold text on Ivory).
- **Offline First**: The app must function 100% offline with zero required network calls.

Thank you for contributing! JazakAllah Khair.
