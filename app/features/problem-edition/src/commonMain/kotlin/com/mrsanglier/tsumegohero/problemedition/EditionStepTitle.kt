package com.mrsanglier.tsumegohero.problemedition

import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_step_board_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_step_info_title
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_step_solutions_title
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString

internal val EditionStep.title: TextSpec
    get() = when (this) {
        EditionStep.Board -> THString.tsumegoEdition_step_board_title
        EditionStep.Solutions -> THString.tsumegoEdition_step_solutions_title
        EditionStep.Info -> THString.tsumegoEdition_step_info_title
    }.toTextSpec()
