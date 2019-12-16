import com.algolia.search.APIClient;
import com.algolia.search.ApacheAPIClientBuilder;
import com.algolia.search.Index;
import com.algolia.search.objects.IndexSettings;
import com.algolia.search.objects.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import msadaka.models.Church;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class test {
   public  static void main(String[] args)
    {
        try {
            java.security.Security.setProperty("networkaddress.cache.ttl", "60");
            APIClient client =
                    new ApacheAPIClientBuilder("V6SCF4H8H6", "b294ae630f797e9a055c700753b6f4f4").build();

            Index<Church> index = client.initIndex("churches_prod", Church.class);
            index.setSettings(
                    new IndexSettings().setExactOnSingleWordQuery("id")
                            .setQueryType("prefixNone")
            );

            List<Church> churches=index.search(new Query("14")).getHits();
            if(churches.isEmpty())
            {
                System.out.println("No Church.. ");

            }
            else {

                System.out.println("Deleting.. ");
            }


        }
        catch(Exception e)
        {e.printStackTrace();}

    }
}

