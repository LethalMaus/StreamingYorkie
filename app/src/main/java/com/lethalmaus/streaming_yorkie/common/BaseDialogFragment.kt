package com.lethalmaus.streaming_yorkie.common

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lethalmaus.streaming_yorkie.databinding.DialogFragmentBinding

const val DIALOG_TAG = "DialogFragment"

open class BaseDialogFragment : DialogFragment() {

    var viewBinding: DialogFragmentBinding? = null
    private val binding get() = viewBinding!!
    var onDismissListener: (() -> Unit)? = null

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = DialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = true
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }
}