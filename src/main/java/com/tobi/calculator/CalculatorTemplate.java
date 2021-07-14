package com.tobi.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CalculatorTemplate {

	public <T> T calculate(String filePath, CalculatorCallBack<T> callback, T initVal) throws IOException{
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filePath));
			T res = initVal;
			String line = null;
			while((line = br.readLine()) != null) {
				res = callback.operate(line , res);
			}
			return res;
		} catch(IOException e) {
			throw e;
		} finally {
			if(	br != null) {
				try {
					br.close();
				} catch(IOException e) {
					
				}
				
			}
		}
		
	}
}
