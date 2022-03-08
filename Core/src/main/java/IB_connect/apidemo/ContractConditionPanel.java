/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package IB_connect.apidemo;

import IB_connect.ib.client.ContractCondition;
import IB_connect.ib.client.ContractLookuper;

public class ContractConditionPanel<T extends ContractCondition> extends OperatorConditionPanel<T> {
	ContractConditionPanel(T c, ContractLookuper lookuper) {
		super(c);
		
		final ContractCondition condition = m_condition;
		
		add(new ContractLookupButton(condition.conId(), condition.exchange(), lookuper) {
			protected void actionPerformed(int refConId, String refExchId) {
				condition.conId(refConId);
				condition.exchange(refExchId);				
			}  
		});
	}
	
}