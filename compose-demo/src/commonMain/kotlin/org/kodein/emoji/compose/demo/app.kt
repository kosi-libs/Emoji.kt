package org.kodein.emoji.compose.demo

import androidx.compose.foundation.layout.*
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
import org.kodein.emoji.compose.*
import org.kodein.emoji.compose.m2.TextWithNotoAnimatedEmoji
import org.kodein.emoji.compose.m2.TextWithNotoImageEmoji
import org.kodein.emoji.compose.m2.TextWithPlatformEmoji
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
            ProvideTextStyle(TextStyle(fontSize = 32.sp)) {
                TextWithPlatformEmoji(
                    "Platform:\nWhen I see :people-holding-hands~medium-light,medium-dark:, my <3 goes :collision: :D!".withEmoji()
                )

                TextWithNotoImageEmoji(
                    "images:\nWhen I see ${Emoji.PeopleHoldingHands.mediumLight_mediumDark}, my ${Emoji.RedHeart} goes ${Emoji.Collision} ${Emoji.Smile}!"
                )

                TextWithNotoAnimatedEmoji(
                    "Animated:\nWhen I see ${Emoji.PeopleHoldingHands.mediumLight_mediumDark}, my ${Emoji.RedHeart} goes ${Emoji.Collision} ${Emoji.Smile}!"
                )
            }
            NotoAnimatedEmoji(
                emoji = Emoji.ImpSmile,
                modifier = Modifier.size(64.dp),
                iterations = 2,
                stopAt = 0.76f
            )
        }
    }
}
