# Contributing to VetCare 🐾

First off, thank you for considering contributing to VetCare! It's people like you that make this veterinary management system better for everyone.

## 🌟 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples** (code snippets, screenshots, logs)
- **Describe the behavior you observed** and what you expected
- **Include device/Android version details**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion:

- **Use a clear and descriptive title**
- **Provide a detailed description** of the suggested enhancement
- **Explain why this enhancement would be useful** for veterinary clinics
- **Include mockups or examples** if applicable

### Pull Requests

1. **Fork the repo** and create your branch from `main`
2. **Make your changes** following our coding standards
3. **Test your changes** on multiple Android versions/devices
4. **Update documentation** if needed
5. **Write clear commit messages**
6. **Create a Pull Request** with a comprehensive description

## 🎯 Good First Issues

Look for issues labeled `good first issue` - these are perfect for newcomers!

## 💻 Development Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 26+ (Android 8.0+)
- Kotlin 1.9.0+

### Setup Steps

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/vetcare-android-kotlin-compose-mvvm.git
cd vetcare-android-kotlin-compose-mvvm

# Open in Android Studio
# File -> Open -> Select the project folder

# Sync Gradle
# Android Studio will automatically sync dependencies

# Run on emulator or physical device
# Click the green "Run" button or Shift + F10
```

## 📝 Coding Standards

### Kotlin

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names in English
- Add KDoc comments for public APIs
- Prefer `val` over `var` when possible
- Use data classes for models
- Leverage Kotlin's null safety

```kotlin
/**
 * Validates pet registration data
 * 
 * @param petName The name of the pet
 * @param petAge The age in years
 * @return ValidationResult indicating if data is valid
 */
