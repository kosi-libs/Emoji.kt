package org.kodein.emoji.compose.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kodein.emoji.Emoji
import org.kodein.emoji.compose.NotoAnimatedEmoji
import org.kodein.emoji.compose.WithNotoAnimatedEmoji
import org.kodein.emoji.compose.WithPlatformEmoji
import org.kodein.emoji.compose.withEmoji
import org.kodein.emoji.mediumLight_mediumDark
import org.kodein.emoji.people_body.family.PeopleHoldingHands
import org.kodein.emoji.smileys_emotion.emotion.Collision
import org.kodein.emoji.smileys_emotion.face_negative.ImpSmile
import org.kodein.emoji.smileys_emotion.face_smiling.Smile
import org.kodein.emoji.smileys_emotion.heart.RedHeart


@Composable
fun App() {
    SelectionContainer {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
//            ProvideTextStyle(TextStyle(fontSize = 32.sp)) {
//                WithPlatformEmoji(
//                    "Platform:\nWhen I see :people-holding-hands~medium-light,medium-dark:, my <3 goes :collision: :D!".withEmoji()
//                ) { text, inlineContent ->
//                    Text(text = text, inlineContent = inlineContent)
//                }
//
//                WithNotoAnimatedEmoji(
//                    "Animated:\nWhen I see ${Emoji.PeopleHoldingHands.mediumLight_mediumDark}, my ${Emoji.RedHeart} goes ${Emoji.Collision} ${Emoji.Smile}!"
//                ) { text, inlineContent ->
//                    Text(text = text, inlineContent = inlineContent)
//                }
//            }
            NotoAnimatedEmoji(
                emoji = Emoji.ImpSmile,
                modifier = Modifier.size(128.dp),
                iterations = 2,
                stopAt = 0.76f
            )
        }
    }
}
