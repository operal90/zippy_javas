//package com.macrotel.zippyworld_test.provider;
//
//import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class QRIOSConnect {
//    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
//    public Object dataConnectEp(String phoneNumber, double amount, String sku, String operationId){
//        List<Object> result = new ArrayList<>();
//        int dataAmount = (int) amount;
//        if(Objects.equals(network, "MTN")){
//            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"mtn_bundle_vending"));
//        } else if (Objects.equals(network,"AIRTEL")) {
//            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"airtel_bundle_vending"));
//        }
//        else if (Objects.equals(network,"9MOBILE")) {
//            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"nine_mobile_bundle_vending"));
//        }
//        else if (Objects.equals(network,"GLO")) {
//            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"glo_bundle_vending"));
//        }
//        return result;
//    }
//}
