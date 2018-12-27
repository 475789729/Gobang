package com.liuyao.gobang.ui;

import java.io.IOException;

import com.liuyao.gobang.widget.Chessboard;

import net.liuyao.core.AI;
import net.liuyao.core.GameStatus;
import net.liuyao.core.Gobang;
import net.liuyao.core.Gobang.AIWinCallback;
import net.liuyao.core.Gobang.IPlayerWinCallback;
import net.liuyao.core.Gobang.PlayMP3;
import net.liuyao.core.VCTHelper;
import net.liuyao.core.WeighValue;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//程序入口Activity
public class MainActivity extends Activity {
	private Chessboard gameView;
	private Gobang gobang;
	private MediaPlayer mp;
	private Button btn_new, btn_back, btn_xuhao;
	private TextView text_new, text_back, text_xuhao;
	private AlertDialog alert;
	private AlertDialog alertExit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.five_game_view);
        initView();
        initMp();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				showSetDialog();
			}
		}, 500);
             	     
    } 
    private void initMp(){
    	mp = MediaPlayer.create(this, R.raw.click);
    	mp.setAudioStreamType(AudioManager.STREAM_ALARM);
    }
    private void playSound(int res){
    	if(mp != null){				
			try {
				mp.reset();
				AssetFileDescriptor fileDesc = getResources()
					      .openRawResourceFd(res);
				mp.setDataSource(fileDesc.getFileDescriptor(), fileDesc.getStartOffset(),fileDesc.getLength());
				fileDesc.close();
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			
		}
    }
    private void initView(){
    	int width = getWindowManager().getDefaultDisplay().getWidth();
        gameView = (Chessboard) findViewById(R.id.snake);
        gameView.getLayoutParams().width = (int) (width * 1.18f);
        gameView.getLayoutParams().height = (int) (width * 1.18f);   
       gameView.requestLayout();
       btn_new = (Button) findViewById(R.id.newgame);
       btn_back = (Button) findViewById(R.id.huiqi);
       btn_xuhao = (Button) findViewById(R.id.xuhao);
       text_new = (TextView) findViewById(R.id.text_newgame);
       text_back = (TextView) findViewById(R.id.text_huiqi);
       text_xuhao = (TextView) findViewById(R.id.text_xuhao);
       OnClickListener newListener = new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			// TODO Auto-generated method stub
   			showSetDialog();
   		}
   	   };
   	   OnClickListener backListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			huiqi();
		}
	   };
	   OnClickListener xuhaoListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gameView.isXuhao()){
					gameView.setXuhao(false);
					gameView.invalidate();
				}else{
					gameView.setXuhao(true);
					gameView.invalidate();
				}
			}
		 };
       btn_back.setOnClickListener(backListener);
       btn_new.setOnClickListener(newListener);     
       btn_xuhao.setOnClickListener(xuhaoListener);
       text_new.setOnClickListener(newListener);
       text_back.setOnClickListener(backListener);
       text_xuhao.setOnClickListener(xuhaoListener);
    }
    
    private void initGobang(){
    	SharedPreferences set = getSharedPreferences("set", Activity.MODE_PRIVATE);
    	byte aiColor = 0;
    	if(set.getInt("xianshou", 0) == 0){
    		aiColor = GameStatus.BLACK;
    	}else{
    		aiColor = GameStatus.WHITE;
    	}
    	//层
    	int gx = 0;
    	int fx = 0;
    	int gy = 0;
    	int fy = 0;
    	boolean role = false;
    	boolean v = false;
    	boolean du = false;
    	boolean carnivore = false;
    	
    	if(set.getInt("jinshou", 0) == 0){
    		role = false;
    	}else{
    		role = true;
    	}
    	int level = set.getInt("qi", 0);
    	if(level == 0){
    		//稳健
    		gx = 3;
    		fx = 4;
    		gy = 4;
    		fy = 6;
    		if(aiColor == GameStatus.BLACK){
    			
        		
        		v = true;
        		du = false;
        		WeighValue.init(aiColor);
    		}else{
    		
    		
    			v = true;
    			du = false;
    			WeighValue.init(aiColor);
    		}
    		
    	}else if(level == 1){
    		//阴险
    		gx = 3;
    		fx = 2;
    		gy = 3;
    		fy = 6;
    		if(aiColor == GameStatus.BLACK){
    			
        		
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}else{
    		
        	
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}
    		
    	}else if(level == 2){
    		//中庸
    		gx = 3;
    		fx = 4;
    		gy = 5;
    		fy = 6;
    		if(aiColor == GameStatus.BLACK){
    		
        		
        		v = false;
        		du = false;
        		WeighValue.init(aiColor);
    		}else{
    			
        		
        		v = false;
        		du = false;
        		WeighValue.init(aiColor);
    		}
    		
    	}else if(level == 3){
    		//毒辣
    		gx = 3;
    		fx = 4;
    		gy = 4;
    		fy = 6;
    		if(aiColor == GameStatus.BLACK){
    	
        	
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}else{
    	
        	
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}
    		
    	}else if(level == 4){
    		//怪诞
    		gx = 1;
    		fx = 2;
    		gy = 5;
    		fy = 6;
    		if(aiColor == GameStatus.BLACK){
    	
        		v = true;
        		du = false;
        		WeighValue.init(aiColor);
    		}else{
    		
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}
    			
    	}else if(level == 5){
    		//正宗
    		carnivore = true;
    		VCTHelper.MAX_DEEP = 12;
    		gx = 3;
    		fx = 4;
    		gy = 5;
    		fy = 5;
    		if(aiColor == GameStatus.BLACK){
    		
        		v = true;
        		du = false;
        		WeighValue.init(aiColor);
    		}else{
    		
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}
    		
    	}else if(level == 6){
    		//食肉者
    		carnivore = true;
    		VCTHelper.MAX_DEEP = 18;
    		gx = 3;
    		fx = 4;
    		//gy = 5;
    		//fy = 6;
    		gy = 6;
    		fy = 5;
    		if(aiColor == GameStatus.BLACK){
        		v = true;
        		du = false;
        		
        		WeighValue.init(aiColor);
    		}else{
        		v = true;
        		du = true;
        		WeighValue.init(aiColor);
    		}
    		
    	}
    	
    	gobang = new Gobang(role, aiColor, gx, fx, gy, fy, v, du, carnivore);
		gobang.setMusic(new PlayMP3() {
					
					@Override
					public void playMp3() {					
						playSound(R.raw.click);
					}
				});
        gobang.setAiWinCallback(new AIWinCallback() {
			
			@Override
			public void AIWinCallback() {				
				playSound(R.raw.loss);
			   Toast.makeText(getApplicationContext(), "请继续加油哦!", Toast.LENGTH_SHORT).show();
			}
		});
        gobang.setPlayerWinCallback(new IPlayerWinCallback() {
			
			@Override
			public void playerWinCallback() {
				// TODO Auto-generated method stub
				
				Toast.makeText(getApplicationContext(), "恭喜您取得胜利!", Toast.LENGTH_SHORT).show();
			}
		});
		gameView.setGame(gobang);
        gameView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				gobang.begin();
			}
		}, 30);
    }
    private void newGame(){
    	if(gobang != null){
    		gobang.newGame();
    	}
    	
    	gameView.setGame(null);
    	gameView.invalidate();
    	initGobang();
    }
    
    private void huiqi(){
    	if(gobang.gameStatus.historyPoint.size() == 0){
    		return ;
    	}
    	
    	if(gobang.huiqiIndex < 2){
    		gobang.huiqiIndex++;
    		gobang.back();
    	}else{
    		Toast.makeText(getApplicationContext(), "最多悔棋2次!", Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
    private void showSetDialog(){
    	View view = getLayoutInflater().inflate(R.layout.set_layout, null);
    	final Spinner xianshou_spinner = (Spinner) view.findViewById(R.id.xianshou_spinner);
    	final Spinner jinshou_spinner = (Spinner) view.findViewById(R.id.jinshou_spinner);
    	final Spinner qi_spinner = (Spinner) view.findViewById(R.id.qi_spinner);
    	SharedPreferences set = getSharedPreferences("set", Activity.MODE_PRIVATE);
    	xianshou_spinner.setSelection(set.getInt("xianshou", 0));
    	jinshou_spinner.setSelection(set.getInt("jinshou", 0));
    	qi_spinner.setSelection(set.getInt("qi", 0));
    	alert = new AlertDialog.Builder(this).setTitle("新局")
    			             .setView(view)
    			             .setNegativeButton("取消", null)
    			             .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									alert.dismiss();
									SharedPreferences set = getSharedPreferences("set", Activity.MODE_PRIVATE);
									SharedPreferences.Editor editor = set.edit();
									editor.putInt("xianshou", xianshou_spinner.getSelectedItemPosition());
									editor.putInt("jinshou", jinshou_spinner.getSelectedItemPosition());
									editor.putInt("qi", qi_spinner.getSelectedItemPosition());
									editor.commit();
									newGame();
								}

								
							}).create();
	  alert.setCancelable(false);	  
	  alert.show();
	  
    }
    
    
    
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	initMp();
    }
    
                  @Override
                protected void onStop() {
                	// TODO Auto-generated method stub
                	super.onStop();
                	mp.stop();
                	mp.release();
                }
                  
                  @Override
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                	// TODO Auto-generated method stub
                	  if(keyCode == KeyEvent.KEYCODE_BACK){
                		  exitAlert();
                		  return true;
                	  }
                	return super.onKeyDown(keyCode, event);
                }
                  
             private void exitAlert(){
            	 alertExit = new AlertDialog.Builder(this).setTitle("退出")
			             .setNegativeButton("取消", null)
			             .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertExit.dismiss();
								MainActivity.this.finish();							
							}						
						}).create();
            	 alertExit.show();
             }
}