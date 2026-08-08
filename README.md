<div align="center">

# 🇳🇵 Nepali IDE — Code IDE

### An AI-Native Mobile IDE & Code Editor Built with Kotlin & Jetpack Compose

**Write, run, and manage code right from your Android device — powered by Google Gemini AI.**

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](#license)
[![Platform](https://img.shields.io/badge/Platform-Android_5.0%2B-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin_1.9%2B-purple.svg)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack_Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![AI](https://img.shields.io/badge/AI-Google_Gemini-blueviolet.svg)](https://ai.google.dev)
[![Database](https://img.shields.io/badge/Database-Room-orange.svg)](https://developer.android.com/jetpack/androidx/releases/room)

**Developer:** [Diwas Khatri](https://github.com/DiwasKhatri07) · **Built for Nepal 🇳🇵 · Made in Nepal**

</div>

---

## 📖 Project Overview

**Nepali IDE (Code IDE)** is a sophisticated, AI-native mobile Integrated Development Environment (IDE) and code editor for Android, designed and developed by **Diwas Khatri**. It brings a desktop-class coding experience to your pocket — combining a full-featured code editor with syntax highlighting, an embedded Python interpreter, live HTML preview, an AI assistant powered by **Google Gemini**, and a complete project workspace with file versioning, Git integration, and local SQLite storage.

Built entirely with **Kotlin** and **Jetpack Compose**, the app follows modern Android architecture with a clean MVVM pattern, Room persistence, coroutine-based concurrency, and Firebase GenAI integration. Whether you are a student, a competitive programmer, or a professional developer, Nepali IDE lets you prototype ideas, practice algorithms, and ship features — all without leaving your phone.

> **Made in Nepal, for developers everywhere.** Nepali IDE proves that world-class developer tools can be built locally — one line of Kotlin at a time.

## ✨ Key Features

### 🧠 AI-Powered Development (Google Gemini)

| Feature | Description |
| --- | --- |
| **AI Code Completion** | Context-aware code suggestions powered by the Gemini API, right inside the editor |
| **AI Code Analysis** | One-tap intelligent review of your code — bugs, optimizations, and best practices |
| **AI Unit Testing** | Automatically generate unit tests for your code with a single click |
| **AI Assistant Panel** | A dedicated chat-style drawer to brainstorm, debug, and explain code with Gemini |

### ✍️ Smart Code Editor

| Feature | Description |
| --- | --- |
| **Syntax Highlighting** | Themeable highlighting for Python, HTML/CSS, and web languages |
| **Multi-Tab Editing** | Open multiple files in tabs and switch between them instantly |
| **Undo / Redo** | Full editing history with undo and redo support |
| **Auto-Save** | Changes are saved automatically so you never lose your work |
| **Find & Replace** | Powerful search-and-replace bar for quick refactoring |
| **Custom Keyboard Toolbar** | Mobile-friendly toolbar with common coding symbols for faster typing |
| **Command Palette** | VS Code-style command palette (`Ctrl+P`-like quick actions) |
| **Status Bar** | Live cursor position (line/column) and file information |

### 🐍 Built-in Interpreters & Live Preview

| Feature | Description |
| --- | --- |
| **Python Interpreter** | Run Python scripts and expressions directly on your device — no server needed |
| **Python REPL** | Interactive Read-Eval-Print-Loop console for experimenting with code |
| **HTML Live Preview** | Render and preview HTML/CSS/JS output instantly in a built-in web view |

### 📂 Project Workspace & Data

| Feature | Description |
| --- | --- |
| **Project Management** | Create, organize, and switch between multiple coding projects |
| **File Explorer** | Full drawer-based file tree with create, rename, delete, and snippet support |
| **Version History** | Automatic snapshots of every file — restore any previous version anytime |
| **Database Inspector** | Visual SQLite (Room) database browser to inspect tables and records |
| **JSON & CSV Tools** | Format, validate JSON and view CSV data in polished dialogs |
| **Snippet Library** | Save and reuse code snippets across projects |
| **Todo Tracker** | Built-in task list to plan your sprints inside the IDE |

### 🔗 Git Integration

| Feature | Description |
| --- | --- |
| **Git Panel** | Commit changes with messages, track history, and manage your workspace from a built-in Git dialog |

### 🎨 Theming & UX

| Feature | Description |
| --- | --- |
| **Editor Themes** | Switch between multiple color schemes for the editor |
| **Material 3 Design** | Modern, accessible UI following Material Design 3 guidelines |
| **Dashboard** | Project dashboard with an overview of files, stats, and activity |

## 🏗️ Architecture & Tech Stack

The app is built on a clean **MVVM (Model-View-ViewModel)** architecture with a single-activity Jetpack Compose design:

| Layer | Technology | Purpose |
| --- | --- | --- |
| **Language** | Kotlin 1.9+ | 100% Kotlin codebase |
| **UI Toolkit** | Jetpack Compose + Material 3 | Declarative, reactive UI |
| **Architecture** | MVVM + Repository Pattern | Clean separation of concerns |
| **Database** | Room (SQLite) | Local persistence for projects, files, versions, and snippets |
| **AI / LLM** | Google Gemini API (Firebase GenAI, Retrofit + OkHttp) | AI completion, analysis, and chat |
| **Concurrency** | Kotlin Coroutines + Flow | Async-safe data and state handling |
| **Serialization** | Moshi | JSON parsing for AI API responses |
| **Interpreter** | Custom Kotlin Python interpreter | On-device Python execution |
| **Testing** | JUnit + Roborazzi + Compose Test | Unit and screenshot testing |
| **Build** | Gradle Kotlin DSL + KSP | Modern build configuration |

```
Nepali-IDE
├── app/src/main/java/com/example/
│   ├── ai/                  # Gemini AI models & repository (REST + Firebase GenAI)
│   ├── data/                # Room entities, DAOs & WorkspaceRepository
│   ├── editor/              # SyntaxHighlighter & EditorTheme
│   ├── interpreter/         # PythonInterpreter & HtmlPreviewView
│   ├── ui/
│   │   ├── components/      # Editor, panels, dialogs & toolbars (Compose UI)
│   │   └── theme/           # Material 3 theming
│   └── MainActivity.kt      # Single-activity Compose entry point
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala or newer
- JDK 17+
- Android SDK 35+

### Setup & Run

1. Clone the repository:

```bash
git clone https://github.com/DiwasKhatri07/Nepali-IDE.git
cd Nepali-IDE
```

2. Copy the environment example and add your **Gemini API key** (get one free at [Google AI Studio](https://aistudio.google.com/app/apikey)):

```bash
cp .env.example .env
# Edit .env and set: GEMINI_API_KEY=your_api_key_here
```

3. Open the project in Android Studio and click **Run** (or build from the command line):

```bash
./gradlew assembleDebug
```

## 📂 Project Structure

| Directory | Description |
| --- | --- |
| `app/src/main/java/.../ai/` | Gemini AI request/response models and repository |
| `app/src/main/java/.../data/` | Room database, entities, DAOs, and workspace repository |
| `app/src/main/java/.../editor/` | Syntax highlighting engine and editor color themes |
| `app/src/main/java/.../interpreter/` | On-device Python interpreter and HTML preview view |
| `app/src/main/java/.../ui/components/` | All Compose UI components (editor, drawers, dialogs, toolbars) |
| `app/src/main/java/.../ui/theme/` | Material 3 color, type, and theme definitions |
| `.github/FUNDING.yml` | Sponsorship configuration |
| `.env.example` | Example environment variables for the Gemini API key |

## 🏆 Roadmap

- [ ] PHP and JavaScript interpreters
- [ ] GitHub sign-in and cloud sync for workspaces
- [ ] Collab mode — share snippets and projects with friends
- [ ] More language syntax themes (Java, C++, Kotlin, SQL)
- [ ] Terminal emulator inside the app
- [ ] Firebase Analytics and crash reporting
- [ ] Publish to Google Play Store 🎉

## 🤝 Contributing

Contributions are what make the open-source community amazing! Any contribution — big or small — is deeply appreciated:

1. **Fork** the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. Open a **Pull Request**

Please make sure your code builds cleanly and follows the existing Kotlin coding conventions.

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Developer Credits

<div align="center">

### Crafted with ❤️ by **Diwas Khatri**

| | |
| --- | --- |
| 👤 **Developer** | [Diwas Khatri](https://github.com/DiwasKhatri07) |
| 🐙 **GitHub** | [github.com/DiwasKhatri07](https://github.com/DiwasKhatri07) |
| 🇳🇵 **Location** | Nepal |
| 🛠️ **Stack** | Kotlin · Jetpack Compose · Room · Gemini AI |

**If this project helps you, please give it a ⭐ Star on GitHub!**

</div>

## 🏷️ Tags & Topics

`android` · `kotlin` · `jetpack-compose` · `mobile-ide` · `code-editor` · `ai-code-assistant` · `gemini-ai` · `python-interpreter` · `html-preview` · `syntax-highlighting` · `room-database` · `mvvm` · `material-design-3` · `nepal` · `nepali-developer` · `developer-tools` · `ide` · `coding-app` · `android-development` · `open-source`
