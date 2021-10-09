import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapihilt.R
import com.example.androidgooglebooksapihilt.databinding.AdapterBookDetailsBinding
import com.example.androidgooglebooksapihilt.models.bookDetails.AdditionalInformation

class BookDetailsAdapter(private var additionalInformationsList: ArrayList<AdditionalInformation>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return (CustomViewHolder(
            AdapterBookDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val additionalInformation = additionalInformationsList[position]
        (holder as CustomViewHolder).bind(additionalInformation.title, additionalInformation.text)
    }

    override fun getItemCount(): Int {
        return additionalInformationsList.size
    }

    class CustomViewHolder(val binding: AdapterBookDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //create section book list adapter
        fun bind(title: String, text: String) {
            binding.additionalInformationTitle.text = title
            binding.additionalInformationText.text = text
        }
    }


}