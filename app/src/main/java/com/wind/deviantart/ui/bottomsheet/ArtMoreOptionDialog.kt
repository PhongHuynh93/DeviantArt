package com.wind.deviantart.ui.bottomsheet

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.work.WorkInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentArtMoreBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Phong Huynh on 8/12/2020
 */
private const val EXTRA_DATA = "xData"

@AndroidEntryPoint
class ArtMoreOptionDialog : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(path: String?): ArtMoreOptionDialog {
            return ArtMoreOptionDialog().apply {
                arguments = bundleOf(EXTRA_DATA to path)
            }
        }
    }

    private lateinit var viewBinding: FragmentArtMoreBinding
    private val vmArtMoreOptionDialog by viewModels<ArtMoreOptionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentArtMoreBinding.inflate(inflater, container, false).apply {
            vm = vmArtMoreOptionDialog
            path = requireArguments()[EXTRA_DATA] as String
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        vmArtMoreOptionDialog.apply {
            errorEmptyPath.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
            close.observe(viewLifecycleOwner) {
                dismiss()
            }
            downloadImageState.observe(viewLifecycleOwner) { state ->
                when (state?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Toast.makeText(requireContext(), getString(R.string.success_save_image), Toast.LENGTH_SHORT)
                            .show()
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.BLOCKED -> {
                        Toast.makeText(requireContext(), getString(R.string.error_save_image), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    // TODO: 8/15/2020 handle the permission dialog here
    private fun checkPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
//                showInContextUI(...)
                Toast.makeText(requireContext(), "showInContextUI", Toast.LENGTH_SHORT).show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}