package com.akshayomkar.instavansporter;

/**
 * Created by Akshay on 3/6/2016.
 */
public class Job {

    String str_src,str_dest,src_lat,src_lng,dest_lat,dest_lng,amount,num_porter,status,porterslist,src_time,job_id;

    public String getSrc_time() {
        return src_time;
    }

    public void setSrc_time(String src_time) {
        this.src_time = src_time;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public Job() {
        this.str_src = "";
        this.str_dest = "";
        this.src_lat = "";
        this.src_lng = "";
        this.dest_lat = "";
        this.dest_lng = "";
        this.amount = "";
        this.num_porter = "";
        this.status = "0";
        this.porterslist = "";
        this.src_time = "";
        this.job_id = "";
    }

    public String getStr_src() {
        return str_src;
    }

    public void setStr_src(String str_src) {
        this.str_src = str_src;
    }

    public String getStr_dest() {
        return str_dest;
    }

    public void setStr_dest(String str_dest) {
        this.str_dest = str_dest;
    }

    public String getSrc_lat() {
        return src_lat;
    }

    public void setSrc_lat(String src_lat) {
        this.src_lat = src_lat;
    }

    public String getSrc_lng() {
        return src_lng;
    }

    public void setSrc_lng(String src_lng) {
        this.src_lng = src_lng;
    }

    public String getDest_lat() {
        return dest_lat;
    }

    public void setDest_lat(String dest_lat) {
        this.dest_lat = dest_lat;
    }

    public String getDest_lng() {
        return dest_lng;
    }

    public void setDest_lng(String dest_lng) {
        this.dest_lng = dest_lng;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNum_porter() {
        return num_porter;
    }

    public void setNum_porter(String num_porter) {
        this.num_porter = num_porter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPorterslist() {
        return porterslist;
    }

    public void setPorterslist(String porterslist) {
        this.porterslist = porterslist;
    }
}
