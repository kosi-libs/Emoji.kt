package org.kodein.emoji.compose.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kodein.emoji.Emoji
import org.kodein.emoji.SkinTone
import org.kodein.emoji.compose.NotoAnimatedEmoji
import org.kodein.emoji.compose.m2.TextWithNotoAnimatedEmoji
import org.kodein.emoji.compose.m2.TextWithNotoImageEmoji
import org.kodein.emoji.compose.m2.TextWithPlatformEmoji
import org.kodein.emoji.compose.withEmoji
import org.kodein.emoji.mediumLight_mediumDark
import org.kodein.emoji.people_body.family.PeopleHoldingHands
import org.kodein.emoji.people_body.person_sport.PeopleWrestling
import org.kodein.emoji.smileys_emotion.emotion.Collision
import org.kodein.emoji.smileys_emotion.face_hand.Peeking
import org.kodein.emoji.smileys_emotion.face_negative.ImpSmile
import org.kodein.emoji.smileys_emotion.face_smiling.Smile
import org.kodein.emoji.smileys_emotion.heart.RedHeart


@Composable
fun App() {
    SelectionContainer {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            ProvideTextStyle(
                TextStyle(
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                )
            ) {
                TextWithPlatformEmoji(
                    "Platform:\nWhen I see :people-holding-hands~medium-light,medium-dark:, my <3 goes :collision: :D!".withEmoji()
                )

                TextWithNotoImageEmoji(
                    "images:\nWhen I see ${Emoji.PeopleHoldingHands.mediumLight_mediumDark}, my ${Emoji.RedHeart} goes ${Emoji.Collision} ${Emoji.Smile}!"
                )

                TextWithNotoAnimatedEmoji(
                    "Animated:\nWhen I see ${Emoji.PeopleHoldingHands.mediumLight_mediumDark}, my ${Emoji.RedHeart} goes ${Emoji.Collision} ${Emoji.Smile}!"
                )

                TextWithNotoImageEmoji(
                    buildString {
                        append("Unicode 17.0:")
                        SkinTone.entries.forEach { t1 ->
                            SkinTone.entries.forEach { t2 ->
                                append(" ")
                                append(Emoji.PeopleWrestling.withSkinTone(t1, t2))
                            }
                        }
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(64.dp)
            ) {
                NotoAnimatedEmoji(
                    emoji = Emoji.ImpSmile,
                    modifier = Modifier.height(92.dp),
                    iterations = 2,
                    stopAt = 0.76f,
                    placeholder = { Box(it) }
                )
                NotoAnimatedEmoji(
                    emoji = Emoji.Peeking,
                    modifier = Modifier.height(92.dp),
                    iterations = 1,
                    skipLastFrame = true,
                    placeholder = { Box(it) }
                )
            }
        }
    }
}
