package com.tobi.calculator;

public interface CalculatorCallBack<T> {

	public T operate(String line, T res);
}
