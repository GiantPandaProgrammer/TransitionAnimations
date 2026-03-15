package subdomain.reverse.domain.animationsapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.apply
import kotlin.random.Random

private val slides = intArrayOf(
    R.drawable.download,
    R.drawable.download2,
    R.drawable.download3
)

private lateinit var imgA: ImageView
private lateinit var imgB: ImageView
private lateinit var nextButton: Button

private var index = 0

private var showingA = true
private var animating = false
private var durationMs = 450L

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imgA = findViewById<ImageView>(R.id.imgA)
        imgB = findViewById<ImageView>(R.id.imgB)
        nextButton = findViewById<Button>(R.id.nextButton)

        //TODO Show the first image
        imgA.setImageResource(slides[index])
        nextButton.setOnClickListener { if (!animating) nextSlide() }
        //TODO set the next button click listener
    }
}

private fun nextSlide() {
    animating = true
    nextButton.isEnabled = false
    val outgoing = if(showingA) imgA else imgB
    val incoming = if (showingA) imgB else imgA

    index = (index + 1) % slides.size

    incoming.setImageResource(slides[index])
    incoming.visibility=View.VISIBLE

    resetViewForAnimation(outgoing)
    resetViewForAnimation(incoming)

    val exit = randomExitAnimator(outgoing)
    val enter = randomEnterAnimation(incoming)

    val set = AnimatorSet().apply {
        playTogether(exit, enter)
        interpolator = AccelerateDecelerateInterpolator()
        duration = durationMs
        addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                outgoing.visibility = View.INVISIBLE
                resetViewForAnimation(outgoing)
                resetViewForAnimation(incoming)
                showingA = !showingA
                animating = false
                nextButton.isEnabled = true

            }
        }
    }
}

private fun resetViewForAnimation(v:View) {
    v.alpha = 1f
    v.translationX = 0f
    v.translationY = 0f
    v.scaleX = 1f
    v.scaleY = 1f
    v.rotation = 0f
}


private fun randomEnterAnimation(v:View): Animator {
    return when (Random.nextInt(5)) {
        0 -> {
            // Fade in
            v.alpha = 0f
            ObjectAnimator.ofFloat(v, View.ALPHA, 1f)
        }

        1 -> {
            // Slide in from left
            v.translationX = -v.width.toFloat().coerceAtLeast(1f)
            ObjectAnimator.ofFloat(v, View.TRANSLATION_X, 0f)
        }

        2 -> {
            // Slide in from the right
            v.translationX = v.width.toFloat().coerceAtLeast(1f)
            ObjectAnimator.ofFloat(v, View.TRANSLATION_X, 0f)
        }
        3 -> {
            v.scaleX = 0.05f
            v.scaleY = 0.05f
            v.alpha = 0.4f

            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(v, View.SCALE_X, 1f),
                    ObjectAnimator.ofFloat(v, View.SCALE_Y, 1f),
                    ObjectAnimator.ofFloat(v, View.ALPHA, 1f)
                )
            }
            else -> {
                v.rotation = -5f
                v.alpha = 0f
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(v, View.ROTATION, 0),
                        ObjectAnimator.ofFloat(v, View.ALPHA, 1f)
                    )
                }
            }
        }

    }

}

private fun randomExitAnimator(v: View): Animator {
    return when (Random.nextInt(5)) {
        0 -> {
            // Fade out
            ObjectAnimator.ofFloat(v, View.ALPHA, 0f)
        }

        1 -> {
            // Slide out left
            ObjectAnimator.ofFloat(v, View.TRANSLATION_X, -v.width.toFloat().coerceAtLeast(1f))
        }

        2 -> {
            // Slide out right
            ObjectAnimator.ofFloat(v, View.TRANSLATION_X, v.width.toFloat().coerceAtLeast(1f))
        }

        3 -> {
            // Zoom out
            AnimatorSet().apply {
                playTogether {
                    ObjectAnimator.ofFloat(v, View.SCALE_X, 0.05),
                    ObjectAnimator.ofFloat(v, View.SCALE_Y, 0.05),
                    ObjectAnimator.ofFloat(v, 0)
                }
                }

            }
        else -> {
            val dir = if (Random.nextBoolean()) 1 else -1
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(v, View.ROTATION, 0),
                    ObjectAnimator.ofFloat(v, View.ALPHA, 1f)
                )
            }
        }


    }

}