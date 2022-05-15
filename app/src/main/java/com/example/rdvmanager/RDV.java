package com.example.rdvmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class RDV implements Parcelable
{

    long id;
    String title, date, time, contact, phone, address;
    int isDone;

    public RDV(){
    }

    public RDV (String title, String date, String time, String contact, String phone, String address, int isDone){
        this.title = title;
        this.date = date;
        this.time = time;
        this.contact = contact;
        this.phone = phone;
        this.address = address;
        this.isDone = isDone;
    }

    public RDV(long id, String title, String date, String time, String contact, String phone, String address, int isDone){
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.contact = contact;
        this.phone = phone;
        this.address = address;
        this.isDone = isDone;
    }

    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }

    public String getTitle(){ return title; }

    public void setTitle(String title){ this.title = title; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getContact(){ return contact; }

    public void setContact(){ this.contact = contact; }

    public String getPhone(){ return phone; }

    public void setPhone(){ this.phone = phone; }

    public String getAddress(){ return address; }

    public void setAddress(){ this.address = address; }

    public int getIsDone() { return isDone; }

    public void setIsDone(int isDone) { this.isDone = isDone; }

    @Override
    public int describeContents(){ return hashCode(); }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(contact);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeInt(isDone);
    }


    public static final Parcelable.Creator<RDV> CREATOR = new Parcelable.Creator<RDV>(){
        @Override
        public RDV createFromParcel(Parcel parcel) { return new RDV(parcel); }
        @Override
        public RDV[] newArray(int size) { return new RDV[size]; }
    };


    public RDV(Parcel parcel){
        id = parcel.readLong();
        title = parcel.readString();
        date = parcel.readString();
        time = parcel.readString();
        contact = parcel.readString();
        phone = parcel.readString();
        address = parcel.readString();
        isDone = parcel.readInt();
    }

}
