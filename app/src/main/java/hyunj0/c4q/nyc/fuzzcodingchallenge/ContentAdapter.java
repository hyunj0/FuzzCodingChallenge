package hyunj0.c4q.nyc.fuzzcodingchallenge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContentAdapter extends ArrayAdapter<Content> {

    Context context;
    int resource;
    List<Content> contents;
    LayoutInflater layoutInflater;

    public ContentAdapter(Context context, int resource, List<Content> contents) {
        super(context, resource, contents);
        this.context = context;
        this.resource = resource;
        this.contents = contents;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ContentHolder contentHolder;

        if (view == null) {
            view = layoutInflater.inflate(resource, viewGroup, false);

            contentHolder = new ContentHolder();

            contentHolder.id = (TextView) view.findViewById(R.id.id);
            contentHolder.type = (TextView) view.findViewById(R.id.type);
            contentHolder.date = (TextView) view.findViewById(R.id.date);
            contentHolder.data = (TextView) view.findViewById(R.id.data);

            view.setTag(contentHolder);
        } else {
            contentHolder = (ContentHolder) view.getTag();
        }

        Content content = contents.get(position);

        Log.d("content found", content.toString());

        contentHolder.id.setText(content.getId());
        contentHolder.type.setText(content.getType());
        contentHolder.date.setText(content.getDate());
        contentHolder.data.setText(content.getData());

        return view;
    }

    static class ContentHolder {
        TextView id;
        TextView type;
        TextView date;
        TextView data;
    }
}