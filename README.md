# SteamCheck - Application Multiplateforme

## Architecture du Projet

### Vue d'ensemble
SteamCheck est une application Kotlin Multiplatform (KMP) qui permet de consulter les détails des jeux Steam. L'application est structurée selon le pattern MVVM (Model-View-ViewModel) et utilise l'architecture Clean Architecture.

### Structure des dossiers

```
SteamCheck/
├── commonMain/
│   ├── data/           # Couche données (Repository, Sources de données)
│   ├── domain/         # Logique métier et Use Cases
│   ├── presentation/   # ViewModels partagés
│   └── di/            # Injection de dépendances
├── androidApp/         # Application Android
├── desktopApp/        # Application Desktop
└── iosMain/            # Application iOS
```

### Composants principaux

#### Shared Module
- **Data Layer**: Implémente les repositories et gère les sources de données
- **Domain Layer**: Contient la logique métier et les modèles de données
- **Presentation Layer**: Contient les ViewModels partagés
- **DI**: Configuration de Koin pour l'injection de dépendances

#### Plateformes spécifiques
- **androidApp**: Interface utilisateur Android avec Jetpack Compose
- **desktopApp**: Interface utilisateur Desktop avec Compose Multiplatform
- **iosApp**: Interface utilisateur iOS avec SwiftUI

### Technologies utilisées

- **Kotlin Multiplatform**: Pour le partage de code entre plateformes
- **Compose Multiplatform**: Pour les interfaces utilisateurs
- **Ktor**: Pour les appels réseau
- **SqlDelight**: Pour le stockage local
- **Koin**: Pour l'injection de dépendances

### Flow de données

1. L'UI interagit avec les ViewModels
2. Les ViewModels appellent les Use Cases
3. Les Use Cases orchestrent les repositories
4. Les Repositories gèrent les sources de données (API, BDD locale)

### Configuration et déploiement

Pour compiler et exécuter le projet :

```bash
# Android
./gradlew :androidApp:assembleDebug

# Desktop
./gradlew :desktopApp:run

# iOS
cd iosApp
pod install
```
