package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.tobi.calculator.CalculatorClient;
import com.tobi.calculator.CalculatorTemplate;

public class CalcTest {
	
	CalculatorClient client = null;
	
	@Before
	public void setUp() {
		client = new CalculatorClient();
		client.setCalculatorTemplate(new CalculatorTemplate());
	}
	
	@Test
	public void sumOfNumbers() throws IOException{
		int sum = 0;
		sum = client.calcSum(getClass().getResource("numbers.txt").getPath());
		int expected  = 1+2+3+4;
		
		assertThat( sum, is(expected));
	}
	
	@Test
	public void subsOfNumbers() throws IOException{
		int mul = 0;
		mul = client.calcMul(getClass().getResource("numbers.txt").getPath());
		int expected  = 1*2*3*4;
		
		assertThat( mul, is(expected));
	}
	
	@Test
	public void stringMerge() throws IOException{
		
		String result = "";
		result = client.stringMerge(getClass().getResource("numbers.txt").getPath());
		String expected = "1 2 3 4";
		
		assertThat(result,is(expected));
		
	}
}
