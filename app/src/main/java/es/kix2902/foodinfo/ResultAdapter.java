package es.kix2902.foodinfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ItemViewHolder> {

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public ItemViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.txtName);
        }
    }

    private ArrayList<Item> items;

    public ResultAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public ResultAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultAdapter.ItemViewHolder holder, int position) {
        holder.textView.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
