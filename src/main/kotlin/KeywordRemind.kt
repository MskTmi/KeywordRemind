package net.msktim

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info

object KeywordRemind : KotlinPlugin(
    JvmPluginDescription(
        id = "net.msktim.keyword-remind",
        name = "Keyword Remind",
        version = "0.1.0",
    ) {
        author("MskTim")
    }
) {
    override fun onEnable() {
        logger.info { "关键词提醒已开启~" }
        //注册指令
        CommandManager.registerCommand(AddConfigCommand)
        //启用配置文件
        KeywordRemindConfig.reload()
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent> {
            if (KeywordRemindConfig.Config.any { it.key.contains(this.sender.id.toString()) }) {
                val reply = KeywordRemindConfig.Config[this.sender.id.toString()]!!.filter {
                    message.contentToString() in it.key.split("|")
                }

                if (reply.isNotEmpty()) {
                    val chain = buildMessageChain {
                        +PlainText(reply.values.map { it.reply }[0])
                        reply.values.map {
                            it.at.forEach {
                                +At(it.toLong())
                            }
                        }
                    }
                    group.sendMessage(chain)
                }
            }
            return@subscribeAlways
        }
    }
}