/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.script.examples.jvm.jsr223.mainKts

import javax.script.ScriptEngineManager

fun main(vararg args: String) {
    val engine = ScriptEngineManager().getEngineByExtension("main.kts")!!

    val commands = sequence {
        while (true){
            print("> ")
            val line = readLine() ?: return@sequence
            yield(line)
        }

    }

    commands.forEach {
        try {
            val res = engine.eval(it)
            println(res)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Thread.sleep(100)
        }
    }
}
