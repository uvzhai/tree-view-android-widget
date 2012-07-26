package com.netbean.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.netbean.view.TreeNodeInfo;

public class TreeParser4Json implements ITreeParser{

	@Override
	public List<TreeNodeInfo<?>> parse(InputStream is)
	{
		return parseFromJson(is);
	}
	
	/**
	 * 
	 * @param aIns
	 * @return
	 */
	private List<TreeNodeInfo<?>> parseFromJson(InputStream aIns)
	{
		List<TreeNodeInfo<?>> result = null;
		
		InputStreamReader pStreamReader;
		try
		{
			//FIXME charset
			pStreamReader = new InputStreamReader(aIns,"gbk");
			BufferedReader br = new BufferedReader(pStreamReader);
			StringBuffer strBuffer = new StringBuffer();
			String strLine = null;
			try
			{
				while((strLine = br.readLine()) != null)
				{
					strBuffer.append(strLine).append("\n");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			// close stream
			try
			{
				pStreamReader.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			try
			{
				JSONObject root = new JSONObject(strBuffer.toString());
				JSONArray tree = root.getJSONArray("tree"); 
				if(null != tree && tree.length() > 0)
				{
					result = new ArrayList<TreeNodeInfo<?>>();
					for(int idx = 0; idx < tree.length(); idx++)
					{
						JSONObject nodeObj = tree.getJSONObject(idx);
						JSONObject node = nodeObj.getJSONObject("node");
						if(null != node)
						{
							// set level = 0;
							parseNode(node,0,result);
						}
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		catch (UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param node
	 * @param level
	 * @param result_out
	 * @throws JSONException
	 */
	private void parseNode(JSONObject node,int level,final List<TreeNodeInfo<?>> result_out) throws JSONException
	{
		TreeNodeInfo<Long>  treeNode = new TreeNodeInfo<Long>(level, node.getString("title"));
		
		result_out.add(treeNode);
		
		if(node.isNull("child"))
		{
			return;
		}
		else
		{
			JSONArray child = node.getJSONArray("child");
			if(null != child && child.length() > 0)
			{
				level++;
				for(int idx = 0; idx < child.length(); idx++)
				{
					JSONObject cnodeObj = child.getJSONObject(idx);
					if(null != cnodeObj)
					{
						JSONObject cnode = cnodeObj.getJSONObject("node");
						if(null != cnode)
						{
							parseNode(cnode,level,result_out);
						}
					}
				}
			}
		}
	}
}
