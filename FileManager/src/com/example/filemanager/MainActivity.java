package com.example.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnItemLongClickListener{
	//������Ա����
	//�����ʾ���ļ��б������
	private List<String> mFileName = null;
	//�����ʾ���ļ��б��·��
	private List<String> mFilePaths = null;
	//��ʼĿ¼ '/'
	/**
	 * �����и����� ���˰��� java.io.File.separator д���� pathSeparator
	 */
	private String mRootPath = java.io.File.separator;
	//SD�� ��Ŀ¼
	private String mSDCard = Environment.getExternalStorageDirectory().toString();
	private String mOldFilePath = "";
	private String mNewFilePath  = "";
	private String keyWords;
	//������ʾ��ǰ·��
	private TextView mPath;
	//���ڷ��ù�����
	private GridView mGridViewToolbar;
	private int[] gridview_menu_image = {R.drawable.menu_phone,R.drawable.menu_sdcard
			,R.drawable.menu_search,R.drawable.menu_create,R.drawable.menu_create,
			R.drawable.menu_palse,R.drawable.menu_exit};
	private String[] gridview_menu_title = {"�ֻ�","SD��","����","����","ճ��","�˳�"};
	//�����ֻ���SD�� 1 �����ֻ� 2 ����SD��
	private static int menuPosition = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//��ʼ���˵���ͼ
		initGridViewMenu();
		 //��ʼ���˵�������
        initMenuListener();
        //Ϊ�б���󶨳���������
        getListView().setOnItemLongClickListener(this);
        mPath = (TextView)findViewById(R.id.mPath);   
      //һ��ʼ�����ʱ������ֻ�Ŀ¼�µ��ļ��б�
		 initFileListInfo(mRootPath);
	}
	//ΪGirdView ���ò˵���Դ
	private void initGridViewMenu() {
		mGridViewToolbar = (GridView)findViewById(R.id.file_gridview_toolbar);
		//����ѡ��ʱ�ı���ͼƬ
		mGridViewToolbar.setSelector(R.drawable.menu_item_selected);
		//���ñ���ͼƬ
		mGridViewToolbar.setBackgroundResource(R.drawable.menu_background);
		//��������
		mGridViewToolbar.setNumColumns(6);
		//���þ��ж���
		mGridViewToolbar.setGravity(Gravity.CENTER);
		//����ˮƽ ��ֱ�����Ϊ10
		mGridViewToolbar.setVerticalSpacing(10);
		mGridViewToolbar.setHorizontalSpacing(10);
		//����������
		mGridViewToolbar.setAdapter(getMenuAdapter(gridview_menu_title, gridview_menu_image));
	}
	//�˵�������
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		//�����б����ڴ��ӳ���
		ArrayList<HashMap<String,Object>> mData = new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<menuNameArray.length;i++) {
			HashMap<String,Object> mMap = new HashMap<String,Object>();
			//��Imageӳ���ͼƬ��Դ
			mMap.put("image", imageResourceArray[i]);
			mMap.put("title", menuNameArray[i]);
			mData.add(mMap);
		}
		SimpleAdapter mAdapter = new SimpleAdapter(this,mData,R.layout.item_menu,new String[]{"image","title"},
				new int[]{R.id.item_image,R.id.item_text});
		return mAdapter;
	}
	//�˵���ļ���
	protected void initMenuListener() {
		mGridViewToolbar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				switch (position) {
				case 0:
					//�ص���Ŀ¼
					menuPosition = 1;
					initFileListInfo(mRootPath);
					break;
				case 1:
					//�ص�SD��
					menuPosition = 2;
					initFileListInfo(mSDCard);
					break;
				case 2:
					//��ʾ�����Ի���
					searchDialog();
					break;
				case 3:
					//�����ļ�
					createFolder();
					break;
				case 4:
					//ճ���ļ�
					break;
				case 5:
					//�˳�
					MainActivity.this.finish();
					break;
				default:
					break;
				}
			}
			
		});
	}
	//�þ�̬�����洢 ��ǰĿ¼·����Ϣ
	public static String mCurrentFilePath = "";
	
	 /**���ݸ�����һ���ļ���·���ַ��������������
     * �����а������ļ����Ʋ����õ�ListView�б���*/
	private void initFileListInfo(String filePath) {
		isAddBackUp = false;
		mCurrentFilePath = filePath;
		//��ʾ��ǰ��·��
		mPath.setText(filePath);
		mFilePaths = new ArrayList<String>();
		mFileName = new ArrayList<String>();
		File mFile = new File(filePath);
		//���������ļ���·���µ������ļ�/�ļ���
		File[] mFiles = mFile.listFiles();
		//ֻҪ��ǰ·���²����ֻ�Ŀ¼����sd��Ŀ¼�򷵻� ��һ���ͷ��ظ�Ŀ¼
		if(menuPosition==1&&!mCurrentFilePath.equals(mRootPath)) {
			initAddBackUp(filePath,mRootPath);
		}else if(menuPosition==2&&!mCurrentFilePath.equals(mSDCard)) {
			initAddBackUp(filePath,mSDCard);
		}
//		for(int i=0;i<mFiles.length;i++) {
//			mFileName.add(mFiles[i].getName());
//			mFilePaths.add(mFiles[i].getPath());
//		}
		//�������ļ���ӵ�������
		for(File mCurrentFile:mFiles) {
			mFileName.add(mCurrentFile.getName());
			mFilePaths.add(mCurrentFile.getPath());
		}
		//����������
		setListAdapter(new FileAdapter(MainActivity.this,mFileName,mFilePaths));
	}
	private boolean isAddBackUp = false;
	private void initAddBackUp(String filePath,String phone_sdcard) {
		if(!filePath.equals(phone_sdcard)) {
			//�б���ĵ�һ������Ϊ���ظ�Ŀ¼
			mFileName.add("BacktoRoot");
			mFilePaths.add(phone_sdcard);
			mFileName.add("BacktoRoot");
			//��ǰ·���ĸ��� ���Ƿ�����һ��
			mFilePaths.add(new File(filePath).getParent());
			//����Ӱ����ؼ� ��ʾ����ΪTrue
			isAddBackUp = true;
		}
	}
	
	
	/**
	 * �����ļ�
	 */
	private int i;
	FileInputStream fis;
	FileOutputStream fos;
	private void copyFile(String oldFile, String newFile) {
		try {
			fis = new FileInputStream(oldFile);
			fos = new FileOutputStream(newFile);
			do{
					//����ֽڵĶ�ȡ
				if((i=fis.read())!=-1) {
					fos.write(i);
				}
			}while(i!=-1);
			if(fis!=null) {
				fis.close();
			}
			if(fos!=null) {
				fos.close();
			}

		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	private String mNewFolderName = "";
	private File mCreateFile;
	private RadioGroup mCreateRadioGroup;
	private static int mChecked;
	/**
	 * �����ļ��еķ���:���û�����������Ĵ����˵���ʱ�����ڵ�ǰĿ¼�´�����һ���ļ���
     * ��̬����mCurrentFilePath�洢�ľ��ǵ�ǰ·��
     * java.io.File.separator��JAVA�������ṩ��һ��File���еľ�̬��Ա���������ϵͳ�Ĳ�ͬ�������ָ���
     * mNewFolderName��������Ҫ���������ļ������ƣ���EditText����ϵõ���
	 */
	private void createFolder() {
		//���ڱ�ʾ��ǰѡ�е����ļ������ļ���
		mChecked = 2;
		LayoutInflater mLI = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//��ʼ���Ի��򲼾�
		final LinearLayout mLL = (LinearLayout)mLI.inflate(R.layout.create_dialog, null);
		mCreateRadioGroup  = (RadioGroup)mLL.findViewById(R.id.radiogroup_create);
		final RadioButton mCreateFileButton = (RadioButton)mLL.findViewById(R.id.create_file);
		final RadioButton mCreateFolderButton = (RadioButton)mLL.findViewById(R.id.create_folder);
		//����Ĭ��Ϊ�����ļ���
		mCreateFolderButton.setChecked(true);
		//Ϊ��ť���ü�����
		mCreateRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//��ѡ��ı�ʱ ����
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==mCreateFileButton.getId()) {
					mChecked = 1;
				}else if(checkedId==mCreateFolderButton.getId()) {
					mChecked = 2;
				}
			}
			
		});
		
		//��ʾ�Ի���
		Builder mBuilder = new AlertDialog.Builder(MainActivity.this)
		.setTitle("�½�")
		.setView(mLL)
		.setPositiveButton("����", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//�����û����������
				mNewFolderName = ((EditText)mLL.findViewById(R.id.new_filename)).getText().toString();
				if(mChecked==1) {
					try {
						mCreateFile = new File(mCurrentFilePath+java.io.File.separator+mNewFolderName+".txt");
						mCreateFile.createNewFile();
						//ˢ�µ�ǰĿ¼�ļ��б�
						initFileListInfo(mCurrentFilePath);
					}catch(IOException e) {
						Toast.makeText(MainActivity.this, "�ļ���ƴ�ӳ���", Toast.LENGTH_LONG).show();
					}
					
				}else if(mChecked==2) {
					mCreateFile =  new File(mCurrentFilePath+java.io.File.separator+mNewFolderName);
					if(!mCreateFile.exists()&& !mCreateFile.isDirectory()&& mNewFolderName.length()!=0) {
						if(mCreateFile.mkdirs()) {
							//ˢ���ļ��б�
							initFileListInfo(mCurrentFilePath);
						}
						else {
							Toast.makeText(MainActivity.this, "����ʧ��,������ϵͳȨ�޲���", Toast.LENGTH_LONG).show();
						}
					}else {
						Toast.makeText(MainActivity.this, "�ļ���Ϊ�գ�������������", Toast.LENGTH_SHORT).show();
						
					}
				}
			}
		}).setNeutralButton("ȡ��", null);
		mBuilder.show();
	}
	/**
	 * �������Ի���
	 */
	EditText mET;
	//@SuppressWarnings("deprecation")
	private void initRenameDialog(final File file) {
		LayoutInflater mLI = LayoutInflater.from(MainActivity.this);
		//��ʼ�� �������Ի���
		LinearLayout mLL = (LinearLayout)mLI.inflate(R.layout.rename_dialog, null);
		mET = (EditText)mLL.findViewById(R.id.new_filename);
		//��ʾ��ǰ���ļ���\
		mET.setText(file.getName());
		//���ü�����
		OnClickListener listener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String modifyName = mET.getText().toString();
				final String modifyFilePath = file.getParentFile().getPath()+java.io.File.separator;
				final String newFilePath = modifyFilePath+modifyName;
				//�жϸ��µĵ��ļ��� �ڵ�ǰĿ¼���Ƿ��Ѿ�����
				if(new File(newFilePath).exists()) {
					if(modifyName.equals(file.getName())) {
						new AlertDialog.Builder(MainActivity.this)
						.setTitle("��ʾ!")
						.setMessage("���ļ��Ѵ��ڣ��Ƿ�Ҫ����")
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								file.renameTo(new File(newFilePath));
								Toast.makeText(MainActivity.this, "the file path is"+new File(newFilePath), Toast.LENGTH_LONG).show();
								initFileListInfo(file.getParentFile().getPath());
								
							}
						}).setNegativeButton("ȡ��", null).show();
					}
				}else {
					file.renameTo(new File(newFilePath));
					initFileListInfo(file.getParentFile().getPath());
				}
				
			}
		};
		//��ʾ�Ի���
		AlertDialog.Builder renameDialog = new AlertDialog.Builder(MainActivity.this);
		renameDialog.setView(mLL);
		renameDialog.setPositiveButton("ȷ��", listener);
		renameDialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog rename = renameDialog.create();
		rename.show();
	}
	
	/**
	 * ɾ���Ի���
	 */
	private void initDeleteDialog(final File file) {
		new AlertDialog.Builder(MainActivity.this)
		.setTitle("��ʾ")
		.setMessage("��ȷ��Ҫɾ��"+(file.isDirectory()?"�ļ���":"�ļ�")+"��?")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(file.isFile()) {
					file.delete();
					initFileListInfo(file.getParentFile().getPath());
				}else {
					//�ļ��еĻ� ����deleteFolderɾ��
					deleteFolder(file);
					initFileListInfo(file.getParentFile().getPath());
				}
				
			}
		}).setNegativeButton("ȡ��", null).show();
	}
	//�ݹ�ɾ���ļ����µ������ļ�
	private void deleteFolder(File folder) {
		File[] fileArray = folder.listFiles();
		if(fileArray.length==0) {
			//���ļ� ��ֱ��ɾ��
			folder.delete();
		}else {
			//������Ŀ¼
			for(File currentFile:fileArray) {
				if(currentFile.exists() && currentFile.isFile()) {
					currentFile.delete();
				}else {
					//�ݹ�ɾ��
					deleteFolder(currentFile);
				}
			}
		}
	}
	 //�����б�����¼�����:�Գ�����Ҫ����һ�����ƣ����б��а��������ظ�Ŀ¼���͡�������һ����ʱ����Ҫ�������н�������
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
			if(isAddBackUp == true){//˵�����ڷ��ظ�Ŀ¼�ͷ�����һ�����У�������Ҫ�������н�������
				if(position != 0 && position != 1){
					initItemLongClickListener(new File(mFilePaths.get(position)));
				}
			}
			if(mCurrentFilePath.equals(mRootPath)||mCurrentFilePath.equals(mSDCard)){
				initItemLongClickListener(new File(mFilePaths.get(position)));
			}
			return false;
		}
	/**
	 * �����ļ��� ���ļ�ʱ���¼�����
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final File mFile = new File(mFilePaths.get(position));
		Toast.makeText(MainActivity.this, mFilePaths.get(position), Toast.LENGTH_SHORT).show();
		//����ļ��ǿɶ��� ���ǽ�ȥ�鿴�ļ�
		if(mFile.canRead()) {
			if(mFile.isDirectory()) {
				//������ļ��� ��ֱ�ӽ����ļ��� �鿴�ļ�
				initFileListInfo(mFilePaths.get(position));
			}else {
				//������ļ� ����Ӧ�ķ�ʽ��
				String fileName = mFile.getName();
				String fileEnds = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();
				if(fileEnds.equals("txt")) {
					//��ʾ������ ��ʾ���ڶ�ȡ
					initProgressDialog(ProgressDialog.STYLE_HORIZONTAL);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							openTxtFile(mFile.getPath());
						}
					}).start();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							while(true) {
								if(isTxtDataOk==true) {
									//�رս�����
									mProgressDialog.dismiss();
									//ִ�д��ļ�����
									executeIntent(txtData.toString(),mFile.getPath());
									break;
								}
								if(isCancleProgressDialog==true) {
									mProgressDialog.dismiss();
									break;
								}
								
								
							}
						}
					}).start();
					
				}else if(fileEnds.equals("html")||fileEnds.equals("mht")||fileEnds.equals("htm")){
					
					
				}
			}
		}
		super.onListItemClick(l, v, position, id);
	}
	//ִ��intent��ת����
	
	private void executeIntent(String data,String file) {
		Intent intent = new Intent(MainActivity.this,EditTxtActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//�����ļ���·�� ���� ������
		intent.putExtra("path", file);
		intent.putExtra("title", new File(file).getName());
		intent.putExtra("data", data.toString());
		startActivity(intent);
	}
	String txtData="";
	boolean isTxtDataOk = false;
	private void openTxtFile(String file) {
		isTxtDataOk = false;
		try {
			FileReader fis = new FileReader(new File(file));
			StringBuilder mSb = new StringBuilder();
			int m;
			//��ȡ�ı��ļ�����
			while((m=fis.read())!=-1) {
				mSb.append(m);
			}
			fis.close();
			//�����ȡ��������
			txtData = mSb.toString();
			//��ȡ���
			isTxtDataOk = true;
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * ����ϵͳ�ķ��� �����ı��ļ�
	 */
	private void openFile(File file) {
		if(file.isDirectory()) {
			initFileListInfo(file.getPath());
		}else {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			//���õ�ǰ�ļ�����
			intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
			startActivity(intent);
		}
	}
	//���MIME���͵ķ���
	private String getMIMEType(File file) {
		String type="";
		String fileName = file.getName();
		//ȥ���ļ���׺����ת��Сд
		String fileEnds = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();
		if(fileEnds.equals("m4a")||fileEnds.equals("mp3")||fileEnds.equals("mid")||fileEnds.equals("xmff")||
				fileEnds.equals("ogg")||fileEnds.equals("wav")) {
			type = "audio/*";// ϵͳ���г����п��ܴ���Ƶ�ļ��ĳ���ѡ����
		}else if(fileEnds.equals("3gp")||fileEnds.equals("mp4")){
    		type = "video/*";// ϵͳ���г����п��ܴ���Ƶ�ļ��ĳ���ѡ����
    	}else if(fileEnds.equals("jpg")||fileEnds.equals("gif")||fileEnds.equals("png")||fileEnds.equals("jpeg")||fileEnds.equals("bmp")){
    		type = "image/*";// ϵͳ���г����п��ܴ�ͼƬ�ļ��ĳ���ѡ����
    	}else{
    		type = "*/*"; // ϵͳ���г����п��ܴ򿪸��ļ��ĳ���ѡ����
    	}
    	return type;
	}
	//������
	ProgressDialog mProgressDialog;
	boolean isCancleProgressDialog = false;
	private void initProgressDialog(int style) {
		isCancleProgressDialog = false;
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("��ʾ");
		mProgressDialog.setMessage("����Ϊ������ı����ݣ����Ժ�");
		mProgressDialog.setCancelable(true);
		mProgressDialog.setButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isCancleProgressDialog = true;
				mProgressDialog.dismiss();
			}
		});
		mProgressDialog.show();
	}
	private String mCopyFileName;
    private boolean isCopy = false;
	/**�����ļ����ļ���ʱ�����Ĵ�ListViewЧ���Ĺ��ܲ˵�*/
	private void initItemLongClickListener(final File file){
		OnClickListener listener = new DialogInterface.OnClickListener(){
			//item��ֵ���Ǵ�0��ʼ������ֵ(���б�ĵ�һ�ʼ)
			public void onClick(DialogInterface dialog, int item) {
				if(file.canRead()){//ע�⣬���ж��ļ��Ĳ����������ڸ��ļ��ɶ�������²ſ��ԣ����򱨴�
					if(item == 0){//����
						if(file.isFile()&&"txt".equals((file.getName().substring(file.getName().lastIndexOf(".")+1, file.getName().length())).toLowerCase())){
							Toast.makeText(MainActivity.this, "�Ѹ���!", Toast.LENGTH_SHORT).show();
							//���Ʊ�־λ�������Ѹ����ļ�
							isCopy = true;
							//ȡ�ø����ļ�������
							mCopyFileName = file.getName();
							//��¼�����ļ���·��
							mOldFilePath = mCurrentFilePath+java.io.File.separator+mCopyFileName;
						}else{
							Toast.makeText(MainActivity.this, "�Բ���,Ŀǰֻ֧�ָ����ı��ļ�!", Toast.LENGTH_SHORT).show();
						}
					}else if(item == 1){//������
						initRenameDialog(file);
					}else if(item == 2){//ɾ��
						initDeleteDialog(file);
					}
				}else{
					Toast.makeText(MainActivity.this, "�Բ������ķ���Ȩ�޲���!", Toast.LENGTH_SHORT).show();
				}
			}	
    	};
    	//�б�������
    	String[] mMenu = {"����","������","ɾ��"};
    	//��ʾ����ѡ��Ի���
    	new AlertDialog.Builder(MainActivity.this)
    								.setTitle("��ѡ�����!")
    								.setItems(mMenu, listener)
    								.setPositiveButton("ȡ��",null).show();
	}

	
	
	//�䶨��Adapter �ڲ���
	 class FileAdapter extends BaseAdapter {
		 //���ؼ� ���ָ�ʽ���ļ���ͼ��
		 private Bitmap mBackRoot;
		 private Bitmap mBackUp;
		 private Bitmap mImage;
		 private Bitmap mAudio;
		 private Bitmap mRar;
		 private Bitmap mVideo;
		 private Bitmap mFolder;
		 private Bitmap mApk;
		 private Bitmap mOthers;
		 private Bitmap mTxt;
		 private Bitmap mWeb;
		 
		 private Context mContext;
		 //�ļ����б�
		 private List<String> mFileNameList;
		 //�ļ���Ӧ·���б�
		 private List<String> mFilePathList;
		 public FileAdapter(Context context,List<String> fileName,List<String> filePath) {
			 mContext = context;
			 mFileNameList = fileName;
			 mFilePathList = filePath;
			 //��ʼ��ͼƬ��Դ
			 mBackRoot = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back_to_root);
			 mBackUp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back_to_up);
			 mImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image);
			 mAudio = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.audio);
			 mVideo = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.video);
			 mApk = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.apk);
			 mTxt = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.txt);
			 mOthers = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.others);
			 mFolder = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.folder);
			 mRar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.zip_icon);
			 mWeb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.web_browser);
		 }
		 //����ļ�����
		@Override
		public int getCount() {
			
			return mFilePathList.size();
		}
		//��ȡ��ǰλ�ö�Ӧ���ļ���
		@Override
		public Object getItem(int position) {
			
			return mFileNameList.get(position);
		}
		//��ȡ��ǰλ��
		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView==null) {
				viewHolder = new ViewHolder();
				LayoutInflater mLI = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				//��ʼ���б�Ԫ�ؽ���
				convertView = mLI.inflate(R.layout.list_child,null);
				//��ȡ�б��ֽ���Ԫ��
				viewHolder.mIV = (ImageView)convertView.findViewById(R.id.image_list_childs);
				viewHolder.mTV = (TextView)convertView.findViewById(R.id.text_list_childs);
				//��ÿһ�е�Ԫ������Ϊ��ǩ
				convertView.setTag(viewHolder);
			}else {
				//��ȡ��ͼ��ǩ
				viewHolder = (ViewHolder)convertView.getTag();
			}
			File mFile = new File(mFilePathList.get(position).toString());
			if(mFilePathList.get(position).toString().equals("BacktoRoot")) {
				viewHolder.mIV.setImageBitmap(mBackRoot);
				viewHolder.mTV.setText("���ظ�Ŀ¼");
				
			}else if(mFilePathList.get(position).toString().equals("BacktoUp")) {
				viewHolder.mIV.setImageBitmap(mBackUp);
				viewHolder.mTV.setText("������һ��");
			}else if(mFileNameList.get(position).toString().equals("BacktoSearchBefore")) {
				viewHolder.mIV.setImageBitmap(mBackRoot);
				viewHolder.mTV.setText("��������֮ǰĿ¼");
			}else {
				String fileName = mFile.getName();
				viewHolder.mTV.setText(fileName);
				if(mFile.isDirectory()) {
					viewHolder.mIV.setImageBitmap(mFolder);
				}else {
					String fileEnds = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();
					if(fileEnds.equals("m4a")||fileEnds.equals("mp3")||fileEnds.equals("mid")||fileEnds.equals("xmf")||fileEnds.equals("ogg")||fileEnds.equals("wav")){
			    		viewHolder.mIV.setImageBitmap(mVideo);
			    	}else if(fileEnds.equals("3gp")||fileEnds.equals("mp4")){
			    		viewHolder.mIV.setImageBitmap(mAudio);
			    	}else if(fileEnds.equals("jpg")||fileEnds.equals("gif")||fileEnds.equals("png")||fileEnds.equals("jpeg")||fileEnds.equals("bmp")){
			    		viewHolder.mIV.setImageBitmap(mImage);
			    	}else if(fileEnds.equals("apk")){
			    		viewHolder.mIV.setImageBitmap(mApk);
			    	}else if(fileEnds.equals("txt")){
			    		viewHolder.mIV.setImageBitmap(mTxt);
			    	}else if(fileEnds.equals("zip")||fileEnds.equals("rar")){
			    		viewHolder.mIV.setImageBitmap(mRar);
			    	}else if(fileEnds.equals("html")||fileEnds.equals("htm")||fileEnds.equals("mht")){
			    		viewHolder.mIV.setImageBitmap(mWeb);
			    	}else {
			    		viewHolder.mIV.setImageBitmap(mOthers);
			    	}
				}
			}
			return convertView;
		}
		 class ViewHolder {
			 ImageView mIV;
			 TextView mTV;
		 }
	 }
	 
	 
	 Intent serviceIntent;
	 ServiceConnection mSC;
	 RadioGroup mRadioGroup;
	 static int mRadioChecked;
	 public static final String KEYWORD_BROADCAST = "com.supermario.file.KEYWORD_BROADCAST";
	 /**
	  * ��ʾ�����Ի���
	  * @author heshaokang
	  * 
	  */
	 private void searchDialog() {
		 //����ȷ�����ڵ�ǰĿ¼��������������Ŀ¼�����ı�־
		 mRadioChecked = 1;
		 LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
		 final View mLL = (View)inflate.inflate(R.layout.search_dialog, null);
		 mRadioGroup = (RadioGroup)mLL.findViewById(R.id.radiogroup_search);
		 final RadioButton mCurrentPathButton = (RadioButton)mLL.findViewById(R.id.radio_currentpath);
		 final RadioButton mWholePathButton = (RadioButton)mLL.findViewById(R.id.radio_wholepath);
		 //����Ĭ���ڵ�ǰ·����������
		 mCurrentPathButton.setChecked(true);
		 mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==mCurrentPathButton.getId()) {
					mRadioChecked = 1;
				}else if(checkedId==mWholePathButton.getId()){
					mRadioChecked = 2;
				}
				
			}
		});
		Builder mBuilder = new AlertDialog.Builder(MainActivity.this)
		 .setTitle("��ʾ")
		 .setView(mLL)
		 .setPositiveButton("ȷ��", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				keyWords = ((EditText)mLL.findViewById(R.id.edit_search)).getText().toString();
				if(keyWords.length()==0) {
					Toast.makeText(MainActivity.this, "�ؼ��ֲ���Ϊ��", Toast.LENGTH_SHORT).show();
					searchDialog();
				}else {
					if(menuPosition==1) {
						mPath.setText(mRootPath);
					}else {
						mPath.setText(mSDCard);
					}
					//��ȡ�û�����Ĺؼ��� �����͹㲥 ---��ʼ
					Intent keywordIntent = new Intent();
					keywordIntent.setAction(KEYWORD_BROADCAST);
					//���������ķ�Χ���� 1��ǰ·�������� 2 SD��������
					if(mRadioChecked==1) {
						keywordIntent.putExtra("searchpath", mCurrentFilePath);
					}else {
						keywordIntent.putExtra("searchpath", mSDCard);
					}
					//���ݹؼ���
					keywordIntent.putExtra("keyword", keyWords);
					//������Ϊֹ��Я���ؼ�����Ϣ�������˹㲥 ����Service�����н��ոù㲥����ȡ�ؼ��ֽ�������
					getApplicationContext().sendBroadcast(keywordIntent);
					serviceIntent = new Intent("com.android.service.FILE_SEARCH_START");
					MainActivity.this.startActivity(serviceIntent); //�������� ��������
					isComeBackFromNotification = false;
				}
			}
		}).setNegativeButton("ȡ��", null);
		 mBuilder.create().show();
	 }
	 /**
	  * ע��㲥
	  */
	 private IntentFilter mFilter;
	 private FileBroadcast mFileBroadcast;
	 private IntentFilter mIntentFilter;
	 private SearchBroadCast mServiceBroadcast;
	 
	 @Override
	protected void onStart() {
		
		super.onStart();
		mFilter = new IntentFilter();
		mFilter.addAction(FileService.FILE_SEARCH_COMPLETED);
		mFilter.addAction(FileService.FILE_NOTIFICATION);
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(KEYWORD_BROADCAST);
		if(mFileBroadcast==null) {
			mFileBroadcast = new FileBroadcast();
		}
		if(mServiceBroadcast==null) {
			mServiceBroadcast = new SearchBroadCast();
		}
		this.registerReceiver(mFileBroadcast, mFilter);
		this.registerReceiver(mServiceBroadcast, mIntentFilter);
	}
	 /**
	  * ע�����
	  */
	 @Override
	protected void onDestroy() {
		
		super.onDestroy();
		mFileName.clear();
		mFilePaths.clear();
		this.unregisterReceiver(mFileBroadcast);
		this.unregisterReceiver(mServiceBroadcast);
	}
	 
	 /**
	  * �ڲ��㲥��
	  */
	 private String mAction;
	 public static boolean isComeBackFromNotification= false;
	 class FileBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mAction = intent.getAction();
			//������ϵĹ㲥
			if(FileService.FILE_SEARCH_COMPLETED.equals(mAction)) {
				mFileName = intent.getStringArrayListExtra("mFileNameList");
				mFilePaths = intent.getStringArrayListExtra("mFilePathsList");
				Toast.makeText(MainActivity.this, "�������", Toast.LENGTH_SHORT).show();
				searchCompletedDialog("������ϣ��Ƿ�������ʾ�����");
				getApplicationContext().stopService(serviceIntent);
				
				//���֪ͨ����ת�����Ĺ㲥
			}else if(FileService.FILE_NOTIFICATION.equals(mAction)) {
				String notification = intent.getStringExtra("notification");
				Toast.makeText(MainActivity.this, notification, Toast.LENGTH_SHORT).show();
				searchCompletedDialog("��ȷ��Ҫȡ��������");
			}
		}
		 
	 }
	 //������Ϻ͵��֪ͨ�����ĶԻ���
	 private void searchCompletedDialog(String message) {
		 Builder searchBuilder = new AlertDialog.Builder(MainActivity.this)
		 .setTitle("��ʾ")
		 .setMessage(message)
		 .setPositiveButton("ȷ��", new OnClickListener() {
			//��������ʱ ��Ҫ�԰�ť��һ���ж� ��Ϊ��������� 1 ������� 2 ȡ������
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(FileService.FILE_SEARCH_COMPLETED.equals(mAction)) {
					if(mFileName.size()==0) {
						Toast.makeText(MainActivity.this, "������ļ�|�ļ���", Toast.LENGTH_SHORT).show();
						//����б�
						setListAdapter(new FileAdapter(MainActivity.this, mFileName, mFilePaths));
						
					}else {
						//��ʾ�ļ��б�
						setListAdapter(new FileAdapter(MainActivity.this, mFileName, mFilePaths));
					}
				}else {
					//����������־Ϊtrue
					isComeBackFromNotification = true;
					//�رշ��� ȡ������
					getApplicationContext().stopService(serviceIntent);
				}
			}
		}).setNegativeButton("ȡ��", null);
		 searchBuilder.create();
		 searchBuilder.show();
	 }
}
