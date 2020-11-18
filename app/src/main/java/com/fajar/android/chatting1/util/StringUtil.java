/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.android.chatting1.util;

import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Republic Of Gamers
 */
public class StringUtil {
    static final Random RANDOM = new Random();
    
    public static String randomNumber(){
        return String.valueOf(RANDOM.nextInt(899)+100);
    }
      public static String randomNumber(int min, int max){
        return String.valueOf(RANDOM.nextInt(max)+min);
    }

    public static String randomUUID() {
       return UUID.randomUUID().toString();
    }
    
    public static String beautifyNominal(Object Int) {
		if(Int == null) {
			return "0";
		}
		String[] rawNominal = Int.toString().split("\\.");
		String nominal = rawNominal[0];
		String result = "";
		if (nominal.length() > 3) {
			int nol = 0;
			for (int i = nominal.length() - 1; i > 0; i--) {
				nol++;
				result = nominal.charAt(i) + result;
				if (nol == 3) {
					result = "." + result;
					nol = 0;
				}

			}
			result = nominal.charAt(0) + result;
		} else {
			result = Int.toString();
		}

		if (rawNominal.length > 1) {
			result = result + "," + rawNominal[1];
		}

		return result;
	}
	
    
}
