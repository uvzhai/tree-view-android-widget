package com.netbean.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

import com.netbean.view.TreeNodeInfo;

public class TreeParser4Xml implements ITreeParser {

	public static final String TAG = TreeParser4Xml.class.getSimpleName();
	@Override
	public List<TreeNodeInfo<?>> parse(InputStream is)
	{
		List<TreeNodeInfo<?>> result = null;
		try
		{
			String content = stream2String(is,"utf-8");
			if(null != is)
			{
				Document document = null;
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource insource = new InputSource(new StringReader(content));
				document = documentBuilder.parse(insource);
				//document = documentBuilder.parse(is);
				NodeList nodes = document.getElementsByTagName("node");
				if(null != nodes && nodes.getLength() > 0)
				{
					result = new ArrayList<TreeNodeInfo<?>>();
					Log.d(TAG, "nodes lenght: " + nodes.getLength());
				}
				for(int idx=0; idx<nodes.getLength(); idx++)
				{
					Node aNode = nodes.item(idx);
					if(Node.ELEMENT_NODE == aNode.getNodeType())
					{
						String title = aNode.getAttributes().getNamedItem("title").getNodeValue();
						String level = aNode.getAttributes().getNamedItem("level").getNodeValue();
						Log.d(TAG, "title: " + title + "level: " + level);
						TreeNodeInfo<Long> treeNode = new TreeNodeInfo<Long>(Integer.parseInt(level), title);
						result.add(treeNode);
					}
				}
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return result;
	}
	
	public static String stream2String(InputStream aInputStream,String encode) throws UnsupportedEncodingException 
	{
		if ( null == aInputStream )
			return "";
		
		// Create a new instance of reader.
		InputStreamReader pStreamReader = new InputStreamReader(aInputStream,encode);
		
		// Create the instance for buffer reader.
		final int nSize = 4 * 1024;
		BufferedReader pReader = new BufferedReader(pStreamReader, nSize);
		
		// Create the buffer builder.
        StringBuilder pBuilder = new StringBuilder();
 
        String strLine = null;
        try 
        {
            while ((strLine = pReader.readLine()) != null) 
            {
            	pBuilder.append(strLine + "\n");
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        
        // Get the string.
        String strResult = pBuilder.toString();
        
        // Clean up.
        strLine = null;
        pBuilder = null;
        pReader = null;
        pStreamReader = null;
        
        return strResult;
	}

}
