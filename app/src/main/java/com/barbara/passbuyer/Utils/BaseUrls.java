package com.barbara.passbuyer.Utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by barbara on 6/23/15.
 */
public class BaseUrls {
    /**
     * Urls
     */
    public static final String IP = "192.168.1.105";
    public static final String LOGIN_BASE_URL = "http://"+IP+":8888/passDemo/Login.php";
    public static final String REGISTER_BASE_URL = "http://"+IP+":8888/passDemo/Register.php";
    public static final String SHOP_LIST_URL = "http://"+IP+":8888/passDemo/Retailer.php";
    public static final String PASS_LIST_BASE_URL = "http://"+IP+":8888/passDemo/Pass.php";

/*
    private static String getServerIPAddress() {
        String ipAddress = null;
        try {
            //创建文档对象
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dFactory.newDocumentBuilder();
            System.out.print(BaseUrls.class.getResource("/ipConfig.xml").getFile());
            Document doc = builder.parse(new File("/Users/barbara/Program/Android/PassBuyer/app/src/main/res/ipConfig.xml"));

            //解析ip地址
            NodeList nl = doc.getElementsByTagName("IP");
            Node classNode = nl.item(0).getFirstChild();
            ipAddress = classNode.getNodeValue().trim();

            System.out.print(ipAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
            return ipAddress;
    }
*/

}
