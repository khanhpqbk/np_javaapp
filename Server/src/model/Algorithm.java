/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import java.util.ArrayList;

public class Algorithm {

    ArrayList<Item> tab;

    public Algorithm(ArrayList<Item> tab) {
        this.tab = tab;

        work(0, tab.size()-1);
    }

    
    float countFreq(int start, int end){

        float sum = 0;
        for (int i = start; i < end; i++) {
            sum += tab.get(i).freq;
        }
        return sum;
    }

    
    public void work(int begin, int end){

        Float best_dif = Float.MAX_VALUE;
        int middle = 0; 
        Float dif1 = 0f, dif2 = 0f; 

        if ( (end - begin) == 1 ){ 
            tab.get(begin).code += "1";
            tab.get(end).code += "0";
            return;
        }
        else if ( begin == end){
            return;

        }


       
        for (int i = begin; i < end; i++) {


            dif1 = countFreq(begin, i);
            dif2 = countFreq(i+1, end);

            if (Math.abs(dif1 - dif2) < best_dif){
                best_dif = Math.abs(dif1 - dif2);
                //System.out.println(best_dif);

                middle = i;
            }

        }
        

        for (int i = begin; i <= end; i++) {
            if (i <= middle) tab.get(i).code += "1";
            else
                tab.get(i).code += "0";
        }

       
        work(begin, middle);
        work(middle+1, end);


    }
}
