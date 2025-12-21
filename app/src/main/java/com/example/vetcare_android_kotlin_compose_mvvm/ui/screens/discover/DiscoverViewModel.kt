package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.discover

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado UI para Discover
 */
data class DiscoverUiState(
    val stories: List<DiscoverStory> = emptyList(),
    val posts: List<DiscoverPost> = emptyList(),
    val selectedCategory: DiscoverCategory? = null,
    val isLoading: Boolean = false,
    val likedPosts: Set<String> = emptySet()
)

/**
 * ViewModel para Discover
 */
class DiscoverViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        _uiState.value = DiscoverUiState(
            stories = DiscoverMockData.stories,
            posts = DiscoverMockData.posts,
            isLoading = false
        )
    }

    fun filterByCategory(category: DiscoverCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun getFilteredPosts(): List<DiscoverPost> {
        val category = _uiState.value.selectedCategory
        return if (category == null) {
            _uiState.value.posts
        } else {
            _uiState.value.posts.filter { it.category == category }
        }
    }

    fun toggleLike(postId: String) {
        val currentLiked = _uiState.value.likedPosts.toMutableSet()
        if (currentLiked.contains(postId)) {
            currentLiked.remove(postId)
        } else {
            currentLiked.add(postId)
        }
        _uiState.value = _uiState.value.copy(likedPosts = currentLiked)
    }

    fun markStoryAsViewed(storyId: String) {
        val updatedStories = _uiState.value.stories.map { story ->
            if (story.id == storyId) story.copy(isViewed = true) else story
        }
        _uiState.value = _uiState.value.copy(stories = updatedStories)
    }

    fun refresh() {
        loadContent()
    }
}

