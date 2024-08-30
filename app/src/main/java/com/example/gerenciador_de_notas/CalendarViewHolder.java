package com.example.gerenciador_de_notas;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView dayOfMonth;
    private CalendarAdapter.OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView){
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    public void onClick(View view) {
        onItemListener.onItemClick(getAbsoluteAdapterPosition(), (String) dayOfMonth.getText());
    }
}
