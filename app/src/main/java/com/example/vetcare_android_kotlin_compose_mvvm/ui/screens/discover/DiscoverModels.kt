package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.discover

import com.example.vetcare_android_kotlin_compose_mvvm.R

/**
 * Modelo de contenido del feed de Discover
 */
data class DiscoverPost(
    val id: String,
    val authorName: String,
    val authorAvatar: Int? = null,
    val imageRes: Int,
    val title: String,
    val description: String,
    val likes: Int,
    val comments: Int,
    val category: DiscoverCategory,
    val timeAgo: String
)

enum class DiscoverCategory(val displayName: String) {
    TIP("Tip"),
    NUTRITION("Nutrición"),
    HEALTH("Salud"),
    GROOMING("Grooming"),
    FUN("Diversión")
}

/**
 * Modelo de Story
 */
data class DiscoverStory(
    val id: String,
    val name: String,
    val imageRes: Int? = null,
    val isViewed: Boolean = false
)

/**
 * Datos mock para Discover
 */
object DiscoverMockData {

    val stories = listOf(
        DiscoverStory("s1", "VetCare", isViewed = false),
        DiscoverStory("s2", "Dr. Pedro", isViewed = false),
        DiscoverStory("s3", "Max", isViewed = true),
        DiscoverStory("s4", "Luna", isViewed = true),
        DiscoverStory("s5", "Tips", isViewed = false)
    )

    val posts = listOf(
        DiscoverPost(
            id = "p1",
            authorName = "VetCare Tips",
            imageRes = R.drawable.ic_launcher_foreground,
            title = "5 señales de que tu mascota necesita ir al veterinario",
            description = "Aprende a identificar cuando tu mascota requiere atención médica urgente. Cambios en el comportamiento, pérdida de apetito y más...",
            likes = 234,
            comments = 45,
            category = DiscoverCategory.HEALTH,
            timeAgo = "2h"
        ),
        DiscoverPost(
            id = "p2",
            authorName = "Nutrición Pet",
            imageRes = R.drawable.ic_launcher_foreground,
            title = "La alimentación ideal para perros senior",
            description = "Los perros mayores tienen necesidades nutricionales especiales. Descubre qué alimentos son mejores para ellos.",
            likes = 189,
            comments = 32,
            category = DiscoverCategory.NUTRITION,
            timeAgo = "5h"
        ),
        DiscoverPost(
            id = "p3",
            authorName = "Grooming Pro",
            imageRes = R.drawable.ic_launcher_foreground,
            title = "Cómo cepillar correctamente a tu gato",
            description = "El cepillado regular previene bolas de pelo y mantiene el pelaje brillante. Te enseñamos la técnica correcta.",
            likes = 156,
            comments = 28,
            category = DiscoverCategory.GROOMING,
            timeAgo = "8h"
        ),
        DiscoverPost(
            id = "p4",
            authorName = "VetCare",
            imageRes = R.drawable.ic_launcher_foreground,
            title = "Calendario de vacunación para cachorros",
            description = "Mantén a tu cachorro protegido con el calendario de vacunación completo. Primera dosis, refuerzos y más.",
            likes = 312,
            comments = 67,
            category = DiscoverCategory.HEALTH,
            timeAgo = "1d"
        ),
        DiscoverPost(
            id = "p5",
            authorName = "Pet Fun",
            imageRes = R.drawable.ic_launcher_foreground,
            title = "Los mejores juegos para hacer con tu perro en casa",
            description = "Mantén a tu perro activo y feliz con estos juegos divertidos que puedes hacer en casa.",
            likes = 423,
            comments = 89,
            category = DiscoverCategory.FUN,
            timeAgo = "1d"
        )
    )
}

