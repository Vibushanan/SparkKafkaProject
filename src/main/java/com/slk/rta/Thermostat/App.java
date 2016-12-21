package com.slk.rta.Thermostat;

/**
 * Hello world!
 *
 */
public class App 
{
	public int solution(int N) {
        // write your code in Java SE 8
       String bin = ""; 
        int gap=0,maxgap = 0;
        while (N>0 && N%2 == 0){
        	bin += N%2;
            N = N/2; 
        }
       
        while (N>0){
         
            int rem = N%2;
            N=N/2;
            bin += rem;
            if(rem == 1){
             
            while(N>0 && N%2 == 0){
            	bin += N%2;
            	N=N/2;
                
                gap++;
             
            }  
           }
            if(gap > maxgap){
             maxgap = gap;   
             
            }   
            gap=0;
         } 
        System.out.println(bin);
         return maxgap;   
    }
	     
	    public static void main(String a[]){
	   
	    	App dtb = new App();
	        System.out.println(dtb.solution(561892));
	    }
	
}
