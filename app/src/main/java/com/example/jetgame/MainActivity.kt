package com.example.jetgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    lateinit var score : TextView
    lateinit var coin : ImageView
    lateinit var bullet : ImageView
    lateinit var ship : ImageView
    lateinit var instruction: LinearLayout
    lateinit var close: ImageView

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        coin = findViewById(R.id.coin)
        bullet = findViewById(R.id.bullet)
        ship = findViewById(R.id.ship)
        score = findViewById(R.id.Score_counter)
        instruction = findViewById(R.id.linear)
        close = findViewById(R.id.cancel_instructions)


        //instruction Close and all other things will be visible
        close.setOnClickListener(){
            instruction.visibility = View.GONE
            val profit = findViewById<TextView>(R.id.profit)
            profit.visibility = View.VISIBLE
            score.visibility = View.VISIBLE
            coin.visibility = View.VISIBLE
            bullet.visibility = View.VISIBLE
            ship.visibility = View.VISIBLE

        }
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()

        //Animation for coin
        coin_Animation(coin, screenWidth,bullet,score)


        ship.setOnTouchListener(object : View.OnTouchListener {
            var dx = 0f
            var bulletStartX = 0f
            var bulletStartY = 0f
            var isBulletFired = false

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dx = event.rawX - view.x

                        // Save the starting position of the bullet
                        bulletStartX = bullet.x
                        bulletStartY = bullet.y

                        // Set a flag to indicate that the bullet hasn't been fired yet
                        isBulletFired = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        var x = event.rawX - dx

                        // Limit the ship's movement to within the screen bounds
                        if (x < 0) {
                            x = 0f
                        }
                        if (x + view.width > screenWidth) {
                            x = screenWidth - view.width
                        }

                        // Move the ship and the bullet
                        view.x = x
                        if (isBulletFired) {
                            bullet.y -= 30 // move the bullet upward
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isBulletFired) {
                            fireBullet(bullet, view)
                            ship.isEnabled = false
                        }
                    }
                }
                return true
            }
            private fun fireBullet(bullet: ImageView, ship: View) {
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()
                // Set the starting position of the bullet to the center of the ship
                bullet.x = ship.x + (ship.width / 2) - (bullet.width / 2)
                bullet.y = ship.y

                // create an ObjectAnimator to animate the bullet
                val animator = ObjectAnimator.ofFloat(bullet, "translationY", -screenHeight)
                animator.duration = 500 // animation duration in milliseconds

                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        ship.isEnabled = true
                    }
                })

                // start the animation
                animator.start()
            }
        })

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
