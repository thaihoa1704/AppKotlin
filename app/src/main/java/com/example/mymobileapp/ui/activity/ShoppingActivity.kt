package com.example.mymobileapp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingBinding
    private lateinit var controller: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerViewShopping) as NavHostFragment)
        controller = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, controller)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            onNavDestinationSelected(item, controller)
            true
        }
        hideBottomNavigationView()
    }
    private fun hideBottomNavigationView() {
        val listFragment: ArrayList<Int> = getIntegers()
        controller.addOnDestinationChangedListener { _, destination, _ ->
            if (listFragment.contains(destination.id)) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
    private fun getIntegers(): ArrayList<Int> {
        val listFragment = ArrayList<Int>()
        listFragment.add(R.id.detailProductFragment)
        listFragment.add(R.id.searchFragment)
        listFragment.add(R.id.productListFragment)
        listFragment.add(R.id.addressFragment)
        listFragment.add(R.id.profileFragment)
        listFragment.add(R.id.addAddressFragment)
        listFragment.add(R.id.changeNameFragment)
        listFragment.add(R.id.passwordFragment)
        listFragment.add(R.id.orderFragment)
        listFragment.add(R.id.orderProcessFragment)
        listFragment.add(R.id.detailOrderFragment)
        listFragment.add(R.id.rateOrderFragment)
        listFragment.add(R.id.handleOrderFragment)
        listFragment.add(R.id.deliveredFragment)
        return listFragment
    }
}