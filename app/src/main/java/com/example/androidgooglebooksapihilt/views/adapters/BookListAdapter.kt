import android.content.Context
import android.graphics.drawable.Drawable
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidgooglebooksapihilt.models.bookList.Items
import com.example.androidgooglebooksapihilt.R
import com.example.androidgooglebooksapihilt.databinding.AdapterSectionBookBinding
import com.example.androidgooglebooksapihilt.databinding.AdapterSingleBookBinding
import com.example.androidgooglebooksapihilt.views.viewModels.BooksListViewModel
import com.example.androidgooglebooksapihilt.views.viewModels.ViewPagerViewModel


class BookListAdapter(
    val booksListViewModel: BooksListViewModel,
    val viewPagerViewModel: ViewPagerViewModel,
    val fragment: Fragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val freeBooksListSize = booksListViewModel.getMutableLiveDataFreeBookListSizeObserver().value
    val paidBooksListSize = booksListViewModel.getMutableLiveDataPaidBookListSizeObserver().value
    val itemsList = booksListViewModel.getMutableLiveDataObserver().value?.items
    lateinit var context: Context
    private var viewHolderListener: ViewHolderListener? = null

    //Comment: Setting ViewHolder - done
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        viewHolderListener = ViewHolderListenerImpl(booksListViewModel, viewPagerViewModel, fragment)
        context = parent.context
        if (viewType == 0) { //Create section with title (Free/Paid books)
            return BookSectionViewHolder(
                AdapterSectionBookBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else { //Create section with book
            return SingleBookViewHolder(
                AdapterSingleBookBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    //Comment: Run bind methods - done
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Comment: holder.itemViewType == 0 -> section with Title (free or paid books)
        if (holder.itemViewType == 0) {
            if (freeBooksListSize != null && position < freeBooksListSize + 1 && freeBooksListSize != 0) {
                //Comment: Run bind inside BookSectionViewHolder and inside set title "Free books"
                (holder as BookSectionViewHolder).bind(context.getString(R.string.free_book_list))
            } else {
                //Comment: Run bind inside BookSectionViewHolder and inside set title "Paid books"
                (holder as BookSectionViewHolder).bind(context.getString(R.string.paid_book_list))
            }
        } else {
            val singleBook = getSingleBook(position)
            if (singleBook == null) {
                return
            } else {
                (holder as SingleBookViewHolder).bind(singleBook)
            }
        }
    }

    //Comment: Change number items in recyclerView - done
    override fun getItemCount(): Int {
        if (itemsList?.size != null) {
            if (freeBooksListSize == 0 || paidBooksListSize == 0) {
                return (itemsList.size) + 1
            } else {
                return (itemsList.size) + 2
            }
        } else {
            return 0
        }
    }

    //Comment: Definition of view type (Section or Item)
    override fun getItemViewType(position: Int): Int {
        if (position == 0 || (freeBooksListSize != null && freeBooksListSize != 0 && position == freeBooksListSize + 1)) {
            return 0 //Section
        } else {
            return 1 //Item Book
        }
    }


    //Comment: ViewHolder with title (Paid or free books) - done
    class BookSectionViewHolder(val binding: AdapterSectionBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //create section book list adapter
        fun bind(title: String) {
            binding.sectionTitle.text = title
        }
    }

    //Comment: ViewHolder with book
    inner class SingleBookViewHolder(val binding: AdapterSingleBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val bookImage = binding.imageBook
        private val bookTitle = binding.bookTitleTextView
        private val bookContainer = binding.singleBook

        //create single book adapter
        fun bind(singleBook: Items) {
            bookTitle.text = singleBook.volumeInfo.title
            bookTitle.transitionName = singleBook.etag + "title"
            bookImage.transitionName = singleBook.etag

            setImage(singleBook)

            bookContainer.setOnClickListener {
                    view ->
                viewHolderListener?.onItemClicked(
                    view,
                    adapterPosition
                )
            }

//            bookImage.transitionName = java.lang.String.valueOf(singleBook)
//            bookTitle.transitionName = singleBook.etag + "title"
//            bookImage.transitionName = singleBook.etag
        }

        private fun setImage(singleBook: Items) {
            //Download image
            if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.thumbnail != null) {
                bookImage.setImageDrawable(null)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                bookImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                Glide
                    .with(itemView)
                    .load(singleBook.volumeInfo.imageLinks.thumbnail)
                    .thumbnail(Glide.with(itemView.context).load(R.drawable.loading_apple))
                    .error(R.drawable.no_photo)
                    .fitCenter()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewHolderListener?.onLoadCompleted(bookImage, adapterPosition)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewHolderListener?.onLoadCompleted(bookImage, adapterPosition)
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
            }
        }


    }

    private fun getSingleBook(position: Int): Items? {
        if (position < freeBooksListSize?.plus(1) ?: 0 && freeBooksListSize != 0) {
            return itemsList?.get(position - 1)
        } else if (position >= freeBooksListSize?.plus(1) ?: 0 && freeBooksListSize != 0) {
            return itemsList?.get(position - 2)
        } else {
            return itemsList?.get(position - 1)
        }
    }

    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     */
    interface ViewHolderListener {
        fun onLoadCompleted(view: ImageView?, adapterPosition: Int)
        fun onItemClicked(view: View?, adapterPosition: Int)
    }

    /**
     * Default [ViewHolderListener] implementation.
     */
    class ViewHolderListenerImpl(
        val booksListViewModel: BooksListViewModel,
        val viewPagerViewModel: ViewPagerViewModel,
        val fragment: Fragment
    ) : ViewHolderListener {

        override fun onLoadCompleted(view: ImageView?, adapterPosition: Int) {

        }

        override fun onItemClicked(view: View?, adapterPosition: Int) {


            viewPagerViewModel.setBookPositionInRecyclerView(adapterPosition)

            (fragment.exitTransition as TransitionSet?)!!.excludeTarget(view, true)


            val bookImage: ImageView = view!!.findViewById(R.id.image_book)
            val bookTitle: TextView = view.findViewById(R.id.book_title_text_view)



            val position = getBookPositionOnDownloadedListBasedFromPositionInRecyclerView(
                adapterPosition,
                booksListViewModel
            )
            viewPagerViewModel.setBookPositionOnDownloadedList(position)


            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
//            (fragment.getExitTransition() as TransitionSet).excludeTarget(view, true)

            val extras = FragmentNavigatorExtras(
                bookImage to bookImage.transitionName,
                bookTitle to bookTitle.transitionName
            )
            Navigation.findNavController(view!!)
                .navigate(
                    R.id.action_booksListFragment_to_bookPagerFragment,
                    null,
                    null,
                    extras
                )


//            val viewPagerViewModel : ViewPagerViewModel by activityViewModels()
            // Update the position.
        }

        fun getBookPositionOnDownloadedListBasedFromPositionInRecyclerView(
            position: Int,
            booksListViewModel: BooksListViewModel
        ): Int {
            val freeItemsListSize =
                booksListViewModel.getMutableLiveDataFreeBookListSizeObserver().value

            if (position <= freeItemsListSize!! && freeItemsListSize != 0) {
                return position - 1
            } else if (position >= freeItemsListSize + 1 && freeItemsListSize != 0) {
                return position - 2
            } else {
                return position - 1
            }
        }


    }


}


