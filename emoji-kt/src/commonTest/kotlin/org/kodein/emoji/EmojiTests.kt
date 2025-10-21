package org.kodein.emoji

import org.kodein.emoji.people_body.family.PeopleHoldingHands
import org.kodein.emoji.people_body.family.WomanAndManHoldingHands
import org.kodein.emoji.smileys_emotion.emotion.Collision
import org.kodein.emoji.smileys_emotion.face_smiling.Grin
import org.kodein.emoji.smileys_emotion.face_smiling.GrinningFace
import org.kodein.emoji.smileys_emotion.heart.RedHeart
import kotlin.test.Test
import kotlin.test.assertEquals


class EmojiTests {

    @Test
    fun toneEmoji() {
        assertEquals(
            expected = "👩🏼‍🤝‍👨🏾",
            actual = "${Emoji.WomanAndManHoldingHands.mediumLight_mediumDark}"
        )
    }

    @Test
    fun replaceEmojis() {
        val catalog = EmojiTemplateCatalog(Emoji.list())
        assertEquals(
            expected = "When I see 🧑🏼‍🤝‍🧑🏾, my ❤️ goes 💥 😀!",
            actual = catalog.replace("When I see :people-holding-hands~medium-light,medium-dark:, my <3 goes :collision: :D!")
        )
    }

    @Test
    fun findEmoji() {
        assertEquals(
            expected = arrayListOf(
                FoundEmoji(0, 12, Emoji.PeopleHoldingHands.withSkinTone(SkinTone.MediumLight, SkinTone.MediumDark)),
                FoundEmoji(13, 15, Emoji.RedHeart),
                FoundEmoji(15, 17, Emoji.Collision)
            ),
            actual = EmojiFinder().findEmoji("🧑🏼‍🤝‍🧑🏾 ❤️💥").toList()
        )
    }

    @Test
    fun countEmoji() {
        assertEquals(emojiCount, Emoji.list().size)
    }

    @Test
    fun unicodeVersionProps() {
        val testVersion = UnicodeVersion(15,2)
        assertEquals(
            expected = 15,
            actual = testVersion.major
        )
        assertEquals(
            expected = 2,
            actual = testVersion.minor
        )
    }
}
