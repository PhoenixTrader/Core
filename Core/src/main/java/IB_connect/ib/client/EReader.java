/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package IB_connect.ib.client;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;



/**
 * This class reads commands from TWS and passes them to the user defined
 * EWrapper.
 *
 * This class is initialized with a DataInputStream that is connected to the
 * TWS. Messages begin with an ID and any relevant data are passed afterwards.
 */
public class EReader extends Thread {
    private EClientSocket 	m_clientSocket;
    private EReaderSignal m_signal;
    private EDecoder m_processMsgsDecoder;
    private static final EWrapper defaultWrapper = new DefaultEWrapper();
    private static final int IN_BUF_SIZE_DEFAULT = 8192;
    private byte[] m_iBuf = new byte[IN_BUF_SIZE_DEFAULT];
    private int m_iBufLen = 0;
    private final Deque<EMessage> m_msgQueue = new LinkedList<>();
    
    protected boolean isUseV100Plus() {
		return m_clientSocket.isUseV100Plus();
	}

	protected EClient parent()    { return m_clientSocket; }
    private EWrapper eWrapper()         { return parent().wrapper(); }

    /**
     * Construct the EReader.
     * @param parent An EClientSocket connected to TWS.
     * @param signal A callback that informs that there are messages in msg queue.
     */
    public EReader(EClientSocket parent, EReaderSignal signal) {
    	m_clientSocket = parent;
        m_signal = signal;
        m_processMsgsDecoder = new EDecoder(parent.serverVersion(), parent.wrapper(), parent);
    }
    
	public Map<LinkedList<Integer>, ArrayList<Object>> getDATA(){
		LinkedList<Integer> id = null;
		LinkedList<String> date = null;
		LinkedList<Double> open = null;
		LinkedList<Double> high = null;
		LinkedList<Double> low = null;
		LinkedList<Double> close = null;
		LinkedList<Long> volume = null;
		LinkedList<Integer> count = null;
		LinkedList<Double> WAP = null;
		Map<LinkedList<Integer>,ArrayList<Object>> getDATA = new HashMap<>();
		
		ArrayList<Object> ibkrData = new ArrayList<Object>();

		Hashtable<String, LinkedList<Integer>> idDict = new Hashtable<>();
		Hashtable<String, LinkedList<String>> dateDict = new Hashtable<>();
		Hashtable<String, LinkedList<Double>> openDict = new Hashtable<>();
		Hashtable<String, LinkedList<Double>> highDict = new Hashtable<>();
		Hashtable<String, LinkedList<Double>> lowDict = new Hashtable<>();
		Hashtable<String, LinkedList<Double>> closeDict = new Hashtable<>();
		Hashtable<String, LinkedList<Long>> volumeDict = new Hashtable<>();
		Hashtable<String, LinkedList<Integer>> countDict = new Hashtable<>();
		Hashtable<String, LinkedList<Double>> wapDict = new Hashtable<>();

		Map<LinkedList<Integer>,ArrayList<Object>> getDATA2 = new HashMap<>();
		ArrayList<Object> ibkrData2 = new ArrayList<Object>();

		id = m_processMsgsDecoder.fillID;
		date = m_processMsgsDecoder.fillDate;
		open = m_processMsgsDecoder.fillOpen;
		high = m_processMsgsDecoder.fillHigh;
		low = m_processMsgsDecoder.fillLow;
		close = m_processMsgsDecoder.fillClose;
		volume = m_processMsgsDecoder.fillVolume;
		count = m_processMsgsDecoder.fillCount;
		WAP = m_processMsgsDecoder.fillWAP;

		ibkrData.add(date);
		ibkrData.add(open);
		ibkrData.add(high);
		ibkrData.add(low);
		ibkrData.add(close);
		ibkrData.add(volume);
		ibkrData.add(count);
		ibkrData.add(WAP);

		idDict.put("ID", id);
		dateDict.put("Date", date);
		openDict.put("Open", open);
		highDict.put("High", high);
		lowDict.put("Low", low);
		closeDict.put("Close", close);
		volumeDict.put("Volume", volume);
		countDict.put("Count", count);
		wapDict.put("WAP", WAP);
		
		ibkrData2.add(dateDict);
		ibkrData2.add(openDict);
		ibkrData2.add(highDict);
		ibkrData2.add(lowDict);
		ibkrData2.add(closeDict);
		ibkrData2.add(volumeDict);
		ibkrData2.add(countDict);
		ibkrData2.add(wapDict);

		getDATA2.put(id, ibkrData2);
		
		getDATA.put(id, ibkrData);
		
		return getDATA2;
		//return getDATA;
	}

//	public LinkedList<String> getEndDate(){
//		LinkedList<String> fillDataEnddate = null;
//		fillDataEnddate = m_processMsgsDecoder.fillDataEnddate;
//		return fillDataEnddate;
//	}

