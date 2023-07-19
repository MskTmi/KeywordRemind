package net.msktmi

import net.mamoe.mirai.console.command.*

object AddConfigCommand : CompositeCommand(KeywordRemind, "KeywordCommand", "keywordcommand", "keyword", "关键词") {

    @SubCommand("添加", "add")
    suspend fun addConfig(context: CommandSender, keyword: String, reply: String, at: String = "") {
        var groupId = context.subject?.id.toString()
        val userId = context.user?.id.toString()
        val list = at.split(",", "，").toTypedArray()

        if (KeywordRemindConfig.Config.filter { it.key == groupId }.isEmpty()) {
            //新建群内容与用户信息后添加提醒词
            KeywordRemindConfig.Config = KeywordRemindConfig.Config.plus(
                mutableMapOf(
                    groupId to mutableMapOf(
                        userId to mutableMapOf(
                            keyword to KeywordRemindConfig.Remind(
                                reply,
                                list
                            )
                        )
                    )
                )
            ) as MutableMap<String, MutableMap<String, MutableMap<String, KeywordRemindConfig.Remind>>>

        } else if (KeywordRemindConfig.Config[groupId]!!.filter { it.key == userId }.isEmpty()) {
            //新建用户信息后添加提醒词
            KeywordRemindConfig.Config[groupId] = KeywordRemindConfig.Config[groupId]?.plus(
                mutableMapOf(
                    userId to mutableMapOf(
                        keyword to KeywordRemindConfig.Remind(
                            reply,
                            list
                        )
                    )
                )
            ) as MutableMap<String, MutableMap<String, KeywordRemindConfig.Remind>>

        } else {
            //添加提醒词
            KeywordRemindConfig.Config[groupId]!![userId] = (KeywordRemindConfig.Config[groupId]!![userId]?.plus(
                mutableMapOf(
                    keyword to KeywordRemindConfig.Remind(
                        reply,
                        list
                    )
                )
            )) as MutableMap<String, KeywordRemindConfig.Remind>
        }

        val replyMessage = "添加成功：\n${userId}: \n${keyword}: ${reply}\nat: ${at}\n\n".dropLast(2)
        context.sendMessage(replyMessage)
    }

    @SubCommand("删除", "remove", "del")
    suspend fun reConfig(context: CommandSender, keyword: String) {
        var groupId = context.subject?.id.toString()
        val userId = context.user?.id.toString()

        if (!KeywordRemindConfig.Config[groupId]!![userId]?.filterKeys { it == keyword }.isNullOrEmpty()) {
            KeywordRemindConfig.Config[groupId]!![userId]?.remove(keyword)
            context.sendMessage("删除'${keyword}'成功")
        } else {
            context.sendMessage("没有找到名称为'${keyword}'的提醒词")
        }
    }

    @SubCommand("list", "列表")
    suspend fun configList(context: CommandSender) {
        var list = ""
        var groupId = context.subject?.id.toString()
        val userId = context.user?.id
        //控制台输入
        if (userId != null) {
            val id = userId.toString()
            val groupRemind = KeywordRemindConfig.Config[groupId]!!
            groupRemind[id]?.forEach {
                var at = emptyList<String>().toMutableList()
                it.value.at.forEach {
                    at.add(it)
                }
                if (at.isEmpty()){
                    at[0] = "无"
                }
                list += "${it.key}: ${it.value.reply}\nat: ${at}\n\n"
            }
            list = list.dropLast(2)

        } else {
            KeywordRemindConfig.Config.forEach {
                val theGroupId = it.key
                KeywordRemindConfig.Config[it.key]?.forEach {
                    val theUserId = it.key
                    list += "${theGroupId}: \n"
                    it.value.forEach {
                        val at = emptyList<String>().toMutableList()

                        it.value.at.forEach {
                            at += it
                        }
                        list += "- ${theUserId}: \n-- ${it.key}: ${it.value.reply}\n-- at: ${at}\n\n"
                    }
                }
            }
            list = list.dropLast(2)
        }
        if(list.isNotEmpty()){
            context.sendMessage(list)
        }else {
            context.sendMessage("无关键词提醒")
        }
    }
}
