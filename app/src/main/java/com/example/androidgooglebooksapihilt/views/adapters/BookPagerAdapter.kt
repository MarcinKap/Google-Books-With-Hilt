import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidgooglebooksapihilt.models.bookList.Items

import com.example.androidgooglebooksapihilt.views.fragments.BookDetailsFragment
import com.example.androidgooglebooksapihilt.views.viewModels.BooksListViewModel
import com.example.androidgooglebooksapihilt.views.viewModels.ViewPagerViewModel
import javax.inject.Inject

class BookPagerAdapter(fragment : Fragment, val booksListViewModel: BooksListViewModel) : FragmentStateAdapter(fragment) {

    val bookList = booksListViewModel.getMutableLiveDataObserver().value!!.items

    override fun getItemCount(): Int = bookList.size


    override fun createFragment(position: Int): Fragment = BookDetailsFragment.newInstance(bookList[position])




}