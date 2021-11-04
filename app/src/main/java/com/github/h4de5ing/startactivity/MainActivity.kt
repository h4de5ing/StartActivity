package com.github.h4de5ing.startactivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider.getUriForFile
import com.github.h4de5ing.startactivity.databinding.ActivityMainBinding
import java.io.File

/**
StartActivityForResult() 简单启动页面获取数据
StartIntentSenderForResult()
RequestMultiplePermissions() 请求一组权限
RequestPermission()  请求单个权限
TakePicturePreview() 相机拍照
TakePicture() 相机拍照
TakeVideo()
PickContact() 获取联系人
GetContent() 选择一个内容
GetMultipleContents() 选择多个内容
OpenDocument() 选择一个文档
OpenMultipleDocuments() 选择多个文档
OpenDocumentTree() 选择文档树，目录
CreateDocument() 创建文档
以及自定义ActivityResultContract<I,O>
 */
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //1.启动一个页面获取数据
        val requestDataLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data?.getStringExtra("data")
                    Log.d(TAG, "返回来的数据是:${data}")
                }
            }
        binding.startAFR.setOnClickListener {
            requestDataLauncher.launch(Intent(this, SecondActivity::class.java))
        }
        //2.
        binding.startSFR.setOnClickListener {

        }
        //3.请求多个权限
        val requestMultiplePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
                //通过的权限
                val grantedList = it.filterValues { it }.mapNotNull { it.key }
                //是否所有权限通过
                val allGranted = grantedList.size == it.size
                val list = (it - grantedList).map { it.key }
                //未通过权限
                val deniedList =
                    list.filter { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
                //拒绝并且点了“不在询问”权限
                val alwaysDeniedList = list - deniedList
                Log.d(TAG, "授权结果:${it}")
            }
        binding.startRMP.setOnClickListener {
            requestMultiplePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
        //4.请求权限
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                Log.d(TAG, "授权结果:${granted}")
            }
        binding.startRP.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        //5.请求拍照预览
        val takePicturePreviewLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                binding.iv.setImageBitmap(bitmap)
            }
        binding.startTPP.setOnClickListener {
            takePicturePreviewLauncher.launch(null)
        }
        //6.选择照片
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "图片名称.jpg")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            getUriForFile(
                this,
                "$packageName.fileprovider",
                File(externalCacheDir!!.absolutePath)
            )
        }
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) binding.iv.setImageURI(uri)
            }
        binding.startTP.setOnClickListener {
            takePictureLauncher.launch(uri)
        }
        //7.录制视频并返回缩略图
        val captureVideoLauncher =
            registerForActivityResult(ActivityResultContracts.TakeVideo()) { bitmap ->

            }
        binding.startTV.setOnClickListener {
            captureVideoLauncher.launch(
                getUriForFile(
                    this,
                    "$packageName.fileprovider",
                    File("video1.mp4")
                )
            )
        }
        //8.选择联系人
        val register = registerForActivityResult(ActivityResultContracts.PickContact()) {
            if (it != null) {
                val cursor = contentResolver.query(it, null, null, null, null)
                cursor?.run {
                    if (cursor.moveToFirst()) {
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneNum =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    }
                }
            }
        }
        binding.startPC.setOnClickListener {
            register.launch(null)
        }
        //9.选择一条内容
        val getContentLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri1 ->
                Log.d(TAG, "文件:${uri1}")
            }
        binding.startGC.setOnClickListener {
            getContentLauncher.launch("text/plain")
        }
        //10.选择多条内容
        val getMultipleDocumentsLauncher =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri1 ->
                Log.d(TAG, "选择多条内容:${uri1}")
            }
        binding.startGMC.setOnClickListener {

        }
        //11.选择一个文档
        val openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uris ->
                Log.d(TAG, "选择一个文档:${uris}")
            }
        binding.startOD.setOnClickListener {
            openDocumentLauncher.launch(arrayOf("image/*", "text/plain"))
        }
        //12.选择多个文档
        val openMultipleDocumentsLauncher =
            registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { list ->
                Log.d(TAG, "选择多个文档:${list}")
            }
        binding.startOMD.setOnClickListener {
            openMultipleDocumentsLauncher.launch(null)
        }
        //13.打开文档树选择文档
        val openDocumentTreeLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                Log.d(TAG, "选择多个文档:${uri}")
            }
        binding.startOMT.setOnClickListener {
            openDocumentTreeLauncher.launch(
                getUriForFile(
                    this, "$packageName.fileprovider",
                    Environment.getRootDirectory()
                )
            )
        }
        //14.创建一个新文档
        val createDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
                Log.d(TAG, "创建一个新文档:${uri}")
            }
        binding.startCD.setOnClickListener {
            createDocumentLauncher.launch("log.txt")
        }
    }
}