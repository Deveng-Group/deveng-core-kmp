package core.util.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText

/**
 * Renders a markdown string as simple rich text lines.
 * Supports headings (#, ##, ###), list items (-), bold (**text**) and italic (_text_).
 *
 * @param markdownContent Raw markdown content to render.
 */
@Composable
fun MarkdownContentParser(markdownContent: String) {
    val lines = markdownContent.lines()

    Column {
        lines.forEach { line ->
            BasicText(text = parseMarkdownLine(line = line))
        }
    }
}

private fun parseMarkdownLine(line: String): AnnotatedString {
    return buildAnnotatedString {
        when {
            line.startsWith("# ") -> {
                val content = line.removePrefix("# ")
                append(content)
                addStyle(
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold).toSpanStyle(),
                    start = 0,
                    end = content.length
                )
            }

            line.startsWith("## ") -> {
                val content = line.removePrefix("## ")
                append(content)
                addStyle(
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold).toSpanStyle(),
                    start = 0,
                    end = content.length
                )
            }

            line.startsWith("### ") -> {
                val content = line.removePrefix("### ")
                append(content)
                addStyle(
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium).toSpanStyle(),
                    start = 0,
                    end = content.length
                )
            }

            line.startsWith("- ") -> {
                append("• ")
                appendInlineMarkdownStyles(
                    content = line.removePrefix("- "),
                    builder = this
                )
            }

            else -> {
                appendInlineMarkdownStyles(content = line, builder = this)
            }
        }
    }
}

private fun appendInlineMarkdownStyles(
    content: String,
    builder: AnnotatedString.Builder
) {
    val inlinePattern = Regex("""(\*\*.+?\*\*|_.+?_)""")
    var currentIndex = 0

    inlinePattern.findAll(content).forEach { matchResult ->
        val start = matchResult.range.first
        val endExclusive = matchResult.range.last + 1

        if (currentIndex < start) {
            builder.append(content.substring(currentIndex, start))
        }

        val token = matchResult.value
        when {
            token.startsWith("**") && token.endsWith("**") && token.length > 4 -> {
                val boldText = token.substring(2, token.length - 2)
                builder.appendWithStyle(
                    text = boldText,
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                )
            }

            token.startsWith("_") && token.endsWith("_") && token.length > 2 -> {
                val italicText = token.substring(1, token.length - 1)
                builder.appendWithStyle(
                    text = italicText,
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                )
            }

            else -> builder.append(token)
        }

        currentIndex = endExclusive
    }

    if (currentIndex < content.length) {
        builder.append(content.substring(currentIndex))
    }
}

private fun AnnotatedString.Builder.appendWithStyle(
    text: String,
    style: SpanStyle
) {
    val start = length
    append(text)
    addStyle(style = style, start = start, end = length)
}
