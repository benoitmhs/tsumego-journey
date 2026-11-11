package com.mrsanglier.tsumegohero.collection.list

import androidx.compose.runtime.Composable
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_fab_import
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_importCollection_title
import com.mrsanglier.tsumegohero.app.coreui.resources.collection_newCollection_placeholder
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_import
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.TextInputModalBottomSheet
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString

/** Name of the collection created from an archive, prefilled with [initialName] (the file name). */
internal class ImportCollectionBottomSheet(
    private val initialName: String,
    private val onImport: (name: String) -> Unit,
    override val onDismiss: () -> Unit,
) : THModalBottomSheetState {

    @Composable
    override fun Content(hideBottomSheet: () -> Unit) {
        TextInputModalBottomSheet(
            headerIcon = THDrawable.ic_import.toIconSpec(),
            headerText = THString.collection_importCollection_title.toTextSpec(),
            buttonText = THString.collection_fab_import.toTextSpec(),
            textFieldPlaceholder = THString.collection_newCollection_placeholder.toTextSpec(),
            initialValue = initialName,
            requestFocus = true,
            onValidate = onImport,
        )
    }
}
