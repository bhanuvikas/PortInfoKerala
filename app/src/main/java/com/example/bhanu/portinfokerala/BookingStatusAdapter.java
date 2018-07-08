package com.example.bhanu.portinfokerala;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.BookingStatusHolder>{


    public interface OnItemClickListener {

        void onItemClick(BookingDetailsCard item);

    }


    private final List<BookingDetailsCard> bookingDetailsCardList;

    private final OnItemClickListener listener;

    public BookingStatusAdapter(List<BookingDetailsCard> bookingDetailsList, OnItemClickListener listener) {

        this.bookingDetailsCardList = bookingDetailsList;
        this.listener = listener;
    }

    @Override
    public BookingStatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_details_card, parent, false);
        return new BookingStatusHolder(v);

    }

    @Override
    public void onBindViewHolder(BookingStatusHolder holder, int position) {
        BookingDetailsCard item = bookingDetailsCardList.get(position);

        holder.bind(bookingDetailsCardList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return bookingDetailsCardList.size();
    }


    static class BookingStatusHolder extends RecyclerView.ViewHolder {

        TextView booking_id_tv, booking_date_tv, booking_quantity_tv, booking_request_method_tv;
        CardView cardView;

        public BookingStatusHolder(View itemView) {
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

            if(item.status.equals("2")) {
                cardView.setBackgroundColor(Color.parseColor("#ff4d4d"));
            } else if(item.status.equals("1")){
                cardView.setBackgroundColor(Color.parseColor("#5cd65c"));
            } else {
                cardView.setBackgroundColor(Color.parseColor("#ffff1a"));
            }
            


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }








}
