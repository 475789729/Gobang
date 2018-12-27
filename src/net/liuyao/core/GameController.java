package net.liuyao.core;

public class GameController {
	/** 
	 * 元数据库操作类
	 * */
   private GameStatus gameStatus;
   public GameController(GameStatus gameStatus){
	   this.gameStatus = gameStatus;
   }
   //第一步下棋之后的分析矩形划定   参数第一位是从左往右数，第二位是从上往下数
   //第一步之后需要调用，否则分析矩形始终是整个棋盘
   public void initRectangle(byte[] firstPosition){
	     gameStatus.left = (byte) ((firstPosition[0] - 2) >= 0 ?(firstPosition[0] - 2):0);
	     gameStatus.right = (byte) ((firstPosition[0] + 2) <= 14 ?(firstPosition[0] + 2):14);
	     gameStatus.top = (byte) ((firstPosition[1] - 2) >= 0 ?(firstPosition[1] - 2):0);
	     gameStatus.bottom = (byte) ((firstPosition[1] + 2) <= 14 ?(firstPosition[1] + 2):14);
		   
	   
   }
   //之后每一步的分析矩形划定。  参数第一位是左往右，第二位是从上往下
   public void adjustRectangle(byte[] position){
	   if(position[0] - 2 < gameStatus.left){
		   gameStatus.left = (byte) ((position[0] - 2) >= 0?(position[0] - 2):0);
	   }else if(position[0] + 2 > gameStatus.right){
		   gameStatus.right = (byte) ((position[0] + 2) <= 14?(position[0] + 2):14);
	   }
	   if(position[1] - 2 < gameStatus.top){
		   gameStatus.top = (byte) ((position[1] - 2) >= 0?(position[1] - 2):0);
	   }else if(position[1] + 2 > gameStatus.bottom){
		   gameStatus.bottom = (byte) ((position[1] + 2) <=14?(position[1] + 2):14);
	   }
   }
   
   
   public void adjustRectangle(){
	   if(gameStatus.historyPoint.size() == 0){
		   gameStatus.left = 0;
		   gameStatus.right = 14;
		   gameStatus.top = 0;
		   gameStatus.bottom = 14;
		   return ;
	   }
	   gameStatus.left = 14;
	   gameStatus.right = 0;
	   gameStatus.top = 14;
	   gameStatus.bottom = 0;
	   if(gameStatus.pointStatus[6][7] != gameStatus.EMPTY || gameStatus.pointStatus[7][7] != gameStatus.EMPTY || gameStatus.pointStatus[8][7] != gameStatus.EMPTY){
		   gameStatus.top = 5;
		   gameStatus.bottom = 9;
	   }
	   if(gameStatus.pointStatus[7][6] != gameStatus.EMPTY || gameStatus.pointStatus[7][7] != gameStatus.EMPTY || gameStatus.pointStatus[7][8] != gameStatus.EMPTY){
		   gameStatus.left = 5;
		   gameStatus.right = 9;
	   }
	   for(int i = 0 ; i < 15; i++){
		   for(int j = 0; j - 2 < gameStatus.top && j < 14; j++){
			   if(gameStatus.pointStatus[i][j] != gameStatus.EMPTY && j - 2 < gameStatus.top){
				   gameStatus.top = (byte) (j - 2 >= 0? j - 2 : 0);
				   break;
			   }
		   }
		   if(gameStatus.top == 0){
			   break;
		   }
	   }
	   for(int i = 0; i < 15; i++){
		   for(int j = 14; j + 2 > gameStatus.bottom && j > 0; j--){
			   if(gameStatus.pointStatus[i][j] != gameStatus.EMPTY && j + 2 > gameStatus.bottom){
				   gameStatus.bottom = (byte) (j + 2 <= 14? j + 2 : 14);
				   break;
			   }
		   }
		   if(gameStatus.bottom == 14){
			   break;
		   }
	   }
	   
	   for(int j = 0; j < 15; j++){
		   for(int i = 0; i - 2 < gameStatus.left && i < 14; i++){
			   if(gameStatus.pointStatus[i][j] != gameStatus.EMPTY && i - 2 <gameStatus.left){
				   gameStatus.left = (byte) (i - 2 >= 0 ? i - 2 : 0);
				   break;
			   }
			   
		   }
		   if(gameStatus.left == 0){
			   break;
		   }
	   }
	   
	   for(int j = 0 ; j < 15; j++){
		   for(int i = 14; i + 2 > gameStatus.right && i > 0; i--){
			   if(gameStatus.pointStatus[i][j] != gameStatus.EMPTY && i + 2 > gameStatus.right){
				   gameStatus.right = (byte) (i + 2 <= 14 ? i + 2 : 14);
				   break;
			   }
		   }
		   
		   if(gameStatus.right == 14){
			   break;
		   }
	   }
   }
   


   
   
   public void throwWrong(){
	   int a = 1/0;
   }
}
