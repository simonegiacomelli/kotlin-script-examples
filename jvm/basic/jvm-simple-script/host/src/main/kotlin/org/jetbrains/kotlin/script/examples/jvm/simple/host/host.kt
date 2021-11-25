/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.script.examples.jvm.simple.host

import org.jetbrains.kotlin.script.examples.jvm.simple.SimpleScript
import java.io.File
import java.nio.file.Files
import java.util.function.Supplier
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<SimpleScript> {
        jvm {
            // configure dependencies for compilation, they should contain at least the script base class and
            // its dependencies
            // variant 1: try to extract current classpath and take only a path to the specified "script.jar"
            dependenciesFromCurrentContext(
                "script" /* script library jar name (exact or without a version) */
            )
            // variant 2: try to extract current classpath and use it for the compilation without filtering
//            dependenciesFromCurrentContext(wholeClasspath = true)
            // variant 3: try to extract a classpath from a particular classloader (or Thread.contextClassLoader by default)
            // filtering as in the variat 1 is supported too
//            dependenciesFromClassloader(classLoader = SimpleScript::class.java.classLoader, wholeClasspath = true)
            // variant 4: explicit classpath
//            updateClasspath(listOf(File("/path/to/jar")))
        }
    }

    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
}

fun main(vararg args: String) {
    println(File(".").canonicalPath)
    if (args.size != 1) {
        println("usage: <app> <script file>")
    } else {
        val scriptFile = File(args[0])
        val watcher = Watch(scriptFile)
        while (true) {
            println("Executing script $scriptFile")

            val res = evalFile(scriptFile)

            res.reports.forEach {
                println(" : ${it.message} ${it.location}" + if (it.exception == null) "" else ": ${it.exception}")
            }
            if (res is ResultWithDiagnostics.Success<EvaluationResult>) {
                val value = res.value.returnValue
                println(value)
                if ( value is ResultValue.Value) {
                    val scriptResult = value.value
                    if (scriptResult is Supplier<*>) {
                        println("==== supplied ${scriptResult.get()}")
                    }
                }
            }

            watcher.waitUntilChanged()
        }
    }
}


class Watch(file: File) {
    private val path = file.toPath()
    private var state: String = newState()

    fun waitUntilChanged() {
        val old = state
        do {
            Thread.sleep(50)
            state = newState()
        } while (old == state)
    }

    private fun newState(): String {
        val size = Files.size(path)
        val mod = Files.getLastModifiedTime(path).toMillis()
        return "size:$size modifiedTime:$mod"
    }
}
