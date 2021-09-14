package com.hsu.mapapp.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.hsu.mapapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CameraFragment : Fragment(R.layout.fragment_camera) {
    val REQUEST_TAKE_PHOTO = 1
    var file: File? = null
    private lateinit var currentPhotoPath: String

    private val gpsTracker: GpsTracker? = null

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        // 카메라 실행
        dispatchTakePictureIntent()

        //

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onResume() {
        super.onResume()
        getRegionName()
    }
    //-----------------------------주소에서 지역 이름 가져오기----------------------------------//
    private fun getRegionName(){
        val address = getCurrentGPS()
        Log.d("Address", address)

    }

    //-----------------------------카메라 실행----------------------------------//
    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Log.d("CameraFragment", "실행")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                createImageFile().let {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.hsu.mapapp",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    file = it
                }
            }
        }
    }

    //-----------------------------카메라 실행 결과 받기----------------------------------//
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView: ImageView = requireActivity().findViewById(R.id.imageView2) as ImageView
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> { // 카메라 앱
                    file.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                Uri.fromFile(it)
                            )
                            ImageDecoder.decodeBitmap(source).let {
                                imageView.setImageBitmap(it)
                                saveMediaToStorage(it)
                            }
                        } else {
                            MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                Uri.fromFile(it)
                            ).let {
                                imageView.setImageBitmap(it)
                                saveMediaToStorage(it)
                            }
                        }
                    }
                }
                GPS_ENABLE_REQUEST_CODE -> { //사용자가 GPS 활성 시켰는지 검사
                    if (checkLocationServicesStatus()) {
                        if (checkLocationServicesStatus()) {
                            Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                            checkRunTimePermission();
                            return;
                        }
                    }

                }
            }
        }
    }

    //-----------------------------카메라로 찍은 사진 파일로 만들기----------------------------------//
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = "JPEG_${timeStamp}"
        return File(storageDir, filename)
    }

    //-----------------------------갤러리에 사진 저장----------------------------------//
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMediaToStorage(bitmap: Bitmap) {
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val todayDate = date.format(formatter)
        val filename = "JPEG_${todayDate}"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.requireActivity().contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                var contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                contentValues = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        getMimeType(imageUri.toString())
                    )
                }
                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this.context, "Captured View and saved to Gallery", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    //-----------------------------현재 위치 구하기----------------------------------//
    private fun getCurrentGPS(): String {
        val gpsTracker = GpsTracker(requireActivity())

        val latitude: Double = gpsTracker.getLatitude()
        val longitude: Double = gpsTracker.getLongitude()
        val address: String = getCurrentAddress(latitude, longitude)

        Toast.makeText(requireActivity(), "현재위치 \n위도 $latitude\n경도 $longitude", Toast.LENGTH_SHORT)
            .show()
        return address
    }

    fun getCurrentAddress(latitude: Double, longitude: Double): String {
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(requireContext(), "지오코더 서비스 사용불가", Toast.LENGTH_SHORT).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(requireContext(), "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses.isEmpty()) {
            Toast.makeText(requireContext(), "주소 미발견", Toast.LENGTH_SHORT).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        return address.getAddressLine(0).toString() + "\n"
    }

    fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음
            getCurrentGPS()
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    REQUIRED_PERMISSIONS.get(0)
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(requireContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT)
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager?
        return (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

}