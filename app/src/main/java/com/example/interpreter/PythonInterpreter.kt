package com.example.interpreter

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.math.*

object PythonInterpreter {

    data class ExecutionResult(
        val stdout: String,
        val stderr: String,
        val executionTimeMs: Long,
        val exitCode: Int
    )

    fun execute(code: String): ExecutionResult {
        val startTime = System.currentTimeMillis()
        val stdoutBuffer = StringBuilder()
        val stderrBuffer = StringBuilder()

        try {
            val lines = code.lines()
            val variables = mutableMapOf<String, Any>()
            val functions = mutableMapOf<String, List<String>>()

            var lineIndex = 0
            while (lineIndex < lines.size) {
                val rawLine = lines[lineIndex]
                val trimmed = rawLine.trim()

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    lineIndex++
                    continue
                }

                if (trimmed.startsWith("print(") && trimmed.endsWith(")")) {
                    val innerContent = trimmed.substring(6, trimmed.length - 1)
                    val output = evaluatePrintExpression(innerContent, variables)
                    stdoutBuffer.append(output).append("\n")
                } else if (trimmed.contains("=") && !trimmed.startsWith("if ") && !trimmed.startsWith("while ")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        val varName = parts[0].trim()
                        val varExpr = parts[1].trim()
                        val value = evaluateExpression(varExpr, variables)
                        variables[varName] = value
                    }
                } else if (trimmed.startsWith("def ")) {
                    val defLine = trimmed.substring(4)
                    val funcName = defLine.substringBefore("(").trim()
                    // Collect function body
                    val funcBody = mutableListOf<String>()
                    lineIndex++
                    while (lineIndex < lines.size && (lines[lineIndex].startsWith("    ") || lines[lineIndex].startsWith("\t") || lines[lineIndex].isBlank())) {
                        if (lines[lineIndex].isNotBlank()) {
                            funcBody.add(lines[lineIndex].trim())
                        }
                        lineIndex++
                    }
                    functions[funcName] = funcBody
                    continue
                } else if (trimmed.startsWith("for ") && trimmed.contains(" in ")) {
                    // Basic loop handling e.g. for i in range(5):
                    val loopHeader = trimmed.removeSuffix(":")
                    val varName = loopHeader.substringAfter("for ").substringBefore(" in ").trim()
                    val rangeExpr = loopHeader.substringAfter(" in ").trim()
                    
                    val loopBody = mutableListOf<String>()
                    lineIndex++
                    while (lineIndex < lines.size && (lines[lineIndex].startsWith("    ") || lines[lineIndex].startsWith("\t") || lines[lineIndex].isBlank())) {
                        if (lines[lineIndex].isNotBlank()) {
                            loopBody.add(lines[lineIndex].trim())
                        }
                        lineIndex++
                    }

                    val rangeCount = parseRange(rangeExpr, variables)
                    for (i in rangeCount) {
                        variables[varName] = i
                        for (bodyLine in loopBody) {
                            if (bodyLine.startsWith("print(") && bodyLine.endsWith(")")) {
                                val inner = bodyLine.substring(6, bodyLine.length - 1)
                                stdoutBuffer.append(evaluatePrintExpression(inner, variables)).append("\n")
                            }
                        }
                    }
                    continue
                } else {
                    // Fallback for function call or raw expression
                    evaluateExpression(trimmed, variables)
                }

                lineIndex++
            }

            if (stdoutBuffer.isEmpty() && stderrBuffer.isEmpty()) {
                stdoutBuffer.append("Script executed cleanly with no printed output.")
            }

