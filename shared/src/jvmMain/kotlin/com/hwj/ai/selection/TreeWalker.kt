package com.hwj.ai.selection

import mmarquee.automation.AutomationTreeWalker
import mmarquee.automation.AutomationTreeWalker.AutomationElementVisitor
import mmarquee.automation.Element
import mmarquee.automation.UIAutomation

class TreeWalker {


    private val recurseLevel: Int = 50


    fun v(automation: UIAutomation, call: (String) -> Unit) {
        try {

            val walker = automation.controlViewWalker
//            val rootE = automation.rootElement //直接加载系统根。。
            val rootE = automation.focusedElement
            val logVisitor = object : AutomationElementVisitor {
                var level = 0
                override fun visit(walker: AutomationTreeWalker?, element: Element?): Boolean {
                    val name = element?.name
                    val className = element?.className
                    val indent = if (level == 0) "" else String.format("%" + level * 2 + "s", "")
                    val message = String.format(
                        "%s'%s' [%s]",
                        indent,
                        name,
                        className
                    )

                    println(message)
                    if (!name.isNullOrEmpty()) {
                        call(name)
                    }

                    if (recurseLevel > level) {
                        level++
                        walker!!.walk(this, element)
                        level--
                    }
                    return true
                }
            }

            walker.walk(logVisitor, rootE)
            println("All Done>")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}