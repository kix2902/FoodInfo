package es.kix2902.foodinfo;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.kix2902.foodinfo.database.ProductsTable;
import es.kix2902.foodinfo.helpers.CursorRecyclerAdapter;

public class DatabaseAdapter extends CursorRecyclerAdapter<DatabaseAdapter.ViewHolder> {

    private OnItemClickHistoryListener mListener;

    public interface OnItemClickHistoryListener {
        public void OnItemClickHistory(Cursor cursor);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.txtName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCursor().moveToPosition(getAdapterPosition());
                    mListener.OnItemClickHistory(getCursor());
                }
            });
        }
    }

    public DatabaseAdapter(Cursor cursor, OnItemClickHistoryListener listener) {
        super(cursor);
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.textView.setText(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_NAME)));
    }
}
