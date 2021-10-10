package com.example.androidgooglebooksapihilt.views.fragments

import BookPagerAdapter
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.ContextWrapper
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.allViews
import androidx.fragment.app.*
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.androidgooglebooksapihilt.R
import com.example.androidgooglebooksapihilt.databinding.FragmentBookPagerBinding
import com.example.androidgooglebooksapihilt.views.viewModels.BooksListViewModel
import com.example.androidgooglebooksapihilt.views.viewModels.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field


@AndroidEntryPoint
class BookPagerFragment : Fragment() {

    private var viewPager: ViewPager2? = null
    val viewPagerViewModel: ViewPagerViewModel by activityViewModels()
    val booksListViewModel: BooksListViewModel by activityViewModels()
    private var _binding: FragmentBookPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        ActivityCompat.postponeEnterTransition(requireActivity())
        _binding = FragmentBookPagerBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager
        viewPager?.adapter = BookPagerAdapter(this, booksListViewModel)

        // Set the current position and add a listener that will update the selection coordinator when
        // paging the images.
        viewPager!!.setCurrentItem(
            viewPagerViewModel.getBookPositionOnDownloadedList().value!!,
            false
        )

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                super.onPageSelected(position)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewPagerViewModel.setBookPositionOnDownloadedList(position)
                viewPagerViewModel.setBookPositionInRecyclerView(
                    getBookPositionInRecyclerView(
                        position
                    )
                )
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })

        prepareEnterSharedElementTransition()
//        postponeEnterTransition()

//         Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }

        activity?.let { hideKeyboard(it) }
        return binding.viewPager
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getBookPositionInRecyclerView(
        position: Int
    ): Int {
        val positionOnSmallList: Int = position
        val freeItemsListSize =
            booksListViewModel.getMutableLiveDataFreeBookListSizeObserver().value!!

        if (positionOnSmallList < freeItemsListSize && freeItemsListSize != 0) {
            return positionOnSmallList + 1
        } else if (positionOnSmallList >= freeItemsListSize && freeItemsListSize != 0) {
            return positionOnSmallList + 2
        } else {
            return positionOnSmallList + 1
        }
    }


    /**
     * Prepares the shared element transition from and back to the grid fragment.
     */
    private fun prepareEnterSharedElementTransition() {

        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.image_shared_element_transition)

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    val view =  viewPager!!.findViewHolderForAdapterPosition(viewPagerViewModel.getBookPositionOnDownloadedList().value!!)
                    sharedElements[names[0]] = view!!.itemView.findViewById(R.id.image_book)
                }
            })


    }

    fun ViewPager2.findViewHolderForAdapterPosition(position: Int): RecyclerView.ViewHolder? {
        return (getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position)
    }



}