package com.example.evento.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.evento.main.FirstTabFragment
import com.example.evento.main.SecondTabFragment
import com.example.evento.main.ThirdTabFragment

class ViewPagerAdapter(fm:FragmentManager): FragmentPagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> FirstTabFragment()
            1 -> SecondTabFragment()
            else -> ThirdTabFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0-> "Events"
            1-> "Favourites"
            else -> "Your Events"
        }
    }
}