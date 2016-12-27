import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by yidu on 12/12/16.
 */
public class EntityCrawoer {
    public static void main(String[] args){
      String key = "";//add your own key
        String osType = System.getProperty("os.name");
        String storePath = "";
        if(osType.contains("Windows")){
            storePath = "D:\\crunchbase\\crunchbase_org\\";
        }else{
            storePath = "/Users/yidu/Downloads/crunchbase/";
        }
        try {
            for(int i = 1; i <= 4861; i ++) {//4861
                String s = Request.Get("https://api.crunchbase.com/v/3/organizations?page="+i+"&user_key="+key)
                        .execute().returnContent().asString();
                JSONObject jobj = new JSONObject(s);
                FileUtils.writeStringToFile(new File(storePath +"organization" + i), jobj.toString(), Charset.defaultCharset(), false);
//                System.out.println(jobj.toString());
                Thread.sleep(1000);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
