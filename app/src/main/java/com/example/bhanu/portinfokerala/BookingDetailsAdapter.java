package com.example.bhanu.portinfokerala;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BookingDetailsAdapter extends RecyclerView.Adapter<BookingDetailsAdapter.BookingDetailsHolder>{

    public interface OnItemClickListener {

        void onItemClick(BookingDetailsCard item);

    }

    private final List<BookingDetailsCard> bookingDetailsCardList;

    private final OnItemClickListener listener;


    public BookingDetailsAdapter(List<BookingDetailsCard> bookingDetailsList, OnItemClickListener listener) {

        this.bookingDetailsCardList = bookingDetailsList;
        this.listener = listener;
    }

    @Override
    public BookingDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_details_card, parent, false);
        return new BookingDetailsHolder(v);

    }

    @Override
    public void onBindViewHolder(BookingDetailsHolder holder, int position) {
        BookingDetailsCard item = bookingDetailsCardList.get(position);

        holder.bind(bookingDetailsCardList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return bookingDetailsCardList.size();
    }




    static class BookingDetailsHolder extends RecyclerView.ViewHolder {

        TextView booking_id_tv, booking_date_tv, booking_quantity_tv, booking_request_method_tv;
        CardView cardView;

        public BookingDetailsHolder(View itemView) {
            super(itemView);

            booking_id_tv = itemView.findViewById(R.id.booking_id);
            booking_date_tv = itemView.findViewById(R.id.booking_date);
            booking_quantity_tv = itemView.findViewById(R.id.booking_quantity);
            booking_request_method_tv = itemView.findViewById(R.id.booking_request_method);
            cardView = itemView.findViewById(R.id.booking_details_card);

        }

        public void bind(final BookingDetailsCard item, final OnItemClickListener listener) {

            booking_id_tv.setText("Booking Id: " + item.booking_id);
            booking_date_tv.setText(item.booking_date);
            booking_quantity_tv.setText(item.booking_quantity + " tons");
            booking_request_method_tv.setText(item.booking_request_method);

            if(item.status.equals("0")) {
                cardView.setBackgroundColor(Color.parseColor("#ff4d4d"));
            } else {
                cardView.setBackgroundColor(Color.parseColor("#5cd65c"));
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

}


