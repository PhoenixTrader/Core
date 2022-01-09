/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package IB_connect.ib.contracts;

import IB_connect.ib.client.Contract;
import IB_connect.ib.client.Types.SecType;

public class StkContract extends Contract {
    public StkContract(String symbol) {
        symbol(symbol);
        secType(SecType.STK.name());
        exchange("SMART");
        currency("USD");
    }
}