            val endTime = System.currentTimeMillis()
            return ExecutionResult(
                stdout = stdoutBuffer.toString().trimEnd(),
                stderr = stderrBuffer.toString().trimEnd(),
                executionTimeMs = endTime - startTime,
                exitCode = 0
            )

        } catch (e: Exception) {
            val endTime = System.currentTimeMillis()
            return ExecutionResult(
                stdout = stdoutBuffer.toString().trimEnd(),
                stderr = "Traceback (most recent call last):\n  RuntimeError: ${e.message ?: "Evaluation exception"}",
                executionTimeMs = endTime - startTime,
                exitCode = 1
            )
        }
    }

    private fun evaluatePrintExpression(expr: String, vars: Map<String, Any>): String {
        var text = expr.trim()
        
        // Handle f-strings e.g. f"Fibonacci: {val}" or f'...'
        if ((text.startsWith("f\"") || text.startsWith("f'")) && (text.endsWith("\"") || text.endsWith("'"))) {
            val raw = text.substring(2, text.length - 1)
            var result = raw
            val regex = Regex("\\{([^}]+)\\}")
            regex.findAll(raw).forEach { match ->
                val varExpr = match.groupValues[1]
                val evalVal = evaluateExpression(varExpr, vars)
                result = result.replace(match.value, evalVal.toString())
            }
            return result
        }

        // Standard string literal "..." or '...'
        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
            return text.substring(1, text.length - 1)
        }

        // Check if expression in variables
        return evaluateExpression(text, vars).toString()
    }

    private fun evaluateExpression(expr: String, vars: Map<String, Any>): Any {
        val trimmed = expr.trim()

        if (vars.containsKey(trimmed)) {
            return vars[trimmed]!!
        }

        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length - 1)
        }

        if (trimmed.toIntOrNull() != null) return trimmed.toInt()
        if (trimmed.toDoubleOrNull() != null) return trimmed.toDouble()
        if (trimmed == "True") return true
        if (trimmed == "False") return false

        // Lists e.g. [1, 2, 3]
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            val itemsStr = trimmed.substring(1, trimmed.length - 1)
            if (itemsStr.isBlank()) return emptyList<Any>()
            return itemsStr.split(",").map { evaluateExpression(it.trim(), vars) }
        }

        // Basic Math operations: e.g. sum([1,2,3]) or math.sqrt(16)
        if (trimmed.startsWith("math.sqrt(") && trimmed.endsWith(")")) {
            val arg = evaluateExpression(trimmed.substring(10, trimmed.length - 1), vars).toString().toDouble()
            return sqrt(arg)
        }

        if (trimmed.startsWith("sum(") && trimmed.endsWith(")")) {
            val argVal = evaluateExpression(trimmed.substring(4, trimmed.length - 1), vars)
            if (argVal is List<*>) {
                return argVal.mapNotNull { it.toString().toDoubleOrNull() }.sum()
            }
        }

        if (trimmed.startsWith("len(") && trimmed.endsWith(")")) {
            val argVal = evaluateExpression(trimmed.substring(4, trimmed.length - 1), vars)
            if (argVal is List<*>) return argVal.size
            if (argVal is String) return argVal.length
        }

        return trimmed
    }

    private fun parseRange(expr: String, vars: Map<String, Any>): List<Int> {
        if (expr.startsWith("range(") && expr.endsWith(")")) {
            val argsStr = expr.substring(6, expr.length - 1)
            val parts = argsStr.split(",").map { it.trim() }
            if (parts.size == 1) {
                val end = evaluateExpression(parts[0], vars).toString().toIntOrNull() ?: 5
                return (0 until end).toList()
            } else if (parts.size == 2) {
                val start = evaluateExpression(parts[0], vars).toString().toIntOrNull() ?: 0
                val end = evaluateExpression(parts[1], vars).toString().toIntOrNull() ?: 5
                return (start until end).toList()
            }
        }
        return (0 until 5).toList()
    }

    class ReplSession {
        val variables = mutableMapOf<String, Any>("__name__" to "__main__", "version" to "3.11.0")
        val functions = mutableMapOf<String, List<String>>()
        val history = mutableListOf<String>()

        fun evaluate(line: String): ExecutionResult {
            val startTime = System.currentTimeMillis()
            val stdoutBuffer = StringBuilder()
            val stderrBuffer = StringBuilder()
            val trimmed = line.trim()

            if (trimmed.isNotBlank()) {
                history.add(line)
            }

            try {
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    return ExecutionResult("", "", 0, 0)
                }

                if (trimmed.startsWith("print(") && trimmed.endsWith(")")) {
                    val innerContent = trimmed.substring(6, trimmed.length - 1)
                    val output = evaluatePrintExpression(innerContent, variables)
                    stdoutBuffer.append(output)
                } else if (trimmed.contains("=") && !trimmed.startsWith("if ") && !trimmed.startsWith("while ") && !trimmed.contains("==")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        val varName = parts[0].trim()
                        val varExpr = parts[1].trim()
                        val value = evaluateExpression(varExpr, variables)
                        variables[varName] = value
                    }
                } else if (trimmed.startsWith("def ")) {
                    val defLine = trimmed.substring(4)
                    val funcName = defLine.substringBefore("(").trim()
                    functions[funcName] = listOf("pass")
                    stdoutBuffer.append("<function $funcName at 0x7f8a91b2>")
                } else if (trimmed.startsWith("for ") && trimmed.contains(" in ")) {
                    val loopHeader = trimmed.removeSuffix(":")
                    val varName = loopHeader.substringAfter("for ").substringBefore(" in ").trim()
                    val rangeExpr = loopHeader.substringAfter(" in ").trim()
                    val rangeCount = parseRange(rangeExpr, variables)
                    stdoutBuffer.append("Ran loop for $varName in ${rangeCount.size} items")
                } else {
                    val result = evaluateExpression(trimmed, variables)
                    stdoutBuffer.append(result.toString())
                }

                val endTime = System.currentTimeMillis()
                return ExecutionResult(
                    stdout = stdoutBuffer.toString().trimEnd(),
                    stderr = stderrBuffer.toString().trimEnd(),
                    executionTimeMs = endTime - startTime,
                    exitCode = 0
                )
            } catch (e: Exception) {
                val endTime = System.currentTimeMillis()
                return ExecutionResult(
                    stdout = stdoutBuffer.toString().trimEnd(),
                    stderr = "NameError/SyntaxError: ${e.message ?: "Evaluation exception"}",
                    executionTimeMs = endTime - startTime,
                    exitCode = 1
                )
            }
        }

        fun loadScript(code: String): ExecutionResult {
            val result = execute(code)
            return result
        }

        fun reset() {
            variables.clear()
            variables["__name__"] = "__main__"
            variables["version"] = "3.11.0"
            functions.clear()
            history.clear()
        }
    }

    fun createSession(): ReplSession = ReplSession()
}