    /**
     * Read and put messages to the msg queue until interrupted or TWS closes connection.
     */
    @Override
    public void run() {
        try {
            // loop until thread is terminated
            while (!isInterrupted()) {
            	if (!putMessageToQueue())
            		break;
            }
        }
        catch ( Exception ex ) {
        	//if (parent().isConnected()) {
        		if( ex instanceof EOFException ) {
            		eWrapper().error(EClientErrors.NO_VALID_ID, EClientErrors.BAD_LENGTH.code(),
            				EClientErrors.BAD_LENGTH.msg() + " " + ex.getMessage());
        		}
        		else {
        			eWrapper().error( ex);
        		}
        		
        		parent().eDisconnect();
        	//}
        } 
        
        m_signal.issueSignal();
    }

	public boolean putMessageToQueue() throws IOException {
		EMessage msg = readSingleMessage();
		
		if (msg == null)
			return false;
		
		synchronized(m_msgQueue) {
			m_msgQueue.addFirst(msg);
		}
		
		m_signal.issueSignal();
		
		return true;
	}   

	protected EMessage getMsg() {
    	synchronized (m_msgQueue) {
    		return m_msgQueue.isEmpty() ? null : m_msgQueue.removeLast();
		}
    }
	
    static final int MAX_MSG_LENGTH = 0xffffff;

	private static class InvalidMessageLengthException extends IOException {
		private static final long serialVersionUID = 1L;

		InvalidMessageLengthException(String message) {
			super(message);
		}
    }
    
    public void processMsgs() throws IOException {
    	EMessage msg = getMsg();
    	System.out.println("MESSAGE");
    	while (msg != null && m_processMsgsDecoder.processMsg(msg) > 0) {
    		msg = getMsg();
    	}
    }

	private EMessage readSingleMessage() throws IOException {
		if (isUseV100Plus()) {
			int msgSize = m_clientSocket.readInt();

			if (msgSize > MAX_MSG_LENGTH) {
				throw new InvalidMessageLengthException("message is too long: "
						+ msgSize);
			}
			
			byte[] buf = new byte[msgSize];
			
			int offset = 0;
			
			while (offset < msgSize) {
				offset += m_clientSocket.read(buf, offset, msgSize - offset);
			}
						
			return new EMessage(buf, buf.length);
		}
		
		if (m_iBufLen == 0) {
			m_iBufLen = appendIBuf();
		}
				
		int msgSize;
		
		while (true)
			try {
				msgSize = 0;
				if (m_iBufLen > 0) {
				  try (EDecoder decoder = new EDecoder(m_clientSocket.serverVersion(), defaultWrapper)) {
				    msgSize = decoder.processMsg(new EMessage(m_iBuf, m_iBufLen));
				  }
				}
				break;
			} catch (IOException e) {
				if (m_iBufLen >= m_iBuf.length * 3/4) {
					byte[] tmp = new byte[m_iBuf.length * 2];
					
					System.arraycopy(m_iBuf, 0, tmp, 0, m_iBuf.length);
					
					m_iBuf = tmp;
				}
				
				m_iBufLen += appendIBuf();
			}
		
		if (msgSize == 0)
			return null;
		
		
		EMessage msg = new EMessage(m_iBuf, msgSize);
		
		System.arraycopy(Arrays.copyOfRange(m_iBuf, msgSize, m_iBuf.length), 0, m_iBuf, 0, m_iBuf.length - msgSize);
		
		m_iBufLen -= msgSize;
		
		if (m_iBufLen < IN_BUF_SIZE_DEFAULT && m_iBuf.length > IN_BUF_SIZE_DEFAULT) {
			byte[] tmp = new byte[IN_BUF_SIZE_DEFAULT];
			
			System.arraycopy(m_iBuf, 0, tmp, 0, tmp.length);
			
			m_iBuf = tmp;
		}			
		
		return msg;
	}

	protected int appendIBuf() throws IOException {
		return m_clientSocket.read(m_iBuf, m_iBufLen, m_iBuf.length - m_iBufLen);
	}   
}
