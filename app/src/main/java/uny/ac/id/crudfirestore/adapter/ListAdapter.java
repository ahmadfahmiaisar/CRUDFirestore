package uny.ac.id.crudfirestore.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uny.ac.id.crudfirestore.ListActivity;
import uny.ac.id.crudfirestore.R;
import uny.ac.id.crudfirestore.model.ModelResponse;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

//    Context context;
    List<ModelResponse> modelList;
    ListActivity listActivity;

    public ListAdapter(ListActivity listActivity, List<ModelResponse> modelList) {
        this.modelList = modelList;
        this.listActivity = listActivity;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = modelList.get(position).getTitle();
                String desc = modelList.get(position).getDescription();
                Toast.makeText(listActivity, title+"\n"+desc, Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.tv_title.setText(modelList.get(i).getTitle());
        viewHolder.tv_desc.setText(modelList.get(i).getDescription());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_desc;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mclickListener.onItemClick(v, getAdapterPosition());
                }
            });
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_desc = itemView.findViewById(R.id.tv_description);
        }

        private ViewHolder.ClickListener mclickListener;
        public interface ClickListener{
            void onItemClick(View view, int position);
        }
        public void setOnClickListener(ViewHolder.ClickListener clickListener){
            mclickListener = clickListener;
        }
    }
}
