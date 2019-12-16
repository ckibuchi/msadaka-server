package msadaka.utils;

import msadaka.models.Payment;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class Functions {
    public static JSONObject data = new JSONObject();
    public static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");
    public static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    public  Functions()
    {}
    public JSONObject prepareLNMRequest(String msisdn,String amt, String account,String timestamp,String shortcode,String payBill,String passkey,String callbackurl)
    {

        try {



            System.out.println("Phone: "+msisdn);
            System.out.println("Phone2: 254"+msisdn.substring(msisdn.length() - 9));
            byte[] message  =(shortcode+passkey+timestamp).getBytes("UTF-8"); //base64.encode(Shortcode:Passkey:Timestamp)
            String password =  Base64.getEncoder().encodeToString(message);
            password=password.replaceAll("\n","");
            data.put("BusinessShortCode", shortcode);
            data.put("Password",password);
            data.put("Timestamp", timestamp);
            data.put("TransactionType", "CustomerPayBillOnline");
            data.put("Amount",amt);
            data.put("PartyA","254"+msisdn.substring(msisdn.length() - 9));
            data.put("PartyB", payBill);
            data.put("PhoneNumber", "254"+msisdn.substring(msisdn.length() - 9));
            data.put("CallBackURL", callbackurl);
            data.put("AccountReference",account);
            data.put("TransactionDesc",account);



        }
        catch(Exception e)
        {

            e.printStackTrace();
        }

        return  data;
    }

    public void sendSMS(Payment payment,String username,String apikey)
    {



        // Specify the numbers that you want to send to in a comma-separated list
        // Please ensure you include the country code (+254 for Kenya in this case)
        String recipients = payment.getMsisdn();
        String message="";

      /*  if(payment.getError_code2().equalsIgnoreCase("0"))
        // And of course we want our recipients to know what we really do
        {
            message = "Dear Customer,\n" +
                    "You have successfully sent " + ". Thanks for using msadaka";
        }
        else if(payment.getError_code2().equalsIgnoreCase("1032"))
        {
            message = "Dear Customer,\n" +
                     "Please note that you have cancelled the transaction. Thanks for trying msadaka";

        }
        else
        {
            message = "Dear Customer,\n" +
                    "We are sorry that an error occured. Please try again later..";
        }*/
        // Create a new instance of our awesome gateway class
        AfricasTalkingGateway gateway  = new AfricasTalkingGateway(username,apikey);

        /*************************************************************************************
         NOTE: If connecting to the sandbox:

         1. Use "sandbox" as the username
         2. Use the apiKey generated from your sandbox application
         https://account.africastalking.com/apps/sandbox/settings/key
         3. Add the "sandbox" flag to the constructor

         AfricasTalkingGateway gateway = new AfricasTalkingGateway(username, apiKey, "sandbox");
         **************************************************************************************/

        // Thats it, hit send and we'll take care of the rest. Any errors will
        // be captured in the Exception class below
        try {
            JSONArray results = gateway.sendMessage(recipients, message);
System.out.println("SMS Resp: "+results);
            for( int i = 0; i < results.length(); ++i ) {
                JSONObject result = results.getJSONObject(i);
                System.out.print(result.getString("status") + ","); // status is either "Success" or "error message"
                System.out.print(result.getLong("statusCode") + ",");
                System.out.print(result.getString("number") + ",");
                System.out.print(result.getString("messageId") + ",");
                System.out.println(result.getString("cost"));
            }
        } catch (Exception e) {
            System.out.println("Encountered an error while sending " + e.getMessage());
        }
    }


}
