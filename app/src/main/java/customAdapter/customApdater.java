package customAdapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nagidictionary.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Word;

public class customApdater extends RecyclerView.Adapter<customApdater.WordViewHolder> {
    private List<Word> words;
    private Activity activity;

    public customApdater(List<Word> words) {
//        this.activity = activity;
        this.words = words;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /** Get layout */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        /** Set Value*/
        Word word = words.get(position);
        holder.name.setText(word.getName());
        String content = word.getContent();
        Pattern pattern = Pattern.compile("<ul><li>(.*?)<");
        Matcher matcher = pattern.matcher(content);
        String intro = "";
        if (matcher.find()) {
            intro = matcher.group(1);
        } else {
            intro = content;
        }
        holder.intro.setText(Html.fromHtml(intro));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView intro;

        public WordViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            intro = itemView.findViewById(R.id.tv_intro);
        }
    }
}

