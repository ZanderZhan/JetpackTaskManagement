# JetpackTaskManagement

A simple task management Android application built with Jetpack Compose and modern Android development practices.

## Features

- **Task List**: View all your tasks in a clean, scrollable list.
- **Add Tasks**: Easily add new tasks via a dedicated screen accessed by a Floating Action Button.
- **Completion Toggle**: Mark tasks as completed or pending by clicking on them.
- **Delete with Confirmation**: Long-press a task to delete it, with a confirmation dialog to prevent accidental removals.
- **Modern Navigation**: Uses the new **Navigation 3** library for handling app flow.
- **State Management**: Implements `ViewModel` and `LiveData` to maintain UI state consistently.

## Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Navigation**: [Navigation 3](https://developer.android.com/guide/navigation/navigation-3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Management**: Version Catalogs (`libs.versions.toml`)
- **Serialization**: Kotlinx Serialization for navigation routes.
- **Material Design**: Material 3 components and theme.

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio (Ladybug or later recommended).
3. Build and run the app on an emulator or physical device.

## Project Structure

- `model/`: Data classes representing the application's domain (e.g., `Task`).
- `viewmodel/`: `TaskListViewModel` managing the state and business logic for the task list.
- `screen/`: Composable screens (`TaskListScreen`, `TaskAddScreen`) defining the UI.
- `MainActivity.kt`: The entry point of the application, setting up Navigation 3 and the root UI.
