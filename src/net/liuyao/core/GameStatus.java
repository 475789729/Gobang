package net.liuyao.core;


import java.util.Vector;

public class GameStatus {
	/** 
	 * Ԫ���ݿ�
	 * */
   public final static byte  BLACK = 1;
   public final static byte  WHITE =-1;
   public final static byte  EMPTY = 0;
   //��һλ�Ǻ������������ң��ڶ�λ�������������ϵ���
   public byte[][] pointStatus;
   //���η�Χ�ü����߽��Լ��߽��ڲ�Ҫ���з���������Ŀ���
   public byte left = 0;
   public byte right = 14;
   public byte top = 0;
   public byte bottom = 14;
   
   public float[][][] pointBlackScore;
   public float[][][] pointWhiteScore;
   //��ǰ���ֵ㣬�����ǿ��ŵ�
   public Vector<byte[]> rolePoint;
   //˫���㣬��������Ч������������Ч����
   public Vector<byte[]> maybeRolePoint;
   //�����¼
   public Vector<byte[]> historyPoint;
   private GameController gameController;
   //forward�ı�winner��һ���ǿգ�������forward
   public byte winner = EMPTY;
   
   //��ɱ���ڵڼ�������
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
