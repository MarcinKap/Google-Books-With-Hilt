package com.example.androidgooglebooksapihilt.views.fragments

import BookDetailsAdapter
import android.content.res.Configuration
import android.graphics.drawable.Drawable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.androidgooglebooksapihilt.models.bookDetails.AdditionalInformation
import com.example.androidgooglebooksapihilt.models.bookList.Items
import com.example.androidgooglebooksapihilt.R
import com.example.androidgooglebooksapihilt.databinding.FragmentBookDetailsBinding
import com.example.androidgooglebooksapihilt.databinding.FragmentBookListBinding
import com.example.androidgooglebooksapihilt.databinding.FragmentBookPagerBinding
import com.example.androidgooglebooksapihilt.views.viewModels.BooksListViewModel
import com.example.androidgooglebooksapihilt.views.viewModels.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailsFragment : Fragment() {
    lateinit var singleBook: Items
    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    val viewPagerViewModel : ViewPagerViewModel by activityViewModels()
    val booksListViewModel: BooksListViewModel by activityViewModels()


    companion object {
        fun newInstance(item: Items): BookDetailsFragment {
            val args = Bundle()
            args.putSerializable("item", item)
            val fragment = BookDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)

        singleBook = arguments?.get("item") as Items

        val textViewDescriptionHeader: TextView = binding.bookDetailsDescriptionHeader
        val textViewDescriptionText: TextView = binding.bookDetailsDescription
        val textViewAdditionalInformationHeader: TextView =
            binding.bookDetailsAdditionalInformationsHeader
        val bookImage: ImageView = binding.imageBook
        val recyclerView: RecyclerView = binding.bookDetailsAdditionalInformationsRecyclerView

        binding.bookTitleTextView.text = singleBook.volumeInfo.title
        binding.bookTitleTextView.transitionName = singleBook.etag + "title"
        binding.imageBook.transitionName = singleBook.etag

        textViewAdditionalInformationHeader.text =
            resources.getString(R.string.additional_informations)

        if (singleBook.volumeInfo.description != null) {
            textViewDescriptionHeader.text = resources.getString(R.string.description)
            textViewDescriptionText.text = singleBook.volumeInfo.description
        } else {
            textViewDescriptionHeader.visibility = View.GONE
            textViewDescriptionText.visibility = View.GONE
        }
        val additionalInformationsList = ArrayList<AdditionalInformation>()
        if (singleBook.volumeInfo.authors != null)
            additionalInformationsList.add(
                AdditionalInformation(
                    resources.getString(R.string.author),
                    singleBook.volumeInfo.authors[0]
                )
            )
        if (singleBook.volumeInfo.publisher != null)
            additionalInformationsList.add(
                AdditionalInformation(
                    resources.getString(R.string.publisher),
                    singleBook.volumeInfo.publisher
                )
            )
        if (singleBook.volumeInfo.publishedDate != null)
            additionalInformationsList.add(
                AdditionalInformation(
                    resources.getString(R.string.published_on),
                    singleBook.volumeInfo.publishedDate
                )
            )
        if (singleBook.volumeInfo.pageCount.toString() != null && singleBook.volumeInfo.pageCount != 0)
            additionalInformationsList.add(
                AdditionalInformation(
                    resources.getString(R.string.pages),
                    singleBook.volumeInfo.pageCount.toString()
                )
            )
        recyclerView.adapter = BookDetailsAdapter(additionalInformationsList)

        //Download image
        if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.smallThumbnail != null) {
            Glide
                .with(binding.root)
                .load(singleBook.volumeInfo.imageLinks.smallThumbnail)
                .thumbnail(Glide.with(binding.root.context).load(R.drawable.loading_apple))
                .error(R.drawable.no_photo)
                .fitCenter()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        parentFragment?.startPostponedEnterTransition()
                        return false

                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        parentFragment?.startPostponedEnterTransition()
                        return false
                    }
                })
                .into(bookImage)
        } else {
            bookImage.setImageDrawable(null)
            bookImage.setBackgroundResource(R.drawable.no_photo)
            bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            bookImage.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 0.5f }
            parentFragment?.startPostponedEnterTransition()
        }

        var spanCount = 1
        if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2
        }
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)

        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("singleBook", singleBook)
        outState.putString("currentFragment", "booksDetailsFragment")
        super.onSaveInstanceState(outState)
    }


}