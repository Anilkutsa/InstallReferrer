package com.sheldononpot.mydemoapp

data class InstallEvent(val referrerExists: Boolean) {
    var utmSource: String? = null
    var utmMedium: String? = null
    var utmTerm: String? = null
    var utmContent: String? = null
    var utmCampaign: String? = null
}