package com.mrsanglier.tsumegohero.collection.list

import androidx.compose.runtime.Composable
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_fab_newCollection
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_newCollection_create
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_newCollection_placeholder
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_library_add
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.TextInputModalBottomSheet
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString

internal class NewCollectionBottomSheet(
    private val onCreate: (name: String) -> Unit,
    override val onDismiss: () -> Unit,
) : THModalBottomSheetState {

    @Composable
    override fun Content(hideBottomSheet: () -> Unit) {
        TextInputModalBottomSheet(
            headerIcon = THDrawable.ic_library_add.toIconSpec(),
            headerText = THString.collection_fab_newCollection.toTextSpec(),
            buttonText = THString.collection_newCollection_create.toTextSpec(),
            textFieldPlaceholder = THString.collection_newCollection_placeholder.toTextSpec(),
            onValidate = onCreate,
        )
    }
}
