package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla Discover con Stories y Feed - Estilo Premium
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredPosts = viewModel.getFilteredPosts()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        ActivityLogger.log(
            screen = ActivityLogger.Screens.DISCOVER,
            action = ActivityLogger.Actions.VIEW
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VetCareColors.Background)
    ) {
        // Header
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { -30 }
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = VetCareColors.OnBackground,
                    modifier = Modifier.padding(
                        horizontal = VetCareSpacing.md,
                        vertical = VetCareSpacing.sm
                    )
                )
            }
        }

        // Stories
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { -20 }
            ) {
                StoriesRowPremium(
                    stories = uiState.stories,
                    onStoryClick = { story ->
                        viewModel.markStoryAsViewed(story.id)
                        ActivityLogger.log(
                            screen = ActivityLogger.Screens.DISCOVER,
                            action = ActivityLogger.Actions.CLICK,
                            metadata = mapOf("story_id" to story.id)
                        )
                    }
                )
            }
        }

        // Category filters
        item {
            CategoryFiltersPremium(
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = { category ->
                    viewModel.filterByCategory(category)
                }
            )
        }

        // Feed
        items(
            items = filteredPosts,
            key = { it.id }
        ) { post ->
            FeedCardPremium(
                post = post,
                isLiked = uiState.likedPosts.contains(post.id),
                onLikeClick = { viewModel.toggleLike(post.id) },
                onCommentClick = { },
                onBookmarkClick = { }
            )
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun StoriesRowPremium(
    stories: List<DiscoverStory>,
    onStoryClick: (DiscoverStory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = VetCareSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md),
        contentPadding = PaddingValues(horizontal = VetCareSpacing.md)
    ) {
        // Add story button (You)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(VetCareColors.Primary.copy(alpha = 0.1f))
                        .border(2.dp, VetCareColors.Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Add story",
                        tint = VetCareColors.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(VetCareSpacing.xxs))
                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.Primary
                )
            }
        }

        items(stories) { story ->
            StoryAvatarPremium(
                story = story,
                onClick = { onStoryClick(story) }
            )
        }
    }
}

@Composable
private fun StoryAvatarPremium(
    story: DiscoverStory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(
                    brush = if (story.isViewed) {
                        Brush.linearGradient(
                            listOf(VetCareColors.MutedText, VetCareColors.MutedText)
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFFFFE66D),
                                Color(0xFF4ECDC4)
                            )
                        )
                    },
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(VetCareColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Avatar con imagen de mascota simulada
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = story.name,
                    modifier = Modifier.size(32.dp),
                    tint = VetCareColors.MutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(VetCareSpacing.xxs))

        Text(
            text = story.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (story.isViewed) VetCareColors.MutedText else VetCareColors.OnBackground,
            maxLines = 1
        )
    }
}

@Composable
private fun CategoryFiltersPremium(
    selectedCategory: DiscoverCategory?,
    onCategorySelect: (DiscoverCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = VetCareSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs),
        contentPadding = PaddingValues(horizontal = VetCareSpacing.md)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelect(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VetCareColors.Primary,
                    selectedLabelColor = Color.White
                )
            )
        }

        items(DiscoverCategory.entries) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelect(if (selectedCategory == category) null else category) },
                label = { Text(category.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VetCareColors.Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun FeedCardPremium(
    post: DiscoverPost,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.sm),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VetCareSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VetCareColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = VetCareColors.Primary
                    )
                }

                Spacer(modifier = Modifier.width(VetCareSpacing.sm))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = VetCareColors.OnSurface
                    )
                    Text(
                        text = post.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.MutedText
                    )
                }

                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = VetCareColors.MutedText
                    )
                }
            }

            // Descripción
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.OnSurface,
                modifier = Modifier.padding(horizontal = VetCareSpacing.md),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Imagen grande con color de categoría
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = VetCareSpacing.md)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (post.category) {
                            DiscoverCategory.HEALTH -> Color(0xFFE8F5E9)
                            DiscoverCategory.NUTRITION -> Color(0xFFFFF3E0)
                            DiscoverCategory.GROOMING -> Color(0xFFE3F2FD)
                            DiscoverCategory.FUN -> Color(0xFFFCE4EC)
                            DiscoverCategory.TIP -> Color(0xFFF3E5F5)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Simulación de imagen de mascota
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = when (post.category) {
                        DiscoverCategory.HEALTH -> Color(0xFF4CAF50)
                        DiscoverCategory.NUTRITION -> Color(0xFFFF9800)
                        DiscoverCategory.GROOMING -> Color(0xFF2196F3)
                        DiscoverCategory.FUN -> Color(0xFFE91E63)
                        DiscoverCategory.TIP -> Color(0xFF9C27B0)
                    }.copy(alpha = 0.5f)
                )
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VetCareSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.lg)
                ) {
                    // Like
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xxs),
                        modifier = Modifier.clickable(onClick = onLikeClick)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) VetCareColors.Danger else VetCareColors.MutedText,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "${post.likes + (if (isLiked) 1 else 0)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = VetCareColors.MutedText
                        )
                    }

                    // Comment
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xxs),
                        modifier = Modifier.clickable(onClick = onCommentClick)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = VetCareColors.MutedText,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "${post.comments}",
                            style = MaterialTheme.typography.labelMedium,
                            color = VetCareColors.MutedText
                        )
                    }
                }

                // Category badge
                Surface(
                    color = VetCareColors.Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = post.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.Primary,
                        modifier = Modifier.padding(horizontal = VetCareSpacing.sm, vertical = VetCareSpacing.xxs)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscoverScreenPreview() {
    VetCareTheme {
        DiscoverScreen()
    }
}

