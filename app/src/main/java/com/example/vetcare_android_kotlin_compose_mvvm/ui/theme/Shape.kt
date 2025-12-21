package com.example.vetcare_android_kotlin_compose_mvvm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// VetCare Design Tokens - Shapes
val VetCareShapes = Shapes(
    // Extra small for small icons/indicators
    extraSmall = RoundedCornerShape(4.dp),

    // Small for chips, small buttons
    small = RoundedCornerShape(12.dp),

    // Medium for buttons, input fields
    medium = RoundedCornerShape(18.dp),

    // Large for cards, dialogs
    large = RoundedCornerShape(28.dp),

    // Extra large for onboarding containers, hero cards
    extraLarge = RoundedCornerShape(36.dp)
)

// Custom shape values for specific components
object VetCareShapeTokens {
    val CardRadius = 28.dp
    val ChipRadius = 18.dp
    val ButtonRadius = 18.dp
    val AvatarRadiusSmall = 16.dp
    val AvatarRadiusLarge = 20.dp
    val OnboardingContainerRadius = 36.dp
    val ServiceTileRadius = 20.dp
    val BottomSheetRadius = 28.dp
}

// VetCare Design Tokens - Spacing
object VetCareSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp

    // Screen specific
    val screenHorizontalPadding = 16.dp
    val cardPadding = 16.dp
    val sectionGap = 20.dp
    val gridSpacing = 12.dp
}

// VetCare Design Tokens - Elevation
object VetCareElevation {
    val none = 0.dp
    val low = 1.dp
    val medium = 2.dp
    val high = 4.dp
}

// VetCare Design Tokens - Sizes
object VetCareSizes {
    // Touch targets
    val minTouchTarget = 48.dp

    // Icons
    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 28.dp
    val iconXLarge = 32.dp

    // Avatars
    val avatarSmall = 40.dp
    val avatarMedium = 56.dp
    val avatarLarge = 80.dp
    val avatarXLarge = 120.dp

    // Bottom bar
    val bottomBarHeight = 64.dp
    val bottomBarIconSize = 24.dp
}

