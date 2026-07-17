# FitLink

An Android application for connecting to your FTMS-supported fitness equipment.

## General project structure

`Core` directory contains ble repositories, constants, and ui components, to be shared in feature screens.

`Feature` directory contains composable screens and their respective viewmodel.

`Nav` directory contains routes and is responsible for navigation.

```
└── FitLink/
    ├── core/
    │   ├── ble
    │   ├── data
    │   ├── di
    │   ├── ftms
    │   └── ui
    ├── feature/
    │   ├── workout
    │   └── home
    ├── nav
    ├── ui.theme
    ├── Dev.kt
    ├── FitLinkApp.kt
    └── MainActivity.kt
```

### Architecture

The app follows Android's MVVM architecture. Viewmodels receive data from repositories and updates views (screens that are composables) via `StateFlow`

General flow of app:
```
ble device <-> ble repository <-> view model <-> screen
```
