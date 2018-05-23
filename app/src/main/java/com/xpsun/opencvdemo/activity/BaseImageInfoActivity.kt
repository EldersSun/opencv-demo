package com.xpsun.opencvdemo.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.xpsun.opencvdemo.R
import com.xpsun.opencvdemo.framework.BaseActivity
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class BaseImageInfoActivity : BaseActivity() {

    private lateinit var baseImageInfoShow: ImageView
    private lateinit var originalMat: Mat
    private lateinit var grayMat: Mat
    private lateinit var cannyEdges: Mat

    override fun initWidgetsLayoutId(): Int {
        return R.layout.ac_baseimageinfo_layout
    }

    override fun initWidgets() {
        baseImageInfoShow = findViewById(R.id.base_image_info_show)
    }

    override fun initWidgetsInstance() {

        originalMat = Mat(baseImageInfoShow.height, baseImageInfoShow.width, CvType.CV_8U)
        grayMat = Mat()
        cannyEdges = Mat()

    }

    override fun initWidgetsEvent() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.base_image_info_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val takePhoto = Intent(Intent.ACTION_PICK)
        takePhoto.type = "image/"
        var jumpResultTag: Int = -1
        when (item?.itemId) {
            R.id.base_image_open_gallery -> {
                jumpResultTag = PICK_IMAGE_TAG_1
            }
            R.id.cannyEdges -> {
                jumpResultTag = PICK_IMAGE_TAG_2
            }
            R.id.sobel -> {
                jumpResultTag = PICK_IMAGE_TAG_3
            }
            R.id.houghline -> {
                jumpResultTag = PICK_IMAGE_TAG_4
            }
            R.id.houghCircles -> {
                jumpResultTag = PICK_IMAGE_TAG_5
            }
        }
        startActivityForResult(takePhoto, jumpResultTag)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val imageStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            Utils.bitmapToMat(bitmap, originalMat)

            when (requestCode) {
                PICK_IMAGE_TAG_1 -> {
                    differenceOfGaussian(bitmap)
                }
                PICK_IMAGE_TAG_2 -> {
                    canny(bitmap)
                }
                PICK_IMAGE_TAG_3 -> {
                    sobel(bitmap)
                }
                PICK_IMAGE_TAG_4 -> {
                    houghLines(bitmap)
                }
                PICK_IMAGE_TAG_5 -> {
                    houghCircles(bitmap)
                }
            }
        }
    }

    private fun houghCircles(bitmap: Bitmap) {
        var circles = Mat()
        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        Imgproc.Canny(grayMat, cannyEdges, 10.0, 100.0)

        Imgproc.HoughCircles(cannyEdges, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, (cannyEdges.rows() / 15).toDouble())

        var houghCircles = Mat()
        houghCircles.create(cannyEdges.rows(), houghCircles.cols(), CvType.CV_8UC1)

        var i: Int = 0
        while (i < circles.cols()) {
            var parameters = circles[0, i]
            var x: Double
            var y: Double
            var r: Int

            x = parameters[0]
            y = parameters[1]
            r = parameters[2] as Int

            var center = Point(x, y)

            Core.circle(houghCircles, center, r, Scalar(255.0, 0.0, 0.0),1)

        }

        setImageResult(houghCircles, bitmap)
    }

    //霍夫直线
    private fun houghLines(bitmap: Bitmap) {

        var lines = Mat()
        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        Imgproc.Canny(grayMat, cannyEdges, 10.0, 100.0)

        Imgproc.HoughLinesP(cannyEdges, lines, 1.0, Math.PI / 180, 50, 20.0, 20.0)

        var houghLines = Mat()

        houghLines.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1)

        //在图像上绘制直线
        var i: Int = 0
        while (i < lines.cols()) {

            var points = lines[0, i]

            var x1: Double
            var y1: Double
            var x2: Double
            var y2: Double

            x1 = points[0]
            y1 = points[1]
            x2 = points[2]
            y2 = points[3]

            var pt1 = Point(x1, y1)
            var pt2 = Point(x2, y2)

            Core.line(houghLines, pt1, pt2, Scalar(255.0, 0.0, 0.0), 1)

            i++
        }

        setImageResult(houghLines, bitmap)
    }

    //sobel 算子
    private fun sobel(bitmap: Bitmap) {
        val sobel = Mat()

        //分别用户保存梯度和绝对梯度的mat
        val grad_x = Mat()
        val abs_grad_x = Mat()

        val grad_y = Mat()
        val abs_grad_y = Mat()

        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        //计算水平梯度
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1.0, 0.0)
        //计算垂直梯度
        Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1.0, 0.0)
        //计算两个方向上的绝对值梯度
        Core.convertScaleAbs(grad_x, abs_grad_x)
        Core.convertScaleAbs(grad_y, abs_grad_y)

        //计算结果值梯度
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1.0, sobel)

        setImageResult(sobel, bitmap)
    }

    //canny 边缘检测器
    private fun canny(bitmap: Bitmap) {

        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        Imgproc.Canny(grayMat, cannyEdges, 10.0, 100.0)

        setImageResult(cannyEdges, bitmap)
    }

    //高斯差分技术
    private fun differenceOfGaussian(bitmap: Bitmap) {
        val blur1 = Mat()
        val blur2 = Mat()

        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        //以两个不同的模糊半径对图像做模糊处理
        Imgproc.GaussianBlur(grayMat, blur1, Size(15.0, 15.0), 5.0)
        Imgproc.GaussianBlur(grayMat, blur2, Size(21.0, 21.0), 5.0)

        //将两幅图像模糊后的图像做相减
        val dog = Mat()
        Core.absdiff(blur1, blur2, dog)

        //反转二值阈值化
        Core.multiply(dog, Scalar(100.0), dog)
        Imgproc.threshold(dog, dog, 50.0, 255.0, Imgproc.THRESH_BINARY_INV)

        setImageResult(dog, bitmap)
    }

    private fun setImageResult(mat: Mat, bitmap: Bitmap) {
        //将mat 转回位图
        Utils.matToBitmap(mat, bitmap)
        baseImageInfoShow.setImageBitmap(bitmap)
    }

    companion object {
        private const val PICK_IMAGE_TAG_1 = 0x1001
        private const val PICK_IMAGE_TAG_2 = 0x1002
        private const val PICK_IMAGE_TAG_3 = 0x1003
        private const val PICK_IMAGE_TAG_4 = 0x1004
        private const val PICK_IMAGE_TAG_5 = 0x1005
    }

}