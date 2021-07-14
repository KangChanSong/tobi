package com.tobi.calculator;

import java.io.IOException;

public class CalculatorClient {
	
	CalculatorTemplate calculatorTemplate;
	
	public void setCalculatorTemplate(CalculatorTemplate calculatorTemplate) {
		this.calculatorTemplate = calculatorTemplate;
	}

	public Integer calcSum(String filePath) throws IOException{
		
		CalculatorCallBack<Integer> callBack = new CalculatorCallBack<Integer>() {
			@Override
			public Integer operate(String line, Integer res) {
				// TODO Auto-generated method stub
				return Integer.valueOf(line) + res;
			}
		};
		return calculatorTemplate.calculate(filePath, callBack, 0); 
	}
	
	public Integer calcMul(String filePath) throws IOException{
		

		CalculatorCallBack<Integer> callBack = new CalculatorCallBack<Integer>() {
			@Override
			public Integer operate(String line, Integer res) {
				// TODO Auto-generated method stub
				return Integer.valueOf(line) * res;
			}
		};
		return calculatorTemplate.calculate(filePath, callBack, 1); 
	}
	
	public String stringMerge(String filePath) throws IOException{
		
		CalculatorCallBack<String> callBack = new CalculatorCallBack<String>() {
			
			@Override
			public String operate(String line, String res) {
				// TODO Auto-generated method stub
				return line + " " + res;
			}
			
		};
		
		return calculatorTemplate.calculate(filePath, callBack, "");
	}
	
}
