package dev.thkim.imagecroptest

import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Picasso
import dev.thkim.imagecroptest.databinding.ActivityMainBinding
import java.lang.Math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val url = "http://image.dongascience.com/Photo/2020/03/5bddba7b6574b95d37b6079c199d7101.jpg"

    lateinit var bitmap: Bitmap

    var matrix: Matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(binding.ivImage)
            .load(url)
            .fitCenter()
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        bitmap = resource.toBitmap()
                    }
                    return false
                }
            })
            .into(binding.ivImage)


        binding.btnCrop.setOnClickListener {
            val rect = binding.ivImage.displayRect
            Log.d("CropTest", "rect.left: ${rect.left}, rect.top: ${rect.top}, rect.right: ${rect.right}, rect.bottom: ${rect.bottom}")

            // 크롭할 영역 뷰의 width, height
            val viewWidth = binding.ivImage.width
            val viewHeight = binding.ivImage.height

            // 확대된 이미지가 이미지뷰를 채우는 경우
            val scaledBitmap = bitmap.scale(rect.width().toInt(), rect.height().toInt())

            val width = scaledBitmap.width
            val height = scaledBitmap.height
            Log.d("CropTest", "scaledBitmap.width: ${width}, scaledBitmap.height: ${height}")

            val left = if (rect.left >= 0) { // rect.left이 0 보다 큰 경우 (왼쪽에 여백이 있는 경우)
                0
            } else {
                abs(rect.left).toInt()
            }
            val top = if (rect.top >= 0) { // rect.top 0 보다 큰 경우 (위쪽에 여백이 있는 경우)
                0
            } else {
                abs(rect.top).toInt()
            }
            val cropWidth = if (width < viewWidth) { // scale된 비트맵 rect의 width가 viewWidth 보다 큰 경우 (오른쪽에 여백이 있는 경우)
                width // bitmap의 전체 width
            } else {
                viewWidth
            }
            val cropHeight = if (height < viewHeight) { // scale된 비트맵 rect의 hegiht가 viewHeight보다 큰 경우 (오른쪽에 여백이 있는 경우)
                height
            } else {
                viewHeight
            }

            Log.d("CropTest", "left: ${left}, top: ${top}, cropWidth: ${cropWidth}, cropHeight: ${cropHeight}")

            Glide.with(binding.ivImage2)
                .load(getCroppedImage(scaledBitmap, left, top, cropWidth, cropHeight))
                .fitCenter()
                .into(binding.ivImage2)
        }

        binding.btnSaveMatrix.setOnClickListener {
            binding.ivImage.getSuppMatrix(matrix)
            Log.d("CropTest", "btnSave matrix: ${matrix}")
        }

        binding.btnLoadMatrix.setOnClickListener {
            if (matrix != null) {
                binding.ivImage.setSuppMatrix(matrix)
                Log.d("CropTest", "btnLoad matrix: ${matrix}")
            }
        }
    }

    fun getCroppedImage(bitmap: Bitmap,
                        x: Int,
                        y: Int,
                        width: Int,
                        height: Int): Bitmap? {
        if (width > 0 && height > 0) {
            try {
                val output = createBitmap(bitmap, x, y, width, height)
                return output
            } catch (e: Exception) {
                Log.e("CropTest", "${e.cause.toString()}: ${e.message}")
            }
        }
        return null
    }

}