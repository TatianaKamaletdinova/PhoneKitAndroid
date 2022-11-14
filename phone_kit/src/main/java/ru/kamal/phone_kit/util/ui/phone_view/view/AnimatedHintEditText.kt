package ru.kamal.phone_kit.util.ui.phone_view.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat.getColor
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import ru.kamal.country_phone_kit.R

/**
 * Компонент с анимированным появлением хинта за счет физики анимаций, основанной на [SpringAnimation]
 * Уникален своей отрисовкой хинта в процеcсе ввода текста.
 */
internal class AnimatedHintEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs) {

    companion object {
        private const val SPRING_MULTIPLIER = 100f
    }

    var hintEditText: String = ""
        set(value) {
            updateHintEditText(value)
            field = value
        }

    private var hintPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val rect = Rect()

    private val hintFadeProperty: HintFadeProperty = HintFadeProperty()
    private val hintAnimationValues: MutableList<Float> = ArrayList()
    private val hintAnimations: MutableList<SpringAnimation> = ArrayList()
    private var hintAnimationCallback: Runnable? = null

    private var wasHintVisible: Boolean? = null

    private var hintText: String = ""
        set(value) {
            field = value
            invalidate()
            text = text
        }

    init {
        isSaveEnabled = false
        setup()
    }

    private fun setup() {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
        setTextColor(getColor(context, R.color.black))

        hintPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
        hintPaint.color = getColor(context, R.color.mask)
    }

    private fun updateHintEditText(value: String) {
        val show = value.isNotBlank()

        var runAnimation = false
        if (wasHintVisible == null || wasHintVisible != show) {
            hintAnimationValues.clear()
            for (a in hintAnimations) {
                a.cancel()
            }
            hintAnimations.clear()
            wasHintVisible = show
            runAnimation = text.isNullOrBlank()
        }

        if (show || !runAnimation) {
            hintText = value
        }

        if (runAnimation) {
            val str = if (show) value else this.hintEditText
            runHintAnimation(str.length, show) {
                hintAnimationValues.clear()
                for (a in hintAnimations) {
                    a.cancel()
                }
                if (!show) {
                    hintText = value
                }
            }
        }
    }

    private fun runHintAnimation(length: Int, show: Boolean, callback: Runnable) {
        if (hintAnimationCallback != null) {
            removeCallbacks(hintAnimationCallback)
        }
        for (i in 0 until length) {
            val startValue: Float = if (show) 0f else 1f
            val finalValue: Float = if (show) 1f else 0f

            val springAnimation = SpringAnimation(i, hintFadeProperty)
                .setSpring(
                    SpringForce(finalValue * SPRING_MULTIPLIER)
                        .setStiffness(500f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
                        .setFinalPosition(finalValue * SPRING_MULTIPLIER)
                        .setStiffness(SpringForce.STIFFNESS_LOW)
                )
                .setStartValue(startValue * SPRING_MULTIPLIER)
            hintAnimations.add(springAnimation)
            hintAnimationValues.add(startValue)
            springAnimation.start()

           postDelayed({ springAnimation.start() }, i * 5L)
        }
        postDelayed(callback.also { hintAnimationCallback = it }, length * 5L + 150L)
    }

    override fun onDraw(canvas: Canvas) {
        if (hintText.isNotBlank() && length() < hintText.length) {
            var offsetX = 0f
            for (a in hintText.indices) {
                val newOffset: Float = if (a < length()) {
                    paint.measureText(text, a, a + 1)
                } else {
                    hintPaint.measureText(hintText, a, a + 1)
                }
                if (a < length()) {
                    offsetX += newOffset
                    continue
                }
                canvas.save()
                hintPaint.getTextBounds(hintText, 0, hintText.length, rect)
                val offsetY = (height + rect.height()) / 2f
                onPreDrawHintCharacter(a)
                canvas.drawText(hintText, a, a + 1, offsetX, offsetY, hintPaint)
                offsetX += newOffset
                canvas.restore()
            }
        }
        super.onDraw(canvas)
    }

    private fun onPreDrawHintCharacter(index: Int) {
        if (index < hintAnimationValues.size) {
            hintPaint.alpha = (hintAnimationValues[index] * 0xFF).toInt()
        }
    }

    private inner class HintFadeProperty : FloatPropertyCompat<Int>("hint_fade") {
        override fun getValue(hintFade: Int): Float {
            return if (hintFade < hintAnimationValues.size) hintAnimationValues[hintFade] * SPRING_MULTIPLIER else 0f
        }

        override fun setValue(hintFade: Int, value: Float) {
            if (hintFade < hintAnimationValues.size) {
                hintAnimationValues[hintFade] = value / SPRING_MULTIPLIER
                invalidate()
            }
        }
    }
}