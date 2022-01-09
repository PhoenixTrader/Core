/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package IB_connect.apidemo;

import IB_connect.apidemo.util.VerticalPanel;
import IB_connect.ib.client.OrderCondition;

public abstract class OnOKPanel extends VerticalPanel {
	public abstract OrderCondition onOK();
}