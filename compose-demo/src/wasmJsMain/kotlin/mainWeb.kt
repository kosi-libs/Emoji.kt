import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport
import org.kodein.emoji.compose.demo.App


@OptIn(ExperimentalComposeUiApi::class)
fun main() = ComposeViewport {
    App()
}
