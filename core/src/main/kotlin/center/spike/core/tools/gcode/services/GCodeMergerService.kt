package center.spike.core.tools.gcode.services

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class GCodeMergerService {
    fun mergeGCodeFiles(gcodeFiles: List<String>): String {
        var finalString = ""

        for (i in gcodeFiles.indices) {
            val file = gcodeFiles[i]
            val content = file.lines().toMutableList()

            val filteredContent = if (i != 0) {
                // find line with ( Setup )
                val setupIndex = content.indexOfFirst { it.contains("( Setup )") }
                if (setupIndex != -1) {
                    // exclude line with ( Setup ) and everything before it
                    content.subList(setupIndex + 1, content.size).joinToString("\n")
                } else {
                    // if no ( Setup ) found, include the whole content
                    content.joinToString("\n")
                }
            } else {
                // include entire content for first file
                content.joinToString("\n")
            }

            // if its not the last truncate the last two lines
            if (i != gcodeFiles.indices.last) {
                val lines = filteredContent.lines()
                val truncatedContent = if (lines.size > 2) {
                    lines.subList(0, lines.size - 2).joinToString("\n")
                } else {
                    ""
                }

                val finalContent = truncatedContent + "\n"
                finalString += finalContent
            } else {
                val finalContent = filteredContent + "\n"
                finalString += finalContent
            }

            if (i != gcodeFiles.indices.last)
                finalString += "\n( Job $i End )\n\n"
        }

        return finalString
    }
}