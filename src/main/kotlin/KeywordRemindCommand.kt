package net.msktim

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
            context.sendMessage("添加成功：" + KeywordRemindConfig.Config.filter { it.key == userId }
                .toString())
        } catch (e: Exception) {
            if (KeywordRemindConfig.Config.filter { it.key == userId }.isEmpty()) {
                //没有则新建后添加
                newStrategy(userId, keyword, reply, list)
                context.sendMessage("没有找到名称为'${keyword}'的集合\n已新建后添加：" + KeywordRemindConfig.Config.filter { it.key == keyword }
                    .toString())
            }
        }
    }

    @SubCommand("删除", "remove")
    suspend fun reConfig(context: CommandSender, keyword: String) {
        val userId = context.user?.id.toString()
        context.sendMessage(KeywordRemindConfig.Config.filter { it.key == userId }.values.filter { it.keys.equals(keyword)}.toString())
        if (KeywordRemindConfig.Config.filter { it.key == userId }.filter { it.key ==  keyword}.isNotEmpty()) {
            KeywordRemindConfig.Config[userId]?.remove(keyword)
            context.sendMessage("删除'${keyword}'成功")
        } else {
            context.sendMessage("没有找到名称为'${keyword}'的集合")
        }
    }

    @SubCommand("list", "列表")
    suspend fun configList(context: CommandSender) {
        var list = ""
        KeywordRemindConfig.Config.forEach {
            list += "$it\n\n"
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
