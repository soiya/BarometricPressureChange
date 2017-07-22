package com.hobby.soiya.barometricpressurechange;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class PascalsAdapter extends RecyclerView.Adapter<PascalsAdapter.MyViewHolder>{

    public List<Pascal> pascalsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pascal, date;
        public MyViewHolder(View itemView) {
            super(itemView);
            pascal = (TextView) itemView.findViewById(R.id.pascal);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    public PascalsAdapter(List<Pascal> pascalsList){
        this.pascalsList = pascalsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 表示するレイアウトを設定
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pascal_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // データ表示
        Pascal pascal = pascalsList.get(position);
        holder.pascal.setText(String.valueOf(pascal.getPascal()));
        holder.date.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(pascal.getDate()));

    }

    @Override
    public int getItemCount() {
        return pascalsList.size();
    }

}
