package com.teamwizardry.librarianlib.features.autoregister

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.kotlin.singletonInstance
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.PrintWriter
import java.io.StringWriter



/**
 * Created by TheCodeWarrior
 */
abstract class AnnotationMarkerProcessor<A : Annotation, T : Any>(val annotationClass: Class<A>, vararg val assertSuperclass: Class<T>) {

    fun isClassValid(clazz: Class<T>) : Boolean {
        return assertSuperclass.isEmpty() || assertSuperclass.any { it.isAssignableFrom(clazz) }
    }

    abstract fun process(clazz: Class<T>, annotation: A)

}

object AnnotationMarkersHandler {

    val processors = mutableListOf<AnnotationMarkerProcessor<*, *>>()
    val errors = mutableMapOf<Class<*>, Multimap<String, Class<*>>>().withRealDefault { HashMultimap.create() }

    fun preInit(e: FMLPreInitializationEvent) {
        val data = e.asmData
        handle(GetProcessors, data)

        processors.toMutableList().forEach {
            handle(it, data)
        }

        if (errors.isNotEmpty()) {
            val build = mutableListOf("AnnotationMarker Errors:")

            for ((annot, map) in errors) {
                build.add("-+ Errors for @${annot.typeName}")
                for((error, affected) in map.asMap()) {
                    var errorList = error.split("\\r\\n|\\n|\\r")
                    if(errorList.isEmpty())
                        errorList = listOf("<<ERR: Empty Stacktrace!>>")
                    build.add(" |-+ Affected classes:")
                    affected.forEach {
                        build.add(" | |-+ ${it.canonicalName}")
                    }
                    build.add(" |-+ Stacktrace:")
                    errorList.forEach {
                        build.add(" | | $it")
                    }
                }
            }
            LibrarianLog.bigDie("FATAL: AnnotationMarkers failed!!", build)
        }
    }

    fun <A: Annotation, T: Any> handle(processor: AnnotationMarkerProcessor<A, T>, asmDataTable: ASMDataTable) {
        val annotationClass = processor.annotationClass

        val annotationClassName = annotationClass.canonicalName
        val asmDatas = asmDataTable.getAll(annotationClassName)
        for (asmData in asmDatas.sortedBy { it.className }) {
            val clazz = try {
                Class.forName(asmData.className)
            } catch (e: ClassNotFoundException) {
                LibrarianLog.error(e, "Marked class ${asmData.className} not found!")
                null
            }

            if(clazz != null) {
                @Suppress("UNCHECKED_CAST")
                clazz as Class<T>
                if(!processor.isClassValid(clazz)) {
                    LibrarianLog.error("Class ${clazz.canonicalName} annotated with ${annotationClass.typeName} is invalid")
                }
                val annot = clazz.getAnnotation(annotationClass)
                try {
                    processor.process(clazz, annot)
                } catch (e: Throwable) {
                    val writer = StringWriter()
                    e.printStackTrace(PrintWriter(writer))
                    val str = errors.toString()
                    errors[annotationClass].put(str, clazz)
                }
            }
        }
    }

    object GetProcessors : AnnotationMarkerProcessor<AMPRegister, AnnotationMarkerProcessor<*, *>>(AMPRegister::class.java, AnnotationMarkerProcessor::class.java) {
        override fun process(clazz: Class<AnnotationMarkerProcessor<*, *>>, annotation: AMPRegister) {
            AnnotationMarkersHandler.processors.add(clazz.singletonInstance ?: throw RuntimeException("No singleton instance!"))
        }
    }
}
