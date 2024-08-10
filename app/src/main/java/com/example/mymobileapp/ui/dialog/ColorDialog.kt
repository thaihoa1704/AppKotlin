package com.example.mymobileapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.mymobileapp.databinding.ColorDialogBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ColorDialog : DialogFragment() {
    private lateinit var binding: ColorDialogBinding
    private var name = ""
    private var colorCode = 0

    interface GetColor {
        fun getData(name: String, colorCode: Int)
    }
    private lateinit var getColor: GetColor

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        binding = ColorDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        binding.imgAdd.setOnClickListener {
            ColorPickerDialog
                .Builder(requireContext())
                .setTitle("Chọn màu")
                .setPositiveButton("Chọn", object : ColorEnvelopeListener {

                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            colorCode = it.color
                            binding.imgColor.setBackgroundColor(it.color)
                        }
                    }
                }).setNegativeButton("Thoát") { colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }

        binding.btnNo.setOnClickListener { dialog.dismiss() }
        binding.btnYes.setOnClickListener {
            name = binding.edtName.text.toString()
            if (binding.edtName.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Không được để trống tên màu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (colorCode == 0) {
                Toast.makeText(requireContext(), "Không được để trống mã màu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (colorCode != 0 && name.isNotEmpty()) {
                getColor.getData(name, colorCode)
            }
            dialog.dismiss()
        }
        return dialog
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        getColor = (parentFragment as GetColor?)!!
    }
}