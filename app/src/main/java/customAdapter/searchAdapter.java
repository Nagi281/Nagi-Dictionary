package customAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.nagidictionary.R;

import java.util.ArrayList;
import java.util.List;


public class searchAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
    private List<String> arrWords, tempContacts, suggestions;

    public searchAdapter(Context context, int resource, List<String> arrWords) {
        super(context, resource, arrWords);
        this.context = context;
        this.resource = resource;
        this.arrWords = arrWords;
        tempContacts = new ArrayList<>();
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTvSearch = (TextView) convertView.findViewById(R.id.tv_search);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String word = arrWords.get(position);
        viewHolder.mTvSearch.setText(word);
        return convertView;
    }

    public class ViewHolder {
        TextView mTvSearch;
    }
    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String word : tempContacts) {
                    if (word.toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(word);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            ArrayList<String> c =  (ArrayList<String> )results.values ;
            if (results != null && results.count > 0) {
                clear();
                for (String str : c) {
                    add(str);
                    notifyDataSetChanged();
                }
            }
            else{
                clear();
                notifyDataSetChanged();
            }
        }
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return resultValue.toString();
        }
    };
    @Override
    public Filter getFilter(){
        return myFilter;
    }

}
