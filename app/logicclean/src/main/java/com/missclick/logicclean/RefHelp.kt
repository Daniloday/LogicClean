package com.missclick.logicclean

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import java.net.URLEncoder

class RefHelp(val i : InstallReferrerClient, val codeBack : (String?) -> Unit) : InstallReferrerStateListener {
    override fun onInstallReferrerSetupFinished(px: Int) {
        val fe = if(px == 0) URLEncoder.encode(i.installReferrer.installReferrer, "UTF-8")   else null
        i.endConnection()
        codeBack.invoke(fe)
    }

    override fun onInstallReferrerServiceDisconnected() {

    }

}