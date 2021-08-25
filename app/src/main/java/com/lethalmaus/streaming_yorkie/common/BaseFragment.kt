package com.lethalmaus.streaming_yorkie.common

import android.content.Context
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lethalmaus.streaming_yorkie.MainActivity
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.databinding.ActivityMainBinding
import org.apmem.tools.layouts.FlowLayout
import kotlin.reflect.KClass

const val ERROR = "ERROR"

abstract class BaseFragment : Fragment() {

    lateinit var activityBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        activityBinding = (requireActivity() as MainActivity).binding
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).let {
            it.binding.pageHeader.visibility = View.VISIBLE
            it.binding.pageActionIcon.visibility = View.GONE
        }
    }

    fun setTileSize(view: View, marginDP: Int = 16, columns: Int = 3) {
        val displayMetrics = requireContext().resources.displayMetrics
        val margin = (marginDP * displayMetrics.density).toInt()
        val tileSpacing = (marginDP * displayMetrics.density * (columns + 1)) / columns
        val tileSize = (displayMetrics.widthPixels / columns) - tileSpacing.toInt()
        val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(tileSize, tileSize)
        params.setMargins(0, margin, margin, 0)
        view.layoutParams = params
    }

    fun navigate(fragment: Fragment, popBackTo: KClass<*>? = null, popImmediate: Boolean = false) {
        parentFragmentManager
            .also { fm ->
                popBackTo?.let {
                    if (popImmediate) {
                        fm.popBackStackImmediate(it.java.simpleName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    } else {
                        fm.popBackStack(it.java.simpleName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                }
            }
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .replace(R.id.fragmentContainer, fragment, fragment::class.java.simpleName)
            .addToBackStack(fragment::class.java.simpleName)
            .commit()
    }

    fun showDialog(dialog: DialogFragment) {
        dialog.show(parentFragmentManager, DIALOG_TAG)
    }

    fun tintImage(icon: ImageView, color: Int) {
        ImageViewCompat.setImageTintList(
            icon, ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), color)
            )
        )
    }

    fun hasInternetAccess(): Boolean {
        val connectivityManager = ContextCompat.getSystemService(
            requireContext(),
            ConnectivityManager::class.java
        )
        return (connectivityManager?.activeNetworkInfo?.isAvailable == true && connectivityManager.activeNetworkInfo?.isConnected == true)
    }

    fun logResponse(response: GenericResponse<Any?>) {
        if (response is NetworkError) {
            try {
                val log = "### ${response.errorCode} ${response.url}\r\n ${response.body}\r\n ${response.message}\r\n ${response.headers}\r\n"
                val fileOutputStream = requireContext().openFileOutput(ERROR, Context.MODE_APPEND)
                fileOutputStream.write(log.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
interface IOnBackPressed {
    fun onBackPressed(): Boolean
}