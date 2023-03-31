package net.msktmi

import net.mamoe.mirai.console.command.*

object AddConfigCommand : CompositeCommand(KeywordRemind, "KeywordCommand", "keywordcommand", "keyword", "关键词") {

    @SubCommand("添加", "add")
    suspend fun addConfig(context: CommandSender, keyword: String, reply: String, at: String) {
        val userId = context.user?.id.toString()
        val list = at.split(",", "，").toTypedArray()
        try {
            KeywordRemindConfig.Config[userId] = (KeywordRemindConfig.Config[userId]!! + mutableMapOf(
                keyword to KeywordRemindConfig.CustomForm(
                    reply,
                    list
                )
            )) as MutableMap<String, KeywordRemindConfig.CustomForm>
            val reply = "添加成功：\n${userId}: \n${keyword}: ${reply}\nat: ${at}\n\n".dropLast(2)
            context.sendMessage(reply)
        } catch (e: Exception) {
            if (KeywordRemindConfig.Config.filter { it.key == userId }.isEmpty()) {
                //没有则新建后添加
                newStrategy(userId, keyword, reply, list)
                val reply = "已新建用户后添加：\n${userId}: \n${keyword}: ${reply}\nat: ${at}\n\n".dropLast(2)
                context.sendMessage(reply)
            }
        }
    }

    @SubCommand("删除", "remove", "del")
    suspend fun reConfig(context: CommandSender, keyword: String) {
        val userId = context.user?.id.toString()
        if (!KeywordRemindConfig.Config[userId]?.filterKeys { it == keyword }.isNullOrEmpty()) {
            KeywordRemindConfig.Config[userId]?.remove(
                KeywordRemindConfig.Config[userId]?.filterKeys {
                    keyword in it.split("|")
                }?.keys.toString()
            )
            context.sendMessage("删除'${keyword}'成功")
        } else {
            context.sendMessage("没有找到名称为'${keyword}'的提醒词")
        }
    }

    @SubCommand("list", "列表")
    suspend fun configList(context: CommandSender) {
        var list = ""
        val userId = context.user?.id

        if (userId != null) {
            val id = userId.toString()
            KeywordRemindConfig.Config[id]?.forEach {
                val at = emptyList<String>().toMutableList()
                it.value.at.forEach {
                    at += it
                }
                list += "${it.key}: ${it.value.reply}\nat: ${at}\n\n"
            }
            list = list.dropLast(2)
        } else {
            KeywordRemindConfig.Config.forEach {
                val at = emptyList<String>().toMutableList()
                val userId = it.key
                it.value.forEach{
                    it.value.at.forEach {
                        at += it
                    }
                    list += "${userId}: \n${it.key}: ${it.value.reply}\nat: ${at}\n\n"
                }
            }
            list = list.dropLast(2)
        }
        context.sendMessage(list)
    }
}

//新增一个攻略
fun newStrategy(user: String, keyword: String, reply: String, list: Array<String>) {
    var switch = true
    repeat(list.count()) {
        if (switch) {
            KeywordRemindConfig.Config[user] = mutableMapOf(
                keyword to KeywordRemindConfig.CustomForm(
                    reply,
                    list
                )
            )
            switch = false
        } else {
            KeywordRemindConfig.Config[user] = (KeywordRemindConfig.Config[user]!! + mutableMapOf(
                keyword to KeywordRemindConfig.CustomForm(
                    reply,
                    list
                )
            )) as MutableMap<String, KeywordRemindConfig.CustomForm>
        }
    }
}
