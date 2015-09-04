package es.kix2902.foodinfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.kix2902.foodinfo.data.Product;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ItemViewHolder> {

    private OnItemClickResultListener mListener;

    public interface OnItemClickResultListener {
        public void OnItemClickResult(Product product);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public ItemViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.txtName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnItemClickResult(products.get(getAdapterPosition()));
                }
            });
        }
    }

    private ArrayList<Product> products;

    public ResultAdapter(ArrayList<Product> products, OnItemClickResultListener listener) {
        this.products = products;
        this.mListener = listener;
    }

    @Override
    public ResultAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultAdapter.ItemViewHolder holder, int position) {
        holder.textView.setText(products.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
