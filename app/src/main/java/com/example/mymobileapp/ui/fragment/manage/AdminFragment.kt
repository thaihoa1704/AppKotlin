package com.example.mymobileapp.ui.fragment.manage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentAdminBinding
import com.example.mymobileapp.listener.OnClickChoice
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.activity.MainActivity
import com.example.mymobileapp.ui.dialog.ChoiceDialog
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AdminFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: FragmentAdminBinding
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var controller: NavController
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.bringToFront()

        val toggle = ActionBarDrawerToggle(requireActivity(), binding.drawerLayout, binding.toolbar
            , R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        user = it.data!!
                    }
                }
            }
        }
        binding.cardProduct.setOnClickListener {
            controller.navigate(R.id.action_adminFragment_to_categoryFragment)
        }
        binding.cardAccount.setOnClickListener {
            controller.navigate(R.id.action_adminFragment_to_accountFragment)
        }
        binding.cardOrder.setOnClickListener {
            controller.navigate(R.id.action_adminFragment_to_orderListFragment)
        }
        binding.cardStatistical.setOnClickListener {
            controller.navigate(R.id.action_adminFragment_to_statisticalFragment)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_info -> {
                val bundle = Bundle()
                bundle.putSerializable("user", user)
                controller.navigate(R.id.action_adminFragment_to_profileFragment2, bundle)
            }
            R.id.nav_logout -> {
                val logoutDialog = ChoiceDialog("AdminActivity", object : OnClickChoice {
                    override fun onClick(choice: Boolean?) {
                        if (choice == true) {
                            userLogout()
                        }
                    }
                })
                logoutDialog.show(requireActivity().supportFragmentManager, null)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun userLogout() {
        userViewModel.userLogout()
        Toast.makeText(requireContext(), "Tài khoản đã đăng xuất!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}