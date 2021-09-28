package Infrastructure.AccountManagement;

import java.util.*;

import Infrastructure.OrderManagement.*;
import DataFeed.QuoteDataFeed.*;

public class AccountManagement {

	private double balance;
	private double equity;
	private double floatingPnL;
	private double margin;

	// todo: implement currency balances in account management (currency string, value)
	private Map<String,Double> balances;
	private Map<String,Double> floating;


	public AccountManagement() {
		this.balance = 0.0;
		this.equity = 0.0;
		this.margin = 0.0;
		this.floatingPnL = 0.0;

		this.balances = new HashMap<String, Double>();
		this.floating = new HashMap<String, Double>();
	}

	public AccountManagement(double balance) {
		this.balance = balance;
		this.equity = 0.0;
		this.margin = 0.0;
		this.floatingPnL = 0.0;

		this.balances = new HashMap<String, Double>();
		this.floating = new HashMap<String, Double>();
	}

	public void FetchData(OrderManagement orderManagement, QuoteDataFeed priceDataFeed) {

		double newFloatPnL = orderManagement.GetFloatingPnL(priceDataFeed);
		double changePnL = newFloatPnL - this.floatingPnL;
		this.floatingPnL = newFloatPnL;
		double closedPnL = orderManagement.GetClosedPnL();
		this.balance += closedPnL;
		this.equity = this.balance + this.floatingPnL;
		this.margin = 0.0; // tbu
	}
	
	public void Print()
	{
		System.out.print("{equity: " + Double.toString(this.balance) + ", balance: " + Double.toString(this.balance) + "}\n"); 
	}

	/*
	 * # TODO: Function that does the order filling and pnl calculation at once # to
	 * be a little more efficient def FetchAndFill(self,
	 * orderManagement:om.OrderManagement, priceDataFeed:PriceDataFeed): return 0
	 */
}
