package com.mrsanglier.tsumegohero.game.usertsumego

data class UserTsumegoGameNavScope(
    val navigateBack: () -> Unit,
    val navigateToReview: (tsumegoId: String) -> Unit,
    val navigateToEdition: (collectionId: String, tsumegoId: String) -> Unit,
)
