package Tests;

//import IB_connect.ib.client.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import IB_connect.ib.client.Bar;
import IB_connect.ib.client.CommissionReport;
import IB_connect.ib.client.Contract;
import IB_connect.ib.client.ContractDescription;
import IB_connect.ib.client.ContractDetails;
import IB_connect.ib.client.DeltaNeutralContract;
import IB_connect.ib.client.DepthMktDataDescription;
import IB_connect.ib.client.EClientSocket;
import IB_connect.ib.client.EJavaSignal;
import IB_connect.ib.client.EReader;
import IB_connect.ib.client.EWrapper;
import IB_connect.ib.client.EWrapperMsgGenerator;
import IB_connect.ib.client.Execution;
import IB_connect.ib.client.FamilyCode;
import IB_connect.ib.client.HistogramEntry;
import IB_connect.ib.client.HistoricalTick;
import IB_connect.ib.client.HistoricalTickBidAsk;
import IB_connect.ib.client.HistoricalTickLast;
import IB_connect.ib.client.NewsProvider;
import IB_connect.ib.client.Order;
import IB_connect.ib.client.OrderState;
import IB_connect.ib.client.PriceIncrement;
import IB_connect.ib.client.SoftDollarTier;
import IB_connect.ib.client.TickAttrib;
import IB_connect.ib.client.TickAttribBidAsk;
import IB_connect.ib.client.TickAttribLast;
import IB_connect.ib.client.TickType;

import java.util.Set;

public class API_get_ibkr_data implements EWrapper {

    EJavaSignal m_signal = new EJavaSignal();
    EClientSocket m_client = new EClientSocket(this, m_signal);

    private double high = Double.MAX_VALUE;
    private double low = -Double.MAX_VALUE;

    public static void main(String[] args) {
        new API_get_ibkr_data().run();
        //new API_get_ibkr_data().run_v2();
    }

    public Map<LinkedList<Integer>, ArrayList<Object>> test;
    public LinkedList<String> end_date_EReader = new LinkedList<String>();
    private String enddate = "20201028 16:00:00";
    private String enddate_loop = "20201027";

