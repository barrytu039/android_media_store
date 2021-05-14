package com.barrytu.mediastoreretriever

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.barrytu.mediastoreretriever.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val READ_EXTERNAL_STORAGE_REQUEST = 1001

class MainActivity : AppCompatActivity(), MediaAdapter.MediaItemInterface, MediaBottomSheetDialogFragment.MediaBottomSheetInterface {

    private lateinit var binding : ActivityMainBinding

    private val mediaAdapter : MediaAdapter by lazy {
        MediaAdapter(this)
    }

    private val viewModel : MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val registerDeleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == RESULT_OK) {
            // permission granted
            it.data?.clipData?.let { clip ->
                for (i in 0..clip.itemCount) {
                    contentResolver.delete(clip.getItemAt(i).uri, null, null)
                }
            }
            it.data?.data?.let { uri ->
                contentResolver.delete(uri, null, null)
            }
            loadMediaItem()
        } else {
            // permission dined
        }
    }

    private val registerCopyResultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
        it?.let {
            DocumentFile.fromTreeUri(this, it)?.let {
                try {
                    viewModel.selectedEntity?.let { selectedItem ->
                        val toFile = it.createFile(selectedItem.mimeType, selectedItem.name)
                        val inputStream = contentResolver.openInputStream(selectedItem.uri)
                        toFile?.let { toDocFile ->
                            val outputStream = contentResolver.openOutputStream(toDocFile.uri)
                            if (inputStream != null && outputStream != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    if (FileUtils.copy(inputStream, outputStream) > 0) {
                                        loadMediaItem()
                                    }
                                } else {
                                    val buffer = ByteArray(1024)
                                    try {
                                        while (true) {
                                            val length = inputStream.read(buffer)
                                            if (length == -1) {
                                                break
                                            }
                                            outputStream.write(buffer, 0, length)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        outputStream.flush()
                                        outputStream.close()
                                        inputStream.close()
                                    }
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        // todo : temp to delay scan after write new media file
                                        delay(1000)
                                        loadMediaItem()
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadMediaItem()
        AppApplication.mediaRetriever.mediaMutableLiveData.observe(this) {
            if (it.isNullOrEmpty()) {

            } else {
                mediaAdapter.setDataSet(it)
            }
        }
        binding.mediaRecyclerView.apply {
            adapter = mediaAdapter
            layoutManager = GridLayoutManager(
                this@MainActivity,
                4,
                GridLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
        }
    }

    private fun loadMediaItem() {
        if (haveStoragePermission()) {
            lifecycleScope.launch {
                AppApplication.mediaRetriever.scanMediaItem()
            }
        } else {
            requestPermission()
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    // permission granted
                    loadMediaItem()
                } else {
                    // permission dined
                    Toast.makeText(this, "Permission Dined!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        viewModel.selectedEntity = mediaAdapter.mediaItems[position]
        // popup function option sheet
        MediaBottomSheetDialogFragment().show(supportFragmentManager, "MediaBottomSheetDialogFragment")
    }

    private fun deleteMedia() {
        viewModel.selectedEntity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    contentResolver.delete(it.uri, null, null)
                    loadMediaItem()
                } catch (e: RecoverableSecurityException) {
                    val intentSender = e.userAction.actionIntent.intentSender
                    registerDeleteResultLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    contentResolver.delete(it.uri, null, null)
                    loadMediaItem()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun deleteMedias(uris: List<Uri>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi = MediaStore.createDeleteRequest(contentResolver, uris)
            registerDeleteResultLauncher.launch(IntentSenderRequest.Builder(pi).build())
        } else {
            for (uri in uris) {
                contentResolver.delete(uri, null, null)
            }
            loadMediaItem()
        }
    }

    private fun copyMedia() {
        viewModel.selectedEntity?.let { mediaEntity ->
            registerCopyResultLauncher.launch(mediaEntity.uri)
        }
    }

    override fun onDelete() {
        val uriMutableList = mutableListOf<Uri>()
        uriMutableList.add(requireNotNull(viewModel.selectedEntity?.uri))
        deleteMedias(uriMutableList)
    }

    override fun onCopy() {
        // copy media
        copyMedia()
    }
}
