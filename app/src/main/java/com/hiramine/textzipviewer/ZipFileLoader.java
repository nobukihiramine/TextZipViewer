package com.hiramine.textzipviewer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileLoader
{
	public static List<NameAndContent> load( String strPath )
	{
		// ファイルの存在チェック
		File file = new File( strPath );
		if( 0 == file.length() )
		{
			return null;
		}

		List<NameAndContent> listNameAndContent = new ArrayList<>();

		try
		{
			ZipFile                         zipFile = new ZipFile( strPath );
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while( entries.hasMoreElements() )
			{
				// エントリ取り出し
				ZipEntry zipEntry = entries.nextElement();
				if( zipEntry.isDirectory() )
				{    // 基本ファイルのみエントリされるが、空ディレクトリは、ディレクトリがエントリされる。
					continue;
				}
				// ファイルパスの取得（セカンドパースで使用する）
				String strEntryPath = zipEntry.getName();

				BufferedReader bufferedReader;
				boolean        bResult;

				// ファーストパース（行数カウント）
				int[] aiCountLine = new int[]{ 0 };
				bufferedReader = new BufferedReader( new InputStreamReader( zipFile.getInputStream( zipEntry), "UTF-8" ) );
				bResult = TextFileLoader.parse_first( bufferedReader, aiCountLine );
				if( !bResult )
				{
					continue;
				}


				// セカンドパース（内容読み込み）
				String[] astrContent = new String[]{ "" };
				ZipEntry zipEntry2 = zipFile.getEntry( strEntryPath );
				bufferedReader = new BufferedReader( new InputStreamReader( zipFile.getInputStream( zipEntry2 ), "UTF-8" ) );
				bResult = TextFileLoader.parse_second( bufferedReader, astrContent );
				if( !bResult )
				{
					return null;
				}

				// 読み込んだ内容とファイルパスをリストに追加
				listNameAndContent.add( new NameAndContent( strEntryPath, astrContent[0] ) );
			}
			zipFile.close();
		}
		catch( Exception e )
		{
			Log.e( "ZipFileLoader", "load : " + e );
			return null;
		}

		return listNameAndContent;
	}
}
