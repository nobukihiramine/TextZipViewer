package com.hiramine.textzipviewer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TextFileLoader
{
	public static NameAndContent load( String strPath )
	{
		// ファイルの存在チェック
		File file = new File( strPath );
		if( 0 == file.length() )
		{
			return null;
		}

		try
		{
			BufferedReader bufferedReader;
			boolean        bResult;

			// ファーストパース（行数カウント）
			int[] aiCountLine = new int[]{ 0 };
			bufferedReader = new BufferedReader( new FileReader( strPath ) );    // ファイルを開く
			bResult = parse_first( bufferedReader, aiCountLine );
			bufferedReader.close();    // ファイルを閉じる
			if( !bResult )
			{
				return null;
			}

			// セカンドパース（内容読み込み）
			String[] astrContent = new String[]{ "" };
			bufferedReader = new BufferedReader( new FileReader( strPath ) );    // ファイルを開く
			bResult = parse_second( bufferedReader, astrContent );
			bufferedReader.close();    // ファイルを閉じる
			if( !bResult )
			{
				return null;
			}

			return new NameAndContent( strPath, astrContent[0] );
		}
		catch( Exception e )
		{
			Log.e( "TextFileLoader", "load : " + e );
			return null;
		}
	}

	public static boolean parse_first( BufferedReader bufferedReader, int[] aiCountLine )
	{
		// インプットのチェック
		if( null == bufferedReader
			|| null == aiCountLine
			|| 1 != aiCountLine.length )
		{
			return false;
		}

		// アウトプットの初期化
		aiCountLine[0] = 0;

		int iCountLine = 0;
		try
		{
			while( true )
			{
				String strReadString = bufferedReader.readLine();
				if( null == strReadString )
				{
					break;
				}
				iCountLine++;
			}
		}
		catch( Exception e )
		{
			Log.e( "TextFileLoader", "parse_first : " + e );
			return false;
		}

		// アウトプットへの代入
		aiCountLine[0] = iCountLine;

		return true;
	}

	public static boolean parse_second( BufferedReader bufferedReader, String[] astrContent )
	{
		// インプットのチェック
		if( null == bufferedReader
			|| null == astrContent
			|| 1 != astrContent.length )
		{
			return false;
		}

		// アウトプットの初期化
		astrContent[0] = "";

		StringBuilder stringBuilder = new StringBuilder();
		try
		{
			while( true )
			{
				String strReadString = bufferedReader.readLine();
				if( null == strReadString )
				{
					break;
				}
				stringBuilder.append( strReadString );
				stringBuilder.append( "\n" );
			}
		}
		catch( Exception e )
		{
			Log.e( "TextFileLoader", "parse_second : " + e );
			return false;
		}

		// アウトプットへの代入
		astrContent[0] = stringBuilder.toString();

		return true;
	}
}
