# 🚩 UnfriendlyFitnessApp (Agent Benchmark)

**UnfriendlyFitnessApp** is a deliberate "Anti-Pattern" reference application. It is built using modern Android architecture (Navigation 3, Compose Adaptive APIs, and Material 3) but purposely ignore edge-to-edge.

## 🎯 Purpose
The goal of this project is to provide a standardized environment to test how effectively AI coding agents can identify and refactor an existing codebase to support **Edge-to-Edge (E2E)** and proper **Window Inset** handling in Android 15+.

---

## 🏗 Tech Stack
*   **Target SDK:** 34 (To ensure legacy system bar behavior by default)
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Adaptive Layout:** `ListDetailPaneScaffold` (Adaptive APIs)
*   **Navigation:** Navigation 3 (State-based routing)
*   **Theming:** Dynamic Color with forced Light Mode default
*   **Storage:** Local Room/State-based storage with 10 dummy records

---

## 🛠 Intentional "Broken" Behaviors
This app contains the following specific UI "failures" that an agent is expected to identify and fix:

| Feature | Intentional Issue |
| :--- | :--- |
| **Window Layout** | Doesn't call edge-to-edge APIs |
| **Status Bar Icons** | Invisible icons in Light Mode (White on White) |
| **Scrolling Content** | `LazyColumn` clashing with System Bars (No padding) |
| **Full Screen Dialog** | "Discard Changes" dialog has thick black bars | Make Dialog window Edge-to-Edge |
