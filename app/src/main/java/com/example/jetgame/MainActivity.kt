package com.example.jetgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var score : TextView
    lateinit var coin : ImageView
    lateinit var bullet : ImageView
    lateinit var ship : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        coin = findViewById(R.id.coin)
        bullet = findViewById(R.id.bullet)
        ship = findViewById(R.id.ship)
        score = findViewById(R.id.Score_counter)

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        //Animation for coin
        coin_Animation(coin, screenWidth,bullet,score)

        ship.setOnClickListener(){
            ship.isEnabled = false
            // get the current Y position of the image view
            val currentY = bullet.translationY

            // create an ObjectAnimator to animate the image view
            val animator = ObjectAnimator.ofFloat(bullet, "translationY", currentY, currentY - 1100f)

            // set the duration of the animation to 1 second
            animator.duration = 200

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // create a new ObjectAnimator to move the image view back to its original position
                    val downAnimator = ObjectAnimator.ofFloat(bullet, "translationY", currentY)
                    downAnimator.duration = 1
                    downAnimator.start()
                    ship.isEnabled = true
                }
            })
            // start the animation
                animator.start()
            }
    }


    private fun coin_Animation(coin:ImageView,screenWidth:Float,bullet__:ImageView,score_:TextView){
        //create the left-to-right animation
        // create the left-to-right animation
        val leftToRightAnimation = ObjectAnimator.ofFloat(coin, "translationX", -screenWidth/2, screenWidth/2)
        leftToRightAnimation.duration = 500 // animation duration in milliseconds

        // create the right-to-left animation
        val rightToLeftAnimation = ObjectAnimator.ofFloat(coin, "translationX", screenWidth/2, -screenWidth/2)
        rightToLeftAnimation.duration = 500 // animation duration in milliseconds

        // chain the animations together using an AnimatorSet
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(leftToRightAnimation, rightToLeftAnimation)

        // repeat the animation indefinitely
        animatorSet.playSequentially(leftToRightAnimation, rightToLeftAnimation)

        // start the animation
        animatorSet.start()
        //for Looping Animation

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // restart the animation when it ends
                animatorSet.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}

        })
        //for checking intersection
        leftToRightAnimation.addUpdateListener {
            // check if the two images are intersecting
            if (isIntersecting(coin, bullet__)) {
                // do something
                var scToString = score_.text.toString()
                score_.text = (scToString.toInt()+1).toString()
            }
        }

        rightToLeftAnimation.addUpdateListener {
            // check if the two images are intersecting
            if (isIntersecting(coin, bullet__)) {
                // do something
                var scToString = score_.text.toString()
                score_.text = (scToString.toInt()+1).toString()

            }
        }

    }
    private fun isIntersecting(view1: View, view2: View): Boolean {
        val rect1 = Rect()
        view1.getHitRect(rect1)

        val rect2 = Rect()
        view2.getHitRect(rect2)

        return rect1.intersect(rect2)
    }
}
