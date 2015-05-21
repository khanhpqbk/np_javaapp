/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


/**
 * Created by F0RIS on 11.03.2015.
 */
// 
public  class Item implements Comparable{

    public Character ch;
    public Float freq;
    public String code = new String();

    public Item(Character ch, Float freq) {
        this.ch = ch;
        this.freq = freq;
        code = "";
    }

    Item(){

        ch = '1';
        freq = 0.0f;
        code = "";
    }

    @Override
    public int compareTo(Object o) {

        //
        if (Math.abs(this.freq - ((Item)o).freq) < 0.001)
            return this.ch < ((Item)o).ch? -1: 1;

        return this.freq > ((Item)o).freq? -1:1 ;
    }
}

