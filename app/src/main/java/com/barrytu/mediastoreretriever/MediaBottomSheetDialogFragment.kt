package com.barrytu.mediastoreretriever

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barrytu.mediastoreretriever.databinding.FragmentMediabuttomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MediaBottomSheetDialogFragment : BottomSheetDialogFragment() {

    lateinit var binding : FragmentMediabuttomsheetBinding

    private var mediaBottomSheetInterface : MediaBottomSheetInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediabuttomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mediaBottomSheetInterface = try {
            context as MediaBottomSheetInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentMediaBottomSheetDeleteLayout.setOnClickListener {
            mediaBottomSheetInterface?.onDelete()
            dismiss()
        }

        binding.fragmentMediaBottomSheetCopyLayout.setOnClickListener {
            mediaBottomSheetInterface?.onCopy()
            dismiss()
        }
    }

    interface MediaBottomSheetInterface {
        fun onDelete()
        fun onCopy()
    }
}