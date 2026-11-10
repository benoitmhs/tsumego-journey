package com.mrsanglier.tsumegohero.domain.collection.model

import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollection

data class CollectionDetail(
    val collection: TsumegoCollection,
    val problems: List<UserTsumegoPreview>,
)
