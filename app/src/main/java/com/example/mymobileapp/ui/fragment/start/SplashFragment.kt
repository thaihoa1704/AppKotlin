package com.example.mymobileapp.ui.fragment.start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentSplashBinding
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.activity.AdminActivity
import com.example.mymobileapp.ui.activity.ShoppingActivity
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var controller: NavController
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = Navigation.findNavController(view)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Handler().postDelayed({
                            controller.navigate(R.id.action_splashFragment_to_loginFragment)
                        }, 3000)
                    }
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        Handler().postDelayed({
                            changeActivity(it.data!!)
                        }, 3000)
                    }
                }
            }
        }
    }

    private fun changeActivity(user: User) {
        val type = user.type
        if (type == "admin"){
            val intent = Intent(requireContext(), AdminActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else{
            val intent = Intent(requireContext(), ShoppingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}