fun validatePetData(petName: String, petAge: Int): ValidationResult {
    // Implementation
}
```

### Jetpack Compose

- Use `remember` and `rememberSaveable` appropriately
- Minimize recomposition with smart state management
- Keep composables small and focused
- Extract complex UI logic into separate functions
- Use Material 3 components
- Follow Material Design guidelines

```kotlin
// Good: Small, focused composable
@Composable
fun PetCard(
    pet: Pet,
    onPetClick: (Pet) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onPetClick(pet) },
        modifier = modifier
    ) {
        // Implementation
    }
}
```

### MVVM Architecture

- **Model**: Data classes and repository patterns
- **View**: Composable functions
- **ViewModel**: Business logic and state management

```kotlin
// ViewModel example
class PetListViewModel @Inject constructor(
    private val repository: PetRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PetListUiState>(PetListUiState.Loading)
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()
    
    fun loadPets() {
        viewModelScope.launch {
            // Implementation
        }
    }
}
```

### File Organization

```
app/
├── data/           # Models, repositories, data sources
├── di/             # Dependency injection modules
├── domain/         # Use cases, business logic
├── ui/
│   ├── components/ # Reusable composables
│   ├── screens/    # Screen-level composables
│   ├── theme/      # Material 3 theme
│   └── navigation/ # Navigation graphs
└── utils/          # Helper functions
```

### Naming Conventions

- **Packages**: lowercase, no underscores (`com.vetcare.ui.screens`)
- **Classes**: PascalCase (`PetDetailScreen`, `AppointmentViewModel`)
- **Functions**: camelCase (`loadPets()`, `formatDate()`)
- **Composables**: PascalCase (`PetCard`, `AppointmentForm`)
- **Resources**: snake_case (`ic_pet`, `screen_appointments`)

## 🧪 Testing

### Unit Tests

```kotlin
@Test
fun `validatePetAge should return success for valid age`() {
    // Arrange
    val age = 5
    
    // Act
    val result = validatePetAge(age)
    
    // Assert
    assertTrue(result.isSuccess)
}
```

### UI Tests

```kotlin
@Test
fun petCard_clickTriggers_navigation() {
    composeTestRule.setContent {
        PetCard(
            pet = testPet,
            onPetClick = { /* Verify navigation */ }
        )
    }
    
    composeTestRule.onNodeWithText("Max").performClick()
    // Verify navigation occurred
}
```

### Testing Checklist

Before submitting a PR:
- [ ] All unit tests pass
- [ ] UI tests pass
- [ ] Manual testing on Android 8.0 (API 26)
- [ ] Manual testing on Android 14 (API 34)
- [ ] Testing with TalkBack enabled
- [ ] Testing in dark mode
- [ ] Testing with different font sizes

## 🎨 UI/UX Guidelines

### Material Design 3

- Use Material 3 color system (primary, secondary, tertiary)
- Respect elevation and shadows
- Use appropriate animation durations
- Follow spacing guidelines (4dp grid)

### Accessibility

- Provide meaningful content descriptions
- Support TalkBack navigation
- Ensure color contrast ratios meet WCAG 2.1 AA
- Support font scaling
- Test with accessibility scanner

```kotlin
// Accessibility example
Icon(
    imageVector = Icons.Default.Add,
    contentDescription = "Add new pet",
    modifier = Modifier.semantics {
        role = Role.Button
    }
)
```

## 📊 Performance Guidelines

- Minimize overdraw in complex layouts
- Use `LazyColumn`/`LazyRow` for lists
- Optimize image loading (Coil)
- Avoid blocking main thread
- Use `remember` to cache expensive calculations
- Profile with Android Studio Profiler

## 🔒 Security

- Never commit sensitive data (API keys, credentials)
- Use encrypted SharedPreferences for sensitive data
- Validate all user inputs
- Follow OWASP Mobile Security guidelines
- Use ProGuard/R8 for code obfuscation

## 📋 Commit Message Guidelines

Use clear, descriptive commit messages following Conventional Commits:

```
feat: Add appointment reminder notifications
fix: Resolve crash on pet deletion
docs: Update README with setup instructions
style: Format code according to Kotlin conventions
refactor: Extract appointment logic to use case
perf: Optimize pet list rendering
test: Add unit tests for vaccination tracking
chore: Update Gradle dependencies
```

### Format:
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `build`: Build system changes
- `ci`: CI/CD changes

## 🌐 Internationalization

When adding text:
- Add strings to `strings.xml`
- Support Spanish translation in `strings.xml (es)`
- Use plurals where appropriate
- Format dates/times according to locale

```xml
<!-- strings.xml -->
<string name="pet_age_years">%d years old</string>

<!-- strings.xml (es) -->
<string name="pet_age_years">%d años</string>
```

## 🎯 Feature Areas

### High Priority
- 🏥 **Appointments**: Scheduling, reminders, calendar integration
- 🐕 **Pet Management**: Registration, medical history, vaccinations
- 👨‍⚕️ **Veterinarian Tools**: Patient notes, prescriptions, diagnostics
- 📊 **Reports**: Analytics, statistics, export functionality

### Medium Priority
- 💊 **Medication Tracking**: Dosage, schedules, refill alerts
- 📱 **Owner Portal**: Pet owner mobile access, appointment booking
- 💰 **Billing**: Invoice generation, payment tracking
- 🔔 **Notifications**: Smart reminders, push notifications

### Nice to Have
- 🌙 **Dark Theme**: Full dark mode support
- 📷 **Pet Photos**: Gallery, before/after comparisons
- 📈 **Growth Charts**: Weight tracking, health trends
- 🗺️ **Location**: Clinic locator, emergency services

## 💡 Getting Help

- **Questions?** Open a discussion or issue
- **Stuck?** Tag maintainers in your PR
- **Ideas?** Share in discussions section

## 📜 Code of Conduct

Be respectful, inclusive, and constructive. This is a welcoming community for all skill levels.

## 🎁 Recognition

All contributors will be recognized in the project! Your contribution, no matter how small, is valued.

## 📞 Contact

- **GitHub**: [@RodrigoSanchezDev](https://github.com/RodrigoSanchezDev)
- **Email**: rodrigo@sanchezdev.com

Thank you for contributing to VetCare! 🚀🐾
