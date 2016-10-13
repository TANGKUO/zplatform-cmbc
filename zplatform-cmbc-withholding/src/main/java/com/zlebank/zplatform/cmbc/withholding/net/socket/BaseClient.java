package com.zlebank.zplatform.cmbc.withholding.net.socket;

import com.zlebank.zplatform.cmbc.withholding.net.Client;
import com.zlebank.zplatform.cmbc.withholding.net.ReceiveProcessor;

public abstract class BaseClient implements Client {
	public abstract void setReceiveProcessor(ReceiveProcessor receiveProcessor);
}
