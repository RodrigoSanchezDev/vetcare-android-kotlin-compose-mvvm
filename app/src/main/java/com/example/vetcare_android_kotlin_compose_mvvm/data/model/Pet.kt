package com.example.vetcare_android_kotlin_compose_mvvm.data.model

/**
 * Especies de mascotas soportadas
 */
enum class PetSpecies(val displayName: String) {
    DOG("Perro"),
    CAT("Gato"),
    BIRD("Ave"),
    RABBIT("Conejo"),
    HAMSTER("Hámster"),
    OTHER("Otro")
}

/**
 * Mascota registrada en la veterinaria
 * @property id Identificador único
 * @property ownerId ID del dueño
 * @property name Nombre de la mascota
 * @property species Especie
 * @property breed Raza (opcional)
 * @property ageYears Edad en años
 * @property weightKg Peso en kilogramos (opcional)
 * @property photoRes Recurso drawable de la foto
 * @property notes Notas adicionales (opcional)
 */
data class Pet(
    val id: String,
    val ownerId: String,
    val name: String,
    val species: PetSpecies,
    val breed: String? = null,
    val ageYears: Int,
    val weightKg: Double? = null,
    val photoRes: Int? = null,
    val notes: String? = null
)

