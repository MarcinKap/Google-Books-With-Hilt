package com.example.androidgooglebooksapihilt.views.fragments

import BookListAdapter
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.androidgooglebooksapihilt.R
import com.example.androidgooglebooksapihilt.databinding.FragmentBookListBinding
import com.example.androidgooglebooksapihilt.views.viewModels.BooksListViewModel
import com.example.androidgooglebooksapihilt.views.viewModels.ViewPagerViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


@AndroidEntryPoint
class BooksListFragment : Fragment() {

    val booksListViewModel: BooksListViewModel by activityViewModels()
    val viewPagerViewModel: ViewPagerViewModel by activityViewModels()

    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private var timer = Timer()
    private val delay: Long = 1000 // Milliseconds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)

        booksListViewModel.getMutableLiveDataObserver().observe(viewLifecycleOwner) {
            binding.recyclerviewBooksList.adapter =
                BookListAdapter(booksListViewModel, viewPagerViewModel, this)
            setGridLayoutManagerInRecyclerView(binding.recyclerviewBooksList)


        }

        prepareTransitions()
        postponeEnterTransition()



//        viewPagerViewModel.getBookPositionInRecyclerView().observe(viewLifecycleOwner){
//            prepareTransitions()
//            postponeEnterTransition()
//        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadBooksOnTextChanged(binding.editTextBooksTitle)
        scrollToPosition()

        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }

    }

    fun downloadBooksOnTextChanged(textInputEditText: TextInputEditText) {

        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (textInputEditText.hasFocus()) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                booksListViewModel.loadList(textInputEditText.text.toString())
                                viewPagerViewModel.setBookPositionInRecyclerView(0)
                                viewPagerViewModel.setBookPositionOnDownloadedList(0)
                            }
                        },
                        delay
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setGridLayoutManagerInRecyclerView(recyclerView: RecyclerView) {
        var spanCount = 2
        if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4
        }
        val gridLayout = GridLayoutManager(activity, spanCount)
        gridLayout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0 || (booksListViewModel.getMutableLiveDataFreeBookListSizeObserver().value != 0 && position == booksListViewModel.getMutableLiveDataFreeBookListSizeObserver().value?.plus(
                        1
                    ) ?: 1)
                ) {
                    return spanCount;
                } else {
                    return 1;
                }
            }
        }
        recyclerView.apply {
            layoutManager = gridLayout
        }
    }


    private fun prepareTransitions() {
        exitTransition = android.transition.TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder = binding.recyclerviewBooksList
                        .findViewHolderForAdapterPosition(viewPagerViewModel.getBookPositionInRecyclerView().value!!)
                        ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] =
                        selectedViewHolder.itemView.findViewById(R.id.image_book)
                }
            })

    }

    private fun scrollToPosition() {
        binding.recyclerviewBooksList.addOnLayoutChangeListener(object :
            View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                println("Pozycja!: " + viewPagerViewModel.getBookPositionInRecyclerView().value!!)
                binding.recyclerviewBooksList.scrollToPosition(viewPagerViewModel.getBookPositionInRecyclerView().value!!)
                binding.recyclerviewBooksList.removeOnLayoutChangeListener(this)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}