package com.xpsun.opencvdemo.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jph.takephoto.model.TResult
import com.jph.takephoto.model.TakePhotoOptions
import com.xpsun.opencvdemo.R
import com.xpsun.opencvdemo.framework.BaseActivity
import com.xpsun.opencvdemo.utils.ImageUtil
import com.xpsun.opencvdemo.utils.SysSDCardCacheDir
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File


class MainActivity : BaseActivity(), View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private lateinit var ainImageShow: ImageView
    private lateinit var ainImageShowProcessed: ImageView
    private var jump_type_tag: Int = 0

    override fun initWidgetsLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initWidgets() {
        ainImageShow = findViewById(R.id.ain_image_show)
        ainImageShowProcessed = findViewById(R.id.ain_image_show_processed)
    }

    override fun initWidgetsInstance() {
        val intent = intent
        jump_type_tag = intent.getIntExtra(HomeActivity.ACTION_MODE_TAG, 0)

        HiPermission.create(this)
                .animStyle(R.style.PermissionAnimModal)//set dialog animation
                .style(R.style.PermissionDefaultGreenStyle)//set dialog style
                .checkMutiPermission(object : PermissionCallback {
                    override fun onClose() {
                    }

                    override fun onFinish() {
                    }

                    override fun onDeny(permission: String, position: Int) {
                    }

                    override fun onGuarantee(permission: String, position: Int) {
                    }
                })
        openSelectorImage()
    }

    override fun initWidgetsEvent() {
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_load_image -> {
                openSelectorImage()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSelectorImage(){
        val takePhoto = Intent(Intent.ACTION_PICK)
        takePhoto.type = "image/"
        startActivityForResult(takePhoto, SELECTOR_PHOTO_TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECTOR_PHOTO_TAG -> {
                    val imageUri = data?.data
                    val imageStream = contentResolver.openInputStream(imageUri)
                    val selectorBitmap = BitmapFactory.decodeStream(imageStream)
                    val src = Mat(selectorBitmap.height, selectorBitmap.width, CvType.CV_8UC4)
                    Utils.bitmapToMat(selectorBitmap, src)

                    when (jump_type_tag) {
                        0 -> {
                            Imgproc.blur(src, src, Size(30.0, 30.0))
                        }
                        1 -> {
                            Imgproc.GaussianBlur(src, src, Size(3.0, 3.0), 0.0)
                        }
                        2 -> {
                            Imgproc.medianBlur(src, src, 3)
                        }
                        3 -> {
                            val kernet: Mat = Mat(3, 3, CvType.CV_16SC1)
                            kernet.put(0, 0, 0.0, -1.0, 0.0, -1.0, 5.0, -1.0, 0.0, -1.0, 0.0)
                            Imgproc.filter2D(src, src, src.depth(), kernet)
                        }
                        4 -> {
                            val kernetDilate: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
                            Imgproc.dilate(src, src, kernetDilate)
                        }
                        5 -> {
                            val kernelErode: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(5.0, 5.0))
                            Imgproc.erode(src, src, kernelErode)
                        }
                        6 ->{
                            Imgproc.threshold(src,src,100.0,255.0,Imgproc.ADAPTIVE_THRESH_MEAN_C)
                        }
                    }


                    val proessedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
                    Utils.matToBitmap(src, proessedImage)
                    ainImageShow.setImageBitmap(selectorBitmap)
                    ainImageShowProcessed.setImageBitmap(proessedImage)
                }
            }
        }
    }

    companion object {
        private const val UI_HANDLER_MESSAGE_SUCCESS_TAG: Int = 0x1001
        private const val UI_HANDLER_MESSAGE_FAIL_TAG: Int = 0x1002
        private const val UI_HANDLER_MESSAGE_CANCEL_TAG: Int = 0x1003

        private const val SELECTOR_PHOTO_TAG: Int = 0x1004
        private var IMAGE_SAVE_PATH: String = SysSDCardCacheDir.getImgDir().absolutePath +
                File.separator + String.format("%s.jpg", System.currentTimeMillis())
    }


}
