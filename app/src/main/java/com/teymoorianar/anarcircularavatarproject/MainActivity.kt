package com.teymoorianar.anarcircularavatarproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.teymoorianar.anar_circular_avatar_view.AnarStackedAvatarsView

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
        val stackedAvatarsView = findViewById<AnarStackedAvatarsView>(R.id.stackedAvatars)
        val avatars = listOf(
            AnarStackedAvatarsView.AvatarItem(imageResource = R.drawable.man_1, imageUrl = "https://teymoorianar.ir/files/simpleAvatars/man-GeneralAdmiralAladeenTheDictator.png"),
            AnarStackedAvatarsView.AvatarItem(imageUrl = "https://teymoorianar.ir/files/simpleAvatars/man-Heisenberg(WalterWhite).png"),
            AnarStackedAvatarsView.AvatarItem(imageUrl = "https://teymoorianar.ir/files/simpleAvatars/man-Stalin.png"),
            AnarStackedAvatarsView.AvatarItem(imageUrl = "https://teymoorianar.ir/files/simpleAvatars/man-hitler.png"),
            AnarStackedAvatarsView.AvatarItem(imageUrl = "https://teymoorianar.ir/files/simpleAvatars/man-hitler.png"),
            AnarStackedAvatarsView.AvatarItem(), // No image, will use random/default background
            AnarStackedAvatarsView.AvatarItem()  // Extra item, triggers "+2"
        )
        stackedAvatarsView.setAvatars(avatars)
    }
}