package net.liuyao.core;


import java.util.Vector;

public class GameStatus {
	/** 
	 * 元数据库
	 * */
   public final static byte  BLACK = 1;
   public final static byte  WHITE =-1;
   public final static byte  EMPTY = 0;
   //第一位是横着数，从左到右，第二位是竖着数，从上到下
   public byte[][] pointStatus;
   //矩形范围裁剪，边界以及边界内部要进行分析，下棋的可能
   public byte left = 0;
   public byte right = 14;
   public byte top = 0;
   public byte bottom = 14;
   
   public float[][][] pointBlackScore;
   public float[][][] pointWhiteScore;
   //当前禁手点，并且是空着的
   public Vector<byte[]> rolePoint;
   //双三点，不管是有效活三，还是无效活三
   public Vector<byte[]> maybeRolePoint;
   //行棋记录
   public Vector<byte[]> historyPoint;
   private GameController gameController;
   //forward改变winner，一旦非空，则不能再forward
   public byte winner = EMPTY;
   
   //绝杀点在第几步出现
   public int black_DoubleThree = -1;
   public int white_DoubleThree = -1;
   public int black_DoubleFour = -1;
   public int white_DoubleFour = -1;
   
   
   public GameStatus(){
	   setGameController(new GameController(this));
	   pointStatus = new byte[15][15];
	   for(int i = 0; i < 15; i++){
		   for(int j = 0; j < 15; j++){			 		   
			   pointStatus[i][j] = EMPTY;
		   }
	   }
	   pointBlackScore = new float[15][15][2];
	   pointWhiteScore = new float[15][15][2];
	   
	   rolePoint = new Vector<byte[]>();
	   maybeRolePoint = new Vector<byte[]>();
	   historyPoint = new Vector<byte[]>();
	   
   }
	//top,right,bottom,left
   public int[] meaSureBorder(byte[] position){
	   int[] border = new int[4];
	   border[0] = top;
	   border[1] = right;
	   border[2] = bottom;
	   border[3] = left;
	   if(position[0] - 2 < left){
		   border[3] = (position[0] - 2) >= 0?(position[0] - 2):0;
	   }else if(position[0] + 2 > right){
		   border[1] = (position[0] + 2) <= 14?(position[0] + 2):14;
	   }
	   if(position[1] - 2 < top){
		   border[0] = (position[1] - 2) >= 0?(position[1] - 2):0;
	   }else if(position[1] + 2 > bottom){
		   border[2] = (position[1] + 2) <=14?(position[1] + 2):14;
	   }
	   return border;
   }
public GameController getGameController() {
	return gameController;
}
public void setGameController(GameController gameController) {
	this.gameController = gameController;
}
}
