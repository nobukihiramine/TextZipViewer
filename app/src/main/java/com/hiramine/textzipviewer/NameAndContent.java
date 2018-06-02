package com.hiramine.textzipviewer;

public class NameAndContent
{
	// メンバー変数
	String m_strName;
	String m_strContent;

	// コンストラクタ
	public NameAndContent()
	{
		m_strName = "";
		m_strContent = "";
	}

	// コンストラクタ
	public NameAndContent( String strName, String strContent )
	{
		m_strName = strName;
		m_strContent = strContent;
	}
}
