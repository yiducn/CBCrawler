import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by yidu on 12/12/16.
 */
public class OrganizationParser {
    public static void main(String[] args){
      String key = "";//add your own key
        String osType = System.getProperty("os.name");
        String orgPath;
        String storePath;
        if(osType.contains("Windows")){
            orgPath = "D:\\crunchbase\\crunchbase_org\\";
            storePath = "D:\\crunchbase\\crunchbase_permalink\\";
        }else{
            orgPath = "/Users/yidu/Downloads/crunchbase/";
            storePath = "/Users/yidu/Downloads/crunchbase_permalink/";
        }

        try{
            for(int i = 1; i <= 4861; i ++) {
                String s = FileUtils.readFileToString(new File(orgPath + "organization" + i), Charset.defaultCharset());
                JSONObject jobj = new JSONObject(s);
                JSONObject jdata = jobj.getJSONObject("data");
                JSONArray jitems = jdata.getJSONArray("items");
                for(int j = 0; j < jitems.length(); j ++) {
                    JSONObject jobjitems = jitems.getJSONObject(j);
                    String permalink = jobjitems.getJSONObject("properties").getString("permalink");
                    String uuid = jobjitems.getString("uuid");
                    final int ii = i;
                    final int jj = j;
                    //permalink create uri
                    URI uri = null;
                    boolean hasParsed = true;
                    try {
                        uri = URI.create("https://api.crunchbase.com/v/3/organizations/" + permalink + "?user_key="+key);
                    }catch(IllegalArgumentException ue){
                        System.out.println("URI parse error");
                        hasParsed = false;
                    }
                    if(! hasParsed)
                        continue;
                    Response resp =  Request.Get(uri)
                        .execute().handleResponse(new ResponseHandler<Response>() {
                            @Override
                            public Response handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                                int returnCode = httpResponse.getStatusLine().getStatusCode();
                                System.out.println(permalink+":"+uuid+":"+returnCode);
                                if(returnCode > 200) {
                                    System.out.println("return code:"+returnCode);
                                    return null;
                                }
                                String ss = IOUtils.toString(httpResponse.getEntity().getContent(), Charset.defaultCharset());
                                JSONObject jobjPermalink = new JSONObject(ss);
                                FileUtils.writeStringToFile(new File(storePath + uuid), jobjPermalink.toString(), Charset.defaultCharset(), false);
                                FileUtils.writeStringToFile(new File(storePath + "mapping.txt"), ii + "\t" + jj + "\t" + uuid + "\t" + permalink + "\n", Charset.defaultCharset(), true);
                                return null;
                            }
                        });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
