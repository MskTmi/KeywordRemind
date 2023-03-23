package net.msktim

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object KeywordRemindConfig : AutoSavePluginConfig("KeywordRemindConfig") {
    @Serializable // kotlinx.serialization.Serializable
    class CustomForm(
        val reply: String,
        val at: Array<String>
    )

    @ValueDescription("用户: \n- 关键词: \n-- reply: 回复内容\n-- at: \n--- 12345678\n--- XXX")
    val Config: MutableMap<String, MutableMap<String, CustomForm>> by value(
        mutableMapOf(
            "2206085248" to mutableMapOf(
                "中午了|晚上了" to CustomForm(
                    "快来恰饭",
                    arrayOf("123456", "12345678")

                ),
                "下午了" to CustomForm(
                    "快来恰晚饭",
                    arrayOf("123456", "12345678")

                )
            )

        )
    )

}