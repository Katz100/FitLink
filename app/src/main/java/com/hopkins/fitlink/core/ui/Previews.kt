package com.hopkins.fitlink.core.ui

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
annotation class TabletPreview

@Preview(
    name = "LandscapePhoneDark",
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape,cutout=none,navigation=gesture"
)
annotation class LandscapePhoneDarkPreview

@Preview(
    name = "PhonePortrait",
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=portrait,cutout=none,navigation=gesture"
)
annotation class PortraitPhonePreview
