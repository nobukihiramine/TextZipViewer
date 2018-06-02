package com.hiramine.textzipviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
	// 定数
	private static final int MENUID_FILE                              = 0;    // ファイルメニューID
	private static final int REQUEST_FILESELECT                       = 0;    // リクエストコード
	private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1; // 外部ストレージ読み込みパーミッション要求時の識別コード

	// メンバー変数
	private String       m_strInitialDir = Environment.getExternalStorageDirectory().getPath();    // 初期フォルダ
	List<NameAndContent> m_listNameAndContent;
	ListView             m_listView;
	TextView             m_textView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// アダプタの設定
		m_listView = (ListView)findViewById( R.id.listView );
		List<String> listPath     = new ArrayList<>();
		ArrayAdapter<String>  arrayAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, listPath );
		m_listView.setAdapter( arrayAdapter );
		m_listView.setOnItemClickListener( this );

		// テキストビュー
		m_textView = (TextView)findViewById( R.id.textView );
	}

	// 初回表示時、および、ポーズからの復帰時
	@Override
	protected void onResume()
	{
		super.onResume();

		// 外部ストレージ読み込みパーミッション要求
		requestReadExternalStoragePermission();
	}

	// オプションメニュー生成時
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		super.onCreateOptionsMenu( menu );
		menu.add( 0, MENUID_FILE, 0, "Select File..." );

		return true;
	}

	// オプションメニュー選択時
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case MENUID_FILE:
				// ファイル選択アクティビティ
				Intent intent = new Intent( this, FileSelectionActivity.class );
				intent.putExtra( FileSelectionActivity.EXTRA_INITIAL_DIR, m_strInitialDir );
				intent.putExtra( FileSelectionActivity.EXTRA_EXT, "" );
				startActivityForResult( intent, REQUEST_FILESELECT );
				return true;

		}
		return false;
	}

	// アクティビティ呼び出し結果の取得
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		if( REQUEST_FILESELECT == requestCode && RESULT_OK == resultCode )
		{
			Bundle extras = intent.getExtras();
			if( null != extras )
			{
				File file = (File)extras.getSerializable( FileSelectionActivity.EXTRA_FILE );
				if( null != file )
				{
					Toast.makeText( this, "File Selected : " + file.getPath(), Toast.LENGTH_SHORT ).show();
					m_strInitialDir = file.getParent();

					load( file.getPath() );
				}
			}
		}
	}

	private void load( String strPath )
	{
		// ファイルの存在チェック
		File file = new File( strPath );
		if( 0 == file.length() )
		{
			return;
		}

		// 拡張子による場合分け
		String strPath_lowercase = strPath.toLowerCase();
		if( strPath_lowercase.endsWith( ".zip" ) )
		{
			List<NameAndContent> listNameAndContent = ZipFileLoader.load( file.getPath() );
			if( null != listNameAndContent
				&& 0 != listNameAndContent.size() )
			{
				// 名前と内容のリストの更新
				m_listNameAndContent = listNameAndContent;

				// リストビューの更新
				List<String> listPath = new ArrayList<>();
				int iCount = m_listNameAndContent.size();
				for( int iIndex = 0; iIndex < iCount; iIndex++ )
				{
					listPath.add( m_listNameAndContent.get( iIndex ).m_strName );
				}
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, listPath );
				m_listView.setAdapter( arrayAdapter );

				// テキストビューの更新
				m_textView.setText( m_listNameAndContent.get( 0 ).m_strContent );
			}
		}
		else
		{
			NameAndContent nameAndContent = TextFileLoader.load( file.getPath() );
			if( null != nameAndContent )
			{
				// 名前と内容のリストの更新
				m_listNameAndContent = new ArrayList<>();
				m_listNameAndContent.add( nameAndContent );

				// リストビューの更新
				List<String> listPath = new ArrayList<>();
				listPath.add( nameAndContent.m_strName );
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, listPath );
				m_listView.setAdapter( arrayAdapter );

				// テキストビューの更新
				m_textView.setText( m_listNameAndContent.get( 0 ).m_strContent );
			}
		}
	}

	// 外部ストレージ読み込みパーミッション要求
	private void requestReadExternalStoragePermission()
	{
		if( PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) )
		{    // パーミッションは付与されている
			return;
		}
		// パーミッションは付与されていない。
		// パーミッションリクエスト
		ActivityCompat.requestPermissions( this,
										   new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
										   REQUEST_PERMISSION_READ_EXTERNAL_STORAGE );
	}

	// パーミッション要求ダイアログの操作結果
	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults )
	{
		switch( requestCode )
		{
			case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
				if( grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED )
				{
					// 許可されなかった場合
					Toast.makeText( this, "Permission denied.", Toast.LENGTH_SHORT ).show();
					finish();    // アプリ終了宣言
					return;
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id )
	{
		String strName = (String)parent.getAdapter().getItem( position );

		int iCount = m_listNameAndContent.size();
		for( int iIndex = 0; iIndex < iCount; iIndex++ )
		{
			if( strName.equals( m_listNameAndContent.get( iIndex ).m_strName ) )
			{
				// 内容テキストの更新
				m_textView.setText( m_listNameAndContent.get( iIndex ).m_strContent );
				return;
			}
		}
	}
}
