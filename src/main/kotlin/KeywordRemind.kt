package net.msktmi

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
        id = "net.msktmi.keyword-remind",
        name = "Keyword Remind",
        version = "1.0.0",
    ) {
        author("MskTmi")
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
                val reply = KeywordRemindConfig.Config[this.sender.id.toString()]!!.filterKeys {
                    message.contentToString() == it
                } + KeywordRemindConfig.Config[this.sender.id.toString()]!!.filterKeys {
                    message.contentToString() in it.split("|")
                }
                //重优先回复无分隔符的内容
                if (reply.isNotEmpty()) {
                    val firstReply = reply.map { it.value }[0]
                    val chain = buildMessageChain {
                        +PlainText(firstReply.reply)
                        firstReply.at.forEach {
                            +At(it.toLong())
                        }
                    }
                    group.sendMessage(chain)
                }
            }
            return@subscribeAlways
        }
    }
}