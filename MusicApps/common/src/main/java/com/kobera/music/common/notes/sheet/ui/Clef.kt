package com.kobera.music.common.notes.sheet.ui

import androidx.annotation.DrawableRes
import com.kobera.music.common.R

enum class Clef(@DrawableRes val resource: Int) {
    Violin(R.drawable.violin_key),
    //TODO
    Bass(R.drawable.violin_key),
    Viola(R.drawable.violin_key);
}