    public void run() {
        m_client.eConnect("127.0.0.1", 4002,0);   //7497(TWS)
        final EReader reader = new EReader(m_client, m_signal);
        reader.start();
        
        new Thread() {
            @Override
            public void run() {
                while (m_client.isConnected() && (!end_date_EReader.contains(enddate_loop))) {
                    m_signal.waitForSignal();
                    try {
                        reader.processMsgs();
                        System.out.println("Try to get data");
                        test = reader.getDATA();
                        end_date_EReader = reader.currentDate();
                        //System.out.println(end_date_EReader);
                        //System.out.println(test);
                        if (end_date_EReader.size() > 0){
                            System.out.println(end_date_EReader.get((end_date_EReader.size() - 1)));
                            break;
                        }
                        if (end_date_EReader.contains(enddate_loop)){
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                        if (end_date_EReader.contains(enddate_loop)){
                            break;
                        }
                    }
                }
            }
        }.start();
    }

    public void run_v2() {
        m_client.eConnect("127.0.0.1", 4002,0);   //7497(TWS)
        final EReader reader = new EReader(m_client, m_signal);
        reader.start();
        
        new Thread() {
            @Override
            public void run() {
                do
                {
                    System.out.println("Starting");
                    m_signal.waitForSignal();
                    try {
                        reader.processMsgs();
                        test = reader.getDATA();
                        System.out.println(test);
                        System.out.println(end_date_EReader.get((end_date_EReader.size() - 1)));
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                }while (end_date_EReader.get((end_date_EReader.size() - 1)) != enddate);
            }
        }.start();
    }



    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {
        System.out.println("HistoricalDataUpdate. " + EWrapperMsgGenerator.historicalData(reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap()));
    }

    @Override
    public void nextValidId(int orderId) {
        System.out.println("id "+orderId);
        //Contract c = new StkContract("IBKR");
        Contract contract = new Contract();
        contract = new Contract();
        contract.symbol("EUR");
        contract.secType("CASH");
        contract.exchange("IDEALPRO"); 
        //contract.exchange("ISLAND");
        contract.currency("USD");
        contract.primaryExch("IDEALPRO");
        
        int counter = 1;
        do
        {

            m_client.reqMarketDataType(1);  // switch to delayed-frozen data if live is not available
            //Types
            //1 - LIVE
            //2 - Frozen
            //3 - Delayed
            //4 - Delayed Frozen
            int liveFeed = 1;
            if (liveFeed == 0){

                System.out.println("Historic data");
                m_client.reqHistoricalData(1, contract, this.enddate, "1 W", "1 day", "BID", 1, 1, false, null);
                //System.out.println(m_client.getData());    
                //m_client.reqRealTimeBars(3001, contract, 1, "MIDPOINT", true, null);

                
            }
            else{
                System.out.println("Marktdata");
                m_client.reqMktData(1, contract, "", false, false, null);
                m_client.reqRealTimeBars(3001, contract, 5, "MIDPOINT", true, null);
    
                // BID, MIDPOINT, ASK
                //historicalDataEnd(1,"20201021 16:00:00", "20201028 16:00:00");
    
                //EDecoder
                //EMessage
                //PreV100MessageReader


            }
            //m_client.reqHistoricalTicks(counter, contract, "20170712 21:39:33", null, 10, "MIDPOINT", 1, true, null);
            counter -= 1;
        } while(counter > 0);
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        System.out.println("HistoricalDataEnd. " + EWrapperMsgGenerator.historicalDataEnd(reqId, startDateStr, endDateStr));
    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        System.out.println("HistoricalData:  " + EWrapperMsgGenerator.historicalData(reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap()));
    }

    // @Override //- why no override? old code versio?!?
    // public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
    //     //if being run on the next calendar day, this works
    //     if (LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE).equals(date)){
    //         this.high = high;
    //         this.low = low;
    //         System.out.println(date + " h: " + high + " l: " +low);
    //     }
    // }



    @Override
    public void error(int id, int errorCode, String errorMsg) {
        System.out.println(id + " " + errorCode + " " + errorMsg);
    }

        
    //@Override - why no override? old code versio?!?
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {

        System.out.println("id: "+tickerId + " " + TickType.getField(field) + " price: "+price);
        if (field == TickType.LAST.index()){
            if (price > high) {
                System.out.println("buy");
            }
            if (price < low){
                System.out.println("sell");
            }
        }

    }
    //implementation rest of EWrapper

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
        System.out.println("Tick Price. Ticker Id:"+tickerId+", Field: "+field+", Price: "+price+", CanAutoExecute: "+ attribs.canAutoExecute() +", pastLimit: " + attribs.pastLimit() + ", pre-open: " + attribs.preOpen());
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        System.out.println("Tick Size. Ticker Id:" + tickerId + ", Field: " + field + ", Size: " + size);
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice,
            double pvDividend, double gamma, double vega, double theta, double undPrice) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints,
            double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact,
            double dividendsToLastTradeDate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void orderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice,
            int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void openOrderEnd() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updatePortfolio(Contract contract, double position, double marketPrice, double marketValue,
            double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAccountTime(String timeStamp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void accountDownloadEnd(String accountName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contractDetailsEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void execDetailsEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price,
            int size, boolean isSmartDepth) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void managedAccounts(String accountsList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void scannerParameters(String xml) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark,
            String projection, String legsStr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void scannerDataEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void currentTime(long time) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fundamentalData(int reqId, String data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void position(String account, Contract contract, double pos, double avgCost) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void positionEnd() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void accountSummaryEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void verifyMessageAPI(String apiData) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void displayGroupList(int reqId, String groups) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void error(Exception e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void error(String str) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectionClosed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectAck() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, double pos,
            double avgCost) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void positionMultiEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value,
            String currency) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void accountUpdateMultiEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId,
            String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline,
            String extraData) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Entry<String, Character>> theMap) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pnlSingle(int reqId, int pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, int size,
            TickAttribLast tickAttribLast, String exchange, String specialConditions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, int bidSize, int askSize,
            TickAttribBidAsk tickAttribBidAsk) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void completedOrdersEnd() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume,
            double wap, int count) {
        // TODO Auto-generated method stub
        
    }
}
