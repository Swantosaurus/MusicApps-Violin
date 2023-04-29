package com.kobera.music.common.notes.sheet.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * stores path and center offset (for drawing on canvas)
 */
data class PathAndCenterOffset(val path: Path, val centerOffset: Offset)
