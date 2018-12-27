package net.liuyao.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

public class Gobang {
     public GameStatus gameStatus;
     private Player player;
     private AI ai;
    
     private int grateSet = 3;
     private int frateSet = 3;
     public byte aiColor;
     public byte playerColor;
     private IPlayerWinCallback playerWinCallback;
     private AIWinCallback aiWinCallback;
     private PlayMP3 music;
     private IDrawChess drawChess;
     private boolean end = false;
     private  boolean backing = false;
     ExecutorService exec = Executors.newSingleThreadExecutor();
     
     private Handler mHandler;
     
     public static final int PLAYER_WIN_WHAT = 1;
     public static final int AI_WIN_WHAT = 2;
     public static final int DRAW_WHAT = 3;
     public int huiqiIndex = 0;
     private boolean duBegin = false;
    
     
     public Gobang(boolean role, byte aiColor,int gdeepSet, int fdeepset, int grateSet, int frateSet, boolean useVCT, boolean duBegin, boolean carnivore){
    	 gameStatus = new GameStatus();
    	 this.aiColor = aiColor;
    	 this.playerColor = (byte)(0 - aiColor);
    	 player = new Player(gameStatus, this.playerColor);
    	
    	 this.grateSet = grateSet;
    	 this.frateSet = frateSet;
    	 ai = new AI(gameStatus, grateSet, frateSet, gdeepSet, fdeepset, role, aiColor, useVCT, carnivore);
    	 this.duBegin = duBegin;
	     
  	   
    	 mHandler = new Handler(){
    		 @Override
    		public void handleMessage(Message msg) {
    			// TODO Auto-generated method stub
    			super.handleMessage(msg);
    			if(msg.what == PLAYER_WIN_WHAT){
    				playerWinCallback.playerWinCallback();
    			}else if(msg.what == AI_WIN_WHAT){
    				aiWinCallback.AIWinCallback();
    			}else if(msg.what == DRAW_WHAT){
    				drawChess.drawChess();
    			}
    				
    			
    		}
    	 };
    	 
    	
     }
     public void begin(){
    	 if(gameStatus.historyPoint.size() == 0){
    		 if(aiColor == GameStatus.BLACK){
    			 AIStep();
    		 }
    	 }
     }
     public void playerStep(final byte[] position){
    	 exec.execute(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				if(end){
					return ;
				}
				if(getTurn() != playerColor){
		    		 throwWrong();
		    	 }
		    	 if(gameStatus.pointStatus[position[0]][position[1]] == GameStatus.EMPTY){
		    		
		    		 
		    		 ai.forward(position, playerColor, AI.REAL);
		    		 if(gameStatus.historyPoint.size() == 1){
		    			 initRectangle();
		    		 }
		    		 if(drawChess != null){	    			 					    			 
		    			 mHandler.sendEmptyMessage(DRAW_WHAT);		    			
		    		 }
		    		 if(music != null){
		    			 music.playMp3();
		    		 }
		    		 if(gameStatus.winner == playerColor){
		    			 end = true;
		    			 if(playerWinCallback != null){		    				 
		    				 mHandler.sendEmptyMessage(PLAYER_WIN_WHAT);	    				 
		    			 }
		    		 }else if(gameStatus.winner == aiColor){
		    			 end = true;
		    			 if(aiWinCallback != null){
		    				 mHandler.sendEmptyMessage(AI_WIN_WHAT);
		    			 }
		    		 }
		    	 }else{
		    		 throwWrong();
		    	 }
			
			}
		}));
    	 
     }
     
     public  void AIStep(){
    	 exec.execute(new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(end){
					return ;
				}
				 if(getTurn() != aiColor){
		    		 throwWrong();
		    	 }
				 Step step = null;
				 if(aiColor == GameStatus.BLACK && gameStatus.historyPoint.size() == 0){
					 step = new Step();
					 step.position = new byte[]{7,7};
				 }else if(gameStatus.historyPoint.size() == 1 || gameStatus.historyPoint.size() == 2){
					 step = new Step();
					 if(duBegin){
						 step.position = BeginHelper.getDuStep(gameStatus.historyPoint);
					 }else{
						 step.position = BeginHelper.getStep(gameStatus.historyPoint);
					 }
					 
					 if(step.position == null){
						 step = ai.getBest();
					 }
						 
				 }else{
					//getBest很耗时间，如果在这期间用户点击悔棋，这里需要注意到
			    	 step = ai.getBest();
				 }
				 
		   
		    	 if(backing){
		    		 return ; 
		    	 }
		    	 ai.forward(step.position, aiColor, AI.REAL);
		    	 if(gameStatus.historyPoint.size() == 1){
	    			 initRectangle();
	    		 }
		    	 if(drawChess != null){		    		 
					 mHandler.sendEmptyMessage(DRAW_WHAT);
				 }
		    	 if(music != null){
	    			 music.playMp3();
	    		 }
		    	 if(gameStatus.winner == aiColor){
		    		 end = true;
		    		 if(aiWinCallback != null){
		    			 mHandler.sendEmptyMessage(AI_WIN_WHAT);
		    			 
		    		 }
		    	 }else if(gameStatus.winner == playerColor){
		    		 end = true;
		    		 if(playerWinCallback != null){
		    			 mHandler.sendEmptyMessage(PLAYER_WIN_WHAT);
		    		 }
		    	 }
			
			}
		}));
    	
    	 
     }
     
     public byte getTurn(){
    	 if(gameStatus.historyPoint.size() % 2 == 0){
    		 return GameStatus.BLACK;
    	 }else{
    		 return GameStatus.WHITE;
    	 }
     }
     //用户点击悔棋
     public  void back(){
    	 if(backing || gameStatus.historyPoint.size() == 0)
    		 return ; 
    	 
    	 end = false;
    	 backing = true;
    	 if(getTurn() == playerColor){
    		 if(gameStatus.historyPoint.size() == 1){
    			 return ;
    		 }
    		 ai.backward();
    		
    		 ai.backward();
    		   			
             mHandler.sendEmptyMessage(DRAW_WHAT);
    		 
    		 
    		 backing = false;
    		 
    	 }
    	 else if(getTurn() == aiColor){
    		 ai.setCallStop(true);
    		 exec.execute(new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub

					// TODO Auto-generated method stub
					ai.stopThink(); 					
		    		 ai.backward();
		    		 mHandler.sendEmptyMessage(DRAW_WHAT);
		    		 backing = false;
				
				}
			}));
    	 }
    	 
    	 
    	 
     }
     //用户点击新局
     public void newGame(){
    	 ai.setCallStop(true);
    	 //遗弃此类实例
     }
     
     
   
    
     private void initRectangle(){
    	 if(gameStatus.historyPoint.size() == 1){
			 gameStatus.getGameController().initRectangle(gameStatus.historyPoint.get(0));
		 }
     }
     
     private void throwWrong(){
    	 int i = 2 / 0;
     }
     
     public IPlayerWinCallback getPlayerWinCallback() {
		return playerWinCallback;
	}

	public void setPlayerWinCallback(IPlayerWinCallback playerWinCallback) {
		this.playerWinCallback = playerWinCallback;
	}

	public AIWinCallback getAiWinCallback() {
		return aiWinCallback;
	}

	public void setAiWinCallback(AIWinCallback aiWinCallback) {
		this.aiWinCallback = aiWinCallback;
	}

	public IDrawChess getDrawChess() {
		return drawChess;
	}

	public void setDrawChess(IDrawChess drawChess) {
		this.drawChess = drawChess;
	}
    public void setMusic(PlayMP3 music){
    	this.music = music;
    }
	public PlayMP3 getMusic(){
		return this.music;
	}
	
	public interface IPlayerWinCallback{
    	 public void playerWinCallback();
     }
     public interface AIWinCallback{
    	 public void AIWinCallback();
     }
     
     public interface IDrawChess{
    	 public void drawChess();
     }
     
     public interface PlayMP3{
    	 public void playMp3();
     }
     
   
     
}
