package com.netbean.datasource;

import java.io.InputStream;
import java.util.List;

import com.netbean.view.TreeNodeInfo;

public interface ITreeParser {

	/**
	 * 
	 * @param is
	 * @return
	 */
	List<TreeNodeInfo<?>> parse(InputStream is);
	
}
