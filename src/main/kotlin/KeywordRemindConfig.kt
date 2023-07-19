package net.msktmi

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object KeywordRemindConfig : AutoSavePluginConfig("KeywordRemindConfig") {
    @Serializable // kotlinx.serialization.Serializable
    class Remind(
        val reply: String,
        val at: Array<String>
    )

    @ValueDescription("群: \n- 用户: \n-- 关键词: \n--- reply: 回复内容\n--- at（为空填''）: \n---- 12345678\n---- XXX")
    var Config: MutableMap<String, MutableMap<String, MutableMap<String, Remind>>> by value(
        mutableMapOf(
            "12345678" to mutableMapOf(
                "1226699222" to mutableMapOf(
                    "中午了|晚上了" to Remind(
                        "快来恰饭",
                        arrayOf("123456")
                    ),
                    "下午了" to Remind(
                        "快来恰晚饭",
                        arrayOf("123456", "12345678")
                    ),
                    "偷偷吃" to Remind(
                        "恰独食喽",
                        arrayOf("")
                    )
                )
            )
        )
    )